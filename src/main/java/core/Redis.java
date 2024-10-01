package core;

import command.RedisCommand;
import configuration.RedisConfiguration;
import replication.Replication;
import replication.ReplicationRole;

import java.io.IOException;
import java.util.List;

public final class Redis {



    private final EventLoop eventLoop;

    private final RedisContext redisContext;

    private final Replication replication;
    private final RedisConfiguration configuration;

    public Redis(RedisConfiguration configuration) {
        this.configuration = configuration;
        this.replication = new Replication(configuration.getReplicationConfiguration(), configuration.getPort());
        this.redisContext = new RedisContext(replication);

        RedisCommandHandler commandHandler = new RedisCommandHandler(redisContext);
        this.eventLoop = new EventLoop(commandHandler, redisContext.getConnectedClients());

        // Replication needs its own command handler to pass down to any replication client. I didn't really want to make commandhandler public
        // CommandHandler needs a redisContext which holds a reference to the replication object, so there's a self reference there!
        replication.setCommandHandler(commandHandler);
    }

    public void startServer() {
        try {
            if(replication.getRole() == ReplicationRole.SLAVE) {
                System.out.println("redis: starting as replica");
                replication.startReplicationService();
            }
            System.out.println("redis: starting event loop");
            eventLoop.run(configuration.getPort());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
