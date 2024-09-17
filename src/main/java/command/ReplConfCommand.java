package command;

import args.Raw;
import args.Rawable;
import core.RedisContext;
import core.RespParser;

import java.util.List;

public class ReplConfCommand extends RedisCommand{

    @Override
    public Rawable execute(List<String> args, RedisContext redisContext) {
        // ignoring this for now
        return new Raw(RespParser.fromSimple("OK"));
    }
}
