package command;

import args.Raw;
import args.Rawable;
import core.RedisContext;
import core.RespParser;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Queue;

public class EchoCommand  extends RedisCommand {

    @Override
    public Queue<ByteBuffer> execute(final List<String> args, final RedisContext redisContext) {

        if(args.isEmpty()) {
            return this.queue(RespParser.fromSimpleError("ERR", "no string provided"));
        }
        return this.queue(RespParser.fromBulk(args.getFirst()));
    }
}
