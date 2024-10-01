package replication;

import command.RedisCommand;
import core.RedisClient;
import core.RedisCommandHandler;
import core.RespParser;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class ReplicationClient implements Runnable {

    private final int port;
    private final String masterHost;
    private final int masterPort;
    private SocketChannel channel;

    private BlockingQueue<ByteBuffer> writeQueue;

    private volatile boolean handShakeCompleted = false;

    private final RedisCommandHandler commandHandler;


    public ReplicationClient(int port, String masterHost, int masterPort, RedisCommandHandler commandHandler) {
        this.port = port;
        this.masterHost = masterHost;
        this.masterPort = masterPort;
        this.writeQueue = new LinkedBlockingQueue<>();
        this.commandHandler = commandHandler;
    }

    @Override
    public void run() {
        try {


            channel = SocketChannel.open();
            channel.connect(new InetSocketAddress(masterHost, masterPort));
            initiateHandShake(channel);
            ByteBuffer buffer = ByteBuffer.allocate(256);
            while (!Thread.interrupted()) {
                List<String> commands = read(buffer);

            }
        } catch (Exception e) {

            System.err.println(e);
        } finally {
            try {
                System.out.println("redis: closing replica conn");
                channel.close();
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }

    }

    private void initiateHandShake(SocketChannel channel) throws IOException {
        System.out.println("Initiating handshake...");
        channel.write(ByteBuffer.wrap(RespParser.fromArray(List.of("PING"))));
        System.out.println("Sent PING to master");
        ByteBuffer buffer = ByteBuffer.allocate(256);
        List<String> res = read(buffer);
        //if (!res.equalsIgnoreCase("pong")) throw new RuntimeException("Couldn't contact master");

        // replconf
        channel.write(ByteBuffer.wrap(RespParser.fromArray(List.of("replconf", "listening-port", String.valueOf(port)))));
        if(!read(buffer).getFirst().equalsIgnoreCase("OK"))
            throw new RuntimeException("redis: repl master did not accept our port binding");

        channel.write(ByteBuffer.wrap(RespParser.fromArray(List.of("replconf", "capa", "psync2"))));
        if(!read(buffer).getFirst().equalsIgnoreCase("OK"))
            throw new RuntimeException("redis: repl master did not accept our capa sync");


            // psync - ? is the replication ID which we do not know yet, and the default offset is -1 when connecting for the first time.
        channel.write(ByteBuffer.wrap(RespParser.fromArray(List.of("PSYNC", "?", "-1"))));

        System.out.println("redis: repl handshake completed");
        handShakeCompleted = true;
    }

    protected boolean offer(ByteBuffer b) throws IOException {
       return writeQueue.offer(b);
    }

    private List<String> read(ByteBuffer buffer) throws IOException {
        buffer.clear();
        channel.read(buffer);
        buffer.flip();

        return RespParser.read(buffer);
    }
}
