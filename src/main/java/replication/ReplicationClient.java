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
    private volatile boolean handShakeCompleted = false;

    private SocketChannel channel;

    private final Object lock = new Object();

    public ReplicationClient(int port, String masterHost, int masterPort) {
        this.port = port;
        this.masterHost = masterHost;
        this.masterPort = masterPort;
    }

    @Override
    public void run()  {
        try {
            System.out.println("running!");
            if(!handShakeCompleted) {
                synchronized (lock) {
                    if(channel == null) {
                        channel = SocketChannel.open();
                        channel.connect(new InetSocketAddress(masterHost, masterPort));
                    }
                    initiateHandShake(channel);
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

    }
    public void startReplicationServer() {
        try(SocketChannel channel = SocketChannel.open()) {
            channel.connect(new InetSocketAddress(masterHost, masterPort));
            initiateHandShake(channel);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private void initiateHandShake(SocketChannel channel) throws IOException {
        System.out.println("Initiating handshake...");
        channel.write(ByteBuffer.wrap(RespParser.fromArray(List.of("PING"))));
        System.out.println("Sent PING to master");
        ByteBuffer buffer = ByteBuffer.allocate(256);
        channel.read(buffer); // blocking
        buffer.flip();
        String res = RespParser.read(buffer).getFirst();
        if(!res.equalsIgnoreCase("pong")) throw new RuntimeException("Couldn't contact master");


        channel.write(ByteBuffer.wrap(RespParser.fromArray(List.of("replconf", "listening-port", String.valueOf(port)))));
        channel.write(ByteBuffer.wrap(RespParser.fromArray(List.of("replconf", "capa", "psync2"))));
    }
}
