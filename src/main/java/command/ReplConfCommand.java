package command;

import core.RedisClient;
import core.RedisContext;
import core.RespParser;
import replication.ReplicationClient;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Queue;

public class ReplConfCommand extends RedisCommand{

    @Override
    public Queue<ByteBuffer> execute(List<String> args, List<String> rawArgs, RedisClient redisClient, RedisContext redisContext) {

        if(args.getFirst().equalsIgnoreCase("capa")) {
            System.out.println("TEEEEST");
            redisContext.getReplication().registerReplica(redisClient);
        }
        return this.queue(RespParser.fromSimple("OK"));
    }
}
