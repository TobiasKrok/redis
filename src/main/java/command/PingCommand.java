package command;

import args.Raw;
import args.Rawable;
import core.RedisContext;
import core.RespParser;

import java.util.List;

public class PingCommand extends RedisCommand{
    @Override
    public Rawable execute(final List<String> args, final RedisContext redisContext) {

        return new Raw(RespParser.fromBulk("PONG"));
    }

}
