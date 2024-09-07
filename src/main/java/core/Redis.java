package core;

import command.RedisCommand;

import java.io.IOException;
import java.util.List;

public final class Redis {


    private final RedisCommandHandler commandHandler;
    private final EventLoop eventLoop;
    private final int port;

    private final RedisContext redisContext;
    public Redis(int port) {
        this.port = port;
        this.redisContext = new RedisContext();
        this.commandHandler = new RedisCommandHandler(redisContext);
        this.eventLoop = new EventLoop(commandHandler);
    }

    public void startServer() {
        try {
            eventLoop.run(port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // look into thread safety
    public RedisCommandHandler getCommandHandler() {
        return commandHandler;
    }


}
