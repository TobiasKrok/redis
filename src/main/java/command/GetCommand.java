package command;

import args.Raw;
import args.Rawable;
import core.RedisContext;
import core.RespParser;

import java.util.List;

public class GetCommand extends RedisCommand{
    @Override
    public Rawable execute(List<String> args, RedisContext redisContext) {
        if(args.isEmpty()) {
            return new Raw(RespParser.fromSimpleError("ERR", "missing arguments"));
        }

        if(!redisContext.getStore().containsKey(args.getFirst())) {
            return new Raw(RespParser.fromBulk(null));
        }
        return new Raw(RespParser.fromBulk((String)redisContext.getStore().get(args.getFirst())));
    }
}
