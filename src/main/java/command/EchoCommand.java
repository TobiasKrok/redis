package command;

import args.Raw;
import args.Rawable;
import core.RedisContext;
import core.RespParser;

import java.util.List;

public class EchoCommand  extends RedisCommand{

    @Override
    public Rawable execute(final List<String> args, final RedisContext redisContext) {

        if(args.isEmpty()) {
            return new Raw(RespParser.fromSimpleError("ERR", "no string provided"));
        }

        System.out.println(args);
        return new Raw(RespParser.fromBulk(args.getFirst()));
    }
}
