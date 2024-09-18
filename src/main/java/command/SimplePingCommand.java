package command;

import args.Raw;
import args.Rawable;
import core.RedisContext;
import core.RespParser;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Queue;

public class SimplePingCommand extends RedisCommand {

    @Override
    public Queue<ByteBuffer> execute(final List<String> args, final RedisContext redisContext) {

        return this.queue(RespParser.fromSimple("PONG"));
    }

}
