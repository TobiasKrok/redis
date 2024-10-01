package command;

import core.RedisClient;
import core.RedisContext;
import core.RespParser;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Queue;

public abstract class PropagatedRedisCommand extends RedisCommand {

    @Override
    public final Queue<ByteBuffer> execute(List<String> args, List<String> rawArgs,
                                           RedisClient redisClient, RedisContext redisContext) {
        System.out.println("redis: propagating to replicas, command: " + rawArgs.toString());

        redisContext.getReplication().propagate(rawArgs);
        return propagatedExecute(args, rawArgs, redisClient, redisContext);
    }

    protected abstract Queue<ByteBuffer> propagatedExecute(List<String> args, List<String> rawArgs,
                                                   RedisClient redisClient, RedisContext redisContext);
}
