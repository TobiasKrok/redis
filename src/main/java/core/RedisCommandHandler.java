package core;

import command.*;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Queue;

public class RedisCommandHandler {



    private final RedisContext redisContext;
    public RedisCommandHandler(final RedisContext context ) {
        this.redisContext = context;
    }


    public boolean process(List<String> rawCommands, RedisClient client) throws IllegalArgumentException {
        return process(rawCommands,client, true);
    }

    public boolean process(List<String> rawCommands, RedisClient client, boolean shouldRespond) throws IllegalArgumentException {

        if(rawCommands.isEmpty()) throw new RuntimeException("Empty command passed");

        System.out.println("redis: command recv: " + rawCommands);

        // can throw
        CommandType commandType = CommandType.valueOf(rawCommands.getFirst().toUpperCase());
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

        // for easier processing, pass a list where the first is removed since we don't really care about it unless the command should be propagated
        Queue<ByteBuffer> res =  redisCommand.execute(rawCommands.size() == 1 ? rawCommands : rawCommands.subList(1, rawCommands.size()), rawCommands, client , redisContext);

       // if this command handler should reply
      if(shouldRespond) {
          if(res.isEmpty()) return false; // nothing to write

          // adds all the returned responses to the client write queue
          client.queueData(res);
          return true;
      }
      return false;
    }
}