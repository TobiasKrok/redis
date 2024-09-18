package core;

import command.*;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Queue;

public class RedisCommandHandler {

    private final RedisContext redisContext;
    public RedisCommandHandler(RedisContext context) {
        this.redisContext = context;
    }


    public boolean process(List<String> rawCommands, RedisClient client) throws IllegalArgumentException {

        if(rawCommands.isEmpty()) throw new RuntimeException("Empty command passed");

        System.out.println("redis: command recv: " + rawCommands);

        // can throw
        CommandType commandType = CommandType.valueOf(rawCommands.getFirst());
        rawCommands.removeFirst(); // we don't need the first element anymore
        RedisCommand redisCommand;
        switch (commandType) {
            case SIMPLE_PING -> redisCommand = new SimplePingCommand();
            case PING -> redisCommand = new PingCommand();
            case ECHO -> redisCommand = new EchoCommand();
            case GET -> redisCommand = new GetCommand();
            case SET -> redisCommand = new SetCommand();
            case INFO -> redisCommand = new InfoCommand();
            case REPLCONF -> redisCommand = new ReplConfCommand();
            case PSYNC -> redisCommand = new PsyncCommand();
            default -> throw new RuntimeException("Unknown command");
        }


        Queue<ByteBuffer> res =  redisCommand.execute(rawCommands, redisContext);
        if(res.isEmpty()) return false; // nothing to write

        // adds all the returned responses to the client write queue
        client.queueData(res);
        return true;
    }
}