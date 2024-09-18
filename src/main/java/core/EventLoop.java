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

class EventLoop {

    private static final int BUFFER_SIZE = 256;  // Use a constant for buffer size

    private final RedisCommandHandler commandHandler;
    private final boolean stop = false;

    public EventLoop(RedisCommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    protected void run(int port) throws IOException {
        try (ServerSocketChannel serverChannel = ServerSocketChannel.open()) {
            serverChannel.bind(new InetSocketAddress("localhost", port));
            serverChannel.configureBlocking(false);
            Selector selector = Selector.open();
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);

            while (!stop) {
                selector.select();  // Wait for events

                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    keyIterator.remove();  // Remove key to avoid reprocessing

                    try {
                        if (key.isAcceptable()) {
                            handleAccept(key, selector);
                        } else if (key.isReadable()) {
                            handleRead(key, selector);
                        } else if (key.isWritable()) {
                            handleWrite(key, selector);
                        }
                    } catch (IOException e) {
                        key.cancel();  // Cancel the key in case of errors
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

        RedisClient redisClient = new RedisClient(clientChannel, 256);
        clientChannel.register(selector, SelectionKey.OP_READ, redisClient);

        System.out.println("redis: registered client " + clientChannel.getRemoteAddress());
    }

    // Handle client data read
    private void handleRead(SelectionKey key, Selector selector) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        RedisClient redisClient = (RedisClient) key.attachment();

        ByteBuffer buffer = redisClient.getBuffer();  // Use a reusable buffer
        int bytesRead = clientChannel.read(buffer);

        if (bytesRead == -1) {
            System.out.println("redis: recv -1 bytes, closing client connection...");
            clientChannel.close();
            return;
        }

        System.out.printf("redis: recv %s bytes from client " + clientChannel.getRemoteAddress() + "%n", bytesRead);
        buffer.flip();

        List<String> rawCommands = RespParser.read(buffer);
        boolean hasWriteable = commandHandler.process(rawCommands, redisClient);

        if (hasWriteable) {
            clientChannel.register(selector, SelectionKey.OP_WRITE, redisClient);
        }

        buffer.clear();  // Clear buffer for next read
    }

    // Handle client data write
    private void handleWrite(SelectionKey key, Selector selector) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        RedisClient redisClient = (RedisClient) key.attachment();

        redisClient.writeToClient();  // Write queued data

        // If all data has been written, switch back to read mode
        if (!redisClient.hasPendingWrites()) {
            clientChannel.register(selector, SelectionKey.OP_READ, redisClient);
        }
    }
}
