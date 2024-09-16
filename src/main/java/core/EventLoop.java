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

    private final RedisCommandHandler commandHandler;
    private final boolean stop = false;

    public EventLoop(RedisCommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    protected void run(int port) throws IOException {
        try (ServerSocketChannel ssc = ServerSocketChannel.open()) {
            ssc.bind(new InetSocketAddress("localhost", port));
            ssc.configureBlocking(false);
            Selector selector = Selector.open();
            ssc.register(selector, SelectionKey.OP_ACCEPT);

            while (!stop) {
                selector.select();
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    if (key.isAcceptable()) {
                        // accpets a new client and say that we're interested in reading from that client
                        ServerSocketChannel server = (ServerSocketChannel) key.channel();
                        SocketChannel client = server.accept();
                        client.configureBlocking(false);
                        client.register(selector, SelectionKey.OP_READ);
                        System.out.println("redis: registered client " + client.getRemoteAddress());
                    } else if (key.isReadable()) {
                        // we got some data from a client that we are interested in and now we read
                        SocketChannel client = (SocketChannel) key.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(256);
                        int bytesRead = client.read(buffer);
                        System.out.printf("redis: recv %s bytes from client " + client.getRemoteAddress() + "%n", bytesRead);
                        if (bytesRead == -1) {
                            client.close();
                        } else {
                            buffer.flip();
                            List<String> rawCommands = RespParser.read(buffer);
                            byte[] res = commandHandler.process(rawCommands);
                            client.write(ByteBuffer.wrap(res));
                        }

                    }
                    keyIterator.remove();
                }
            }
        }
    }
}
