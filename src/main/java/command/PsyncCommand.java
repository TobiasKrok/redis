package command;

import args.Raw;
import args.Rawable;
import core.RedisContext;
import core.RespParser;

import java.util.List;

public class PsyncCommand extends RedisCommand {

    @Override
    public Rawable execute(List<String> args, RedisContext redisContext) {

        return new Raw(RespParser.fromSimple("FULLRESYNC " + redisContext.getReplication().getId() + " " + redisContext.getReplication().getOffset()));
    }
}
