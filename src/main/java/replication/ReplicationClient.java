package replication;

import core.RespParser;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.List;

public class ReplicationClient implements Runnable {

    private final int port;
    private final String masterHost;
    private final int masterPort;
    private final Object lock = new Object();
    private volatile boolean handShakeCompleted = false;
    private volatile boolean handShakeInProgress = false;
    private SocketChannel channel;

    public ReplicationClient(int port, String masterHost, int masterPort) {
        this.port = port;
        this.masterHost = masterHost;
        this.masterPort = masterPort;
    }

    @Override
    public void run() {
        try {
            synchronized (lock) {
                if (channel == null) {
                    channel = SocketChannel.open();
                    channel.connect(new InetSocketAddress(masterHost, masterPort));
                }
                if (!handShakeCompleted && !handShakeInProgress) {
                    handShakeInProgress = true;
                    initiateHandShake(channel);
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

    }

    private void initiateHandShake(SocketChannel channel) throws IOException {
        System.out.println("Initiating handshake...");
        channel.write(ByteBuffer.wrap(RespParser.fromArray(List.of("PING"))));
        System.out.println("Sent PING to master");
        ByteBuffer buffer = ByteBuffer.allocate(256);
        String res = read(channel, buffer).getFirst();
        if (!res.equalsIgnoreCase("pong")) throw new RuntimeException("Couldn't contact master");

        // replconf
        channel.write(ByteBuffer.wrap(RespParser.fromArray(List.of("replconf", "listening-port", String.valueOf(port)))));
        if(!read(channel, buffer).getFirst().equalsIgnoreCase("OK"))
            throw new RuntimeException("redis: repl master did not accept our port binding");

        channel.write(ByteBuffer.wrap(RespParser.fromArray(List.of("replconf", "capa", "psync2"))));
        if(!read(channel, buffer).getFirst().equalsIgnoreCase("OK"))
            throw new RuntimeException("redis: repl master did not accept our capa sync");


            // psync - ? is the replication ID which we do not know yet, and the default offset is -1 when connecting for the first time.
        channel.write(ByteBuffer.wrap(RespParser.fromArray(List.of("PSYNC", "?", "-1"))));

        handShakeCompleted = true;

    }

    private List<String> read(SocketChannel channel, ByteBuffer buffer) throws IOException {
        buffer.clear();
        channel.read(buffer);
        buffer.flip();

        return RespParser.read(buffer);
    }
}
