package command;

import args.Raw;
import args.Rawable;
import core.RedisContext;
import core.RespParser;

import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.List;
import java.util.Queue;

public class GetCommand extends RedisCommand{
    @Override
    public Queue<ByteBuffer> execute(final List<String> args, final RedisContext redisContext) {
        if(args.isEmpty()) {
            return this.queue(RespParser.fromSimpleError("ERR", "missing arguments"));
        }

        String key = args.getFirst();

        if(!redisContext.getStore().containsKey(key)) {
            return this.queue(RespParser.fromBulk(null));
        }

        if(redisContext.getExpiration().containsKey(key)) {
            long now = Instant.now().toEpochMilli();
            if(redisContext.getExpiration().get(key) < now) {
                redisContext.getExpiration().remove(key);
                System.out.println("redis: removed key '" + key + "' because it has expired");
                return this.queue(RespParser.fromBulk((null)));

            }
        }


        //TODO casts to string currently, good/bad?
        return this.queue(RespParser.fromBulk((String)redisContext.getStore().get(key)));
    }
}
