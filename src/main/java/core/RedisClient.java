package core;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RedisClient {
    private final SocketChannel clientChannel;
    private final ConcurrentLinkedQueue<ByteBuffer> writeQueue = new ConcurrentLinkedQueue<>();

    // Callback for other classes to mark this client as readable, can this mess up the event loop?

    private ByteBuffer buffer;

    private final String id = UUID.randomUUID().toString();

    private final Selector selector;
    public RedisClient(SocketChannel clientChannel, Selector selector, int bufferCap) {
        this.clientChannel = clientChannel;
        this.buffer = ByteBuffer.allocate(bufferCap);
        this.selector = selector;
    }

    public String getId() {
        return id;
    }

    private void registerWrite() {
        SelectionKey key = clientChannel.keyFor(selector);
        if(key != null) {
            key.interestOps(SelectionKey.OP_WRITE);
            System.out.println("redis: client " + id + " registered as writeable");

            //key.attach()
        }
    }

    public void queueData(byte[] data) {
        this.writeQueue.add(ByteBuffer.wrap(data));
        registerWrite();

    }

    public void queueData(Queue<ByteBuffer> queue) {
        // todo dont think this is atomic
        this.writeQueue.addAll(queue);
        registerWrite();
    }
    public void queueData(ByteBuffer buffer) {
        this.writeQueue.add(buffer);
        registerWrite();

    }
    protected boolean hasPendingWrites() {
        return !writeQueue.isEmpty();
    }

    protected ByteBuffer getBuffer() {
        return this.buffer;
    }

    protected void writeToClient() throws IOException {
        ByteBuffer buffer = writeQueue.poll();
        if(buffer == null) return;;
        clientChannel.write(buffer);

    }
}
