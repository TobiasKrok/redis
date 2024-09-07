package core;

import command.CommandType;
import command.EchoCommand;
import command.PingCommand;
import command.RedisCommand;

import java.util.List;

public class RedisCommandHandler {

    private final RedisContext redisContext;
    public RedisCommandHandler(RedisContext context) {
        this.redisContext = context;
    }


    public byte[] process(List<String> rawCommands) throws IllegalArgumentException {

        if(rawCommands.isEmpty()) throw new RuntimeException("Empty command passed");

        // can throw
        CommandType commandType = CommandType.valueOf(rawCommands.getFirst());
        rawCommands.removeFirst(); // we don't need the first element anymore
        RedisCommand redisCommand;
        System.out.println(rawCommands);
        switch (commandType) {
            case PING -> {
                redisCommand = new PingCommand();
            }
            case ECHO -> redisCommand = new EchoCommand();
            default -> throw new RuntimeException("Unknown command");
        }

        return redisCommand.execute(rawCommands, redisContext).getRaw();
    }
}