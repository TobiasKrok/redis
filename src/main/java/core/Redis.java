package core;

import command.RedisCommand;
import configuration.RedisConfiguration;

import java.io.IOException;
import java.util.List;

public final class Redis {


    private final RedisCommandHandler commandHandler;
    private final EventLoop eventLoop;

    private final RedisContext redisContext;

    private final RedisConfiguration configuration;
    public Redis(RedisConfiguration configuration) {
        this.configuration = configuration;
        this.redisContext = new RedisContext();
        this.commandHandler = new RedisCommandHandler(redisContext);
        this.eventLoop = new EventLoop(commandHandler);
    }

    public void startServer() {
        try {
            eventLoop.run(configuration.getPort());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // look into thread safety
    public RedisCommandHandler getCommandHandler() {
        return commandHandler;
    }


}
