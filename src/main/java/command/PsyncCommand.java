package command;

import core.RedisClient;
import core.RedisContext;
import core.RespParser;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.List;
import java.util.Queue;

public class PsyncCommand extends RedisCommand {

    @Override
    public Queue<ByteBuffer> execute(List<String> args, List<String> rawArgs, RedisClient redisClient, RedisContext redisContext) {

        // todo better API for this, don't like it
        this.queue(RespParser.fromSimple("FULLRESYNC " + redisContext.getReplication().getId() + " " + redisContext.getReplication().getOffset()));

        byte[] rdb = Base64.getDecoder().decode("UkVESVMwMDEx+glyZWRpcy12ZXIFNy4yLjD6CnJlZGlzLWJpdHPAQPoFY3RpbWXCbQi8ZfoIdXNlZC1tZW3CsMQQAPoIYW9mLWJhc2XAAP/wbjv+wP9aog==");
        this.queue(("$" + rdb.length + "\r\n").getBytes());
        this.queue(rdb);
        return this.queue();

    }
}
