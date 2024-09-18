package command;

import args.Raw;
import args.Rawable;
import core.RedisContext;
import core.RespParser;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Queue;

public class InfoCommand extends RedisCommand {


    @Override
    public Queue<ByteBuffer> execute(final List<String> args, final RedisContext redisContext) {

        // assume for now that INFO always includes "replication"
        System.out.println(args);

        List<String> replicationInfo = redisContext.getReplication().getReplicationInfo();
        StringBuilder sb = new StringBuilder();
        for (String s : replicationInfo) {
            sb.append(s).append('\n');
        }
        return this.queue(RespParser.fromBulk(sb.toString()));

    }

}
