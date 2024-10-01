package command;

import core.RedisClient;
import core.RedisContext;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public abstract class RedisCommand {


    private final Queue<ByteBuffer> queue = new LinkedList<>();

    public abstract Queue<ByteBuffer> execute(final List<String> args, List<String> rawArgs, RedisClient redisClient, final RedisContext redisContext);

    protected Queue<ByteBuffer> queue(final List<byte[]> out) {
        for (byte[] b : out) {
            queue.add(ByteBuffer.wrap(b));
        }

        return queue;
    }

    protected Queue<ByteBuffer> queue(byte[] out) {
        queue.add(ByteBuffer.wrap(out));
        return queue;
    }

    protected Queue<ByteBuffer> queue() {
    return queue;
    }



    }
