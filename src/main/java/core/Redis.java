package core;

import command.RedisCommand;
import configuration.RedisConfiguration;
import replication.Replication;
import replication.ReplicationRole;

import java.io.IOException;
import java.util.List;

public final class Redis {


    private final RedisCommandHandler commandHandler;
    private final EventLoop eventLoop;

    private final RedisContext redisContext;

    private final Replication replication;
    private final RedisConfiguration configuration;
    public Redis(RedisConfiguration configuration) {
        this.configuration = configuration;
        this.replication = new Replication(configuration.getReplicationConfiguration(), configuration.getPort());
        this.redisContext = new RedisContext(replication);
        this.commandHandler = new RedisCommandHandler(redisContext);
        this.eventLoop = new EventLoop(commandHandler);
    }

    public void startServer() {
        try {
            if(replication.getRole() == ReplicationRole.SLAVE) {
                replication.startReplicationService();
            }
            eventLoop.run(configuration.getPort());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
