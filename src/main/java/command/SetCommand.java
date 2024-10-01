package command;

import core.RedisClient;
import core.RedisContext;
import core.RespParser;

import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.List;
import java.util.Queue;

public class SetCommand extends PropagatedRedisCommand {

    @Override
    public Queue<ByteBuffer> propagatedExecute(final List<String> args, List<String> rawArgs, RedisClient redisClient, final RedisContext redisContext) {
        if(args.size() < 2) {
            return this.queue(RespParser.fromSimpleError("ERR", "missing arguments"));
        }

        // check rest of the commands
        for (int i = 2; i < args.size(); i++) {
            String arg = args.get(i);
            if(arg.equalsIgnoreCase("px")) {
                if((i + 1) > args.size()) {
                    throw new RuntimeException("SET px is missing argument");
                }
                long expiry = Instant.now().toEpochMilli() + Integer.parseInt(args.get(i + 1));
                redisContext.getExpiration().compute(args.getFirst(), (k, v) -> expiry);
            }
        }
        redisContext.getStore().compute(args.getFirst(), (k, v) -> args.get(1));
        return this.queue(RespParser.fromSimple("OK"));

    }
}
