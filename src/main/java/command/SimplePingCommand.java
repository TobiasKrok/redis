package command;

import args.Raw;
import args.Rawable;
import core.RedisContext;
import core.RespParser;

import java.util.List;

public class SimplePingCommand extends RedisCommand {

    @Override
    public Rawable execute(final List<String> args, final RedisContext redisContext) {

        return new Raw(RespParser.fromSimple("PONG"));
    }

}
