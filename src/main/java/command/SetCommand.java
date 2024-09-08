package command;

import args.Raw;
import args.Rawable;
import core.RedisContext;
import core.RespParser;

import java.time.Instant;
import java.util.List;

public class SetCommand extends RedisCommand {
    @Override
    public Rawable execute(final List<String> args, final RedisContext redisContext) {
        if(args.size() < 2) {
            return new Raw(RespParser.fromSimpleError("ERR", "missing arguments"));
        }

        // check rest of the commands
        for (int i = 2; i < args.size(); i++) {
            String arg = args.get(i);
            if(arg.equalsIgnoreCase("px")) {
                if((i + 1) > args.size()) {
                    throw new RuntimeException("SET px is missing argument");
                }
                long expiry = Instant.now().toEpochMilli() + Integer.parseInt(args.get(i + 1));
                redisContext.getExpiration().put(args.getFirst(), expiry);
            }
        }
        redisContext.getStore().put(args.getFirst(), args.get(1));
        return new Raw(RespParser.fromSimple("OK"));

    }
}
