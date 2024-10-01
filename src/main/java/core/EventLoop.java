package core;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

class EventLoop {

    private static final int BUFFER_SIZE = 256;  // Use a constant for buffer size

    private final RedisCommandHandler commandHandler;
    private final boolean stop = false;

    private final ConcurrentHashMap<String, RedisClient> connectedClients;

    public EventLoop(final RedisCommandHandler commandHandler, final ConcurrentHashMap<String, RedisClient> connectedClients) {
        this.commandHandler = commandHandler;
        this.connectedClients = connectedClients;
    }

    protected void run(int port) throws IOException {
        try (ServerSocketChannel serverChannel = ServerSocketChannel.open()) {
            serverChannel.bind(new InetSocketAddress("localhost", port));
            serverChannel.configureBlocking(false);
            Selector selector = Selector.open();
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);

            while (!stop) {

                selector.select();  // blocks

                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    keyIterator.remove();

                    try {
                        if (key.isAcceptable()) {
                            handleAccept(key, selector);
                        } else if (key.isReadable()) {
                            handleRead(key, selector);
                        } else if (key.isWritable()) {
                            handleWrite(key, selector);
                        }
                    } catch (IOException e) {
                        key.cancel();
                        key.channel().close();
                        System.err.println("Connection closed due to error: " + e.getMessage());
                    }
                }
            }
        }
    }

    // Handle client connection accept
    private void handleAccept(SelectionKey key, Selector selector) throws IOException {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel clientChannel = serverChannel.accept();
        clientChannel.configureBlocking(false);

        RedisClient redisClient = new RedisClient(clientChannel, selector, 256);
        connectedClients.putIfAbsent(redisClient.getId(), redisClient);
        clientChannel.register(selector, SelectionKey.OP_READ, redisClient);
        System.out.println("redis: registered client " + clientChannel.getRemoteAddress());


    }

    // Handle client data read
    private void handleRead(SelectionKey key, Selector selector) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        RedisClient redisClient = (RedisClient) key.attachment();

        ByteBuffer buffer = redisClient.getBuffer();
        int bytesRead = clientChannel.read(buffer);
        if (bytesRead == -1) {
            System.out.println("redis: recv -1 bytes, closing client connection for client " + redisClient.getId());
            connectedClients.computeIfPresent(redisClient.getId(), (k, v) -> null);
            clientChannel.close();
            return;
        }

        System.out.printf("redis: recv %s bytes from client " + clientChannel.getRemoteAddress() + "%n", bytesRead);
        buffer.flip();

        List<String> rawCommands = RespParser.read(buffer);
        commandHandler.process(rawCommands, redisClient);
        buffer.clear();  // Clear buffer for next read
    }

    // Handle client data write
    private void handleWrite(SelectionKey key, Selector selector) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        RedisClient redisClient = (RedisClient) key.attachment();

        redisClient.writeToClient();  // Write queued data

        // If all data has been written, switch back to read mode
        if (!redisClient.hasPendingWrites()) {
            System.out.println(redisClient.getId() + " has no writable");
            key.interestOps(SelectionKey.OP_READ);
        }
    }
}
