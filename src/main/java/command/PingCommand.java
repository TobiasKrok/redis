package command;

import args.Raw;
import args.RawString;
import args.Rawable;
import core.RedisContext;
import core.RespParser;

import java.util.List;

public class PingCommand extends RedisCommand {

    @Override
    public Rawable execute(List<String> args, RedisContext redisContext) {

        return new Raw(RespParser.fromSimple("PONG"));
    }

}
