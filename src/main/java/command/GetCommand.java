package command;

import core.RedisClient;
import core.RedisContext;
import core.RespParser;

import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.List;
import java.util.Queue;

public class GetCommand extends RedisCommand {
    @Override
    public Queue<ByteBuffer> execute(final List<String> args, List<String> rawArgs, RedisClient redisClient, final RedisContext redisContext) {
        if (args.isEmpty()) {
            return this.queue(RespParser.fromSimpleError("ERR", "missing arguments"));
        }

        String key = args.getFirst();

        // Synchronized block or use of ConcurrentHashMap
            if (redisContext.getStore().get(key) == null) {
                return this.queue(RespParser.fromBulk(null));
            }

            redisContext.getExpiration().computeIfPresent(key, (k, expiryTime) -> {
                long now = Instant.now().toEpochMilli();
                if (expiryTime < now) {
                    redisContext.getStore().remove(k);
                    System.out.println("redis: removed key '" + key + "' because it has expired");
                    return null;
                }
                return expiryTime;
            });


        Object value = redisContext.getStore().get(key);
        if (value == null) {
            return this.queue(RespParser.fromBulk(null));
        }

        return this.queue(RespParser.fromBulk((String) value));
    }
}
