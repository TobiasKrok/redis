package core;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.Queue;

public class RedisClient {
    private final SocketChannel clientChannel;
    private final Queue<ByteBuffer> writeQueue = new LinkedList<>();

    private ByteBuffer buffer;

    public RedisClient(SocketChannel clientChannel, int bufferCap) {
        this.clientChannel = clientChannel;
        this.buffer = ByteBuffer.allocate(bufferCap);
    }

    // Add data to the write queue
    public void queueData(byte[] data) {
        this.writeQueue.add(ByteBuffer.wrap(data));
    }

    public void queueData(Queue<ByteBuffer> queue) {
        this.writeQueue.addAll(queue);
    }
    public void queueData(ByteBuffer buffer) {
        this.writeQueue.add(buffer);
    }
    protected boolean hasPendingWrites() {
        return !writeQueue.isEmpty();
    }

    protected ByteBuffer getBuffer() {
        return this.buffer;
    }

    // Send next part of data
    protected void writeToClient() throws IOException {
        ByteBuffer buffer = writeQueue.peek();  // Get the next buffer
        if(buffer == null) return;

        clientChannel.write(buffer);  // Write to the client

        if (!buffer.hasRemaining()) {
            writeQueue.poll();  // Remove buffer if fully written
        }
    }
}
