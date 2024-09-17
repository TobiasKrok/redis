package core;

import command.*;

import java.util.List;

public class RedisCommandHandler {

    private final RedisContext redisContext;
    public RedisCommandHandler(RedisContext context) {
        this.redisContext = context;
    }


    public byte[] process(List<String> rawCommands) throws IllegalArgumentException {

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

        return redisCommand.execute(rawCommands, redisContext).getRaw();
    }
}