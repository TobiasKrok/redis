package command;

import args.Raw;
import args.Rawable;
import core.RedisContext;
import core.RespParser;

import java.util.List;

public class SetCommand extends RedisCommand {
    @Override
    public Rawable execute(List<String> args, RedisContext redisContext) {
        if(args.size() < 2) {
            return new Raw(RespParser.fromSimpleError("ERR", "missing arguments"));
        }
        redisContext.getStore().put(args.getFirst(), args.get(1));
        return new Raw(RespParser.fromSimple("OK"));

    }
}
