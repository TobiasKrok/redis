package command;

import args.Raw;
import args.Rawable;
import core.RedisContext;
import core.RespParser;

import java.nio.ByteBuffer;
import java.util.List;

public class InfoCommand extends RedisCommand{


    @Override
    public Rawable execute(final List<String> args, final RedisContext redisContext) {

        // assume for now that INFO always includes "replication"

        if(args.getFirst().equalsIgnoreCase("replication")) {
            List<String> replicationInfo = redisContext.getReplication().getReplicationInfo();
            ByteBuffer bb = ByteBuffer.allocate(256);
            for (String s : replicationInfo) {
                bb.put(RespParser.fromBulk(s));
            }
            return new Raw(bb.array());
        }
        // temp
        return new Raw(RespParser.fromSimple("OK"));
        }
}
