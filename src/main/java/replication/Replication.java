package replication;

import command.PingCommand;
import configuration.ReplicationConfiguration;
import core.RedisClient;
import core.RedisCommandHandler;
import core.RespParser;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.rmi.server.UID;
import java.util.*;
import java.util.concurrent.*;

public class Replication {

    private final ReplicationRole replicationRole;

    // can be null
    private final String masterHost;
    // can be null
    private final int masterPort;

    private final int serverPort; // the redis server port

    private final String id = "8371b4fb1155b71f4a04d3e1bc3e18c4a990aeeb";

    private volatile int offset = 0;

    private final List<RedisClient> connectedReplicas;

    private RedisCommandHandler commandHandler;



    public Replication(ReplicationConfiguration replicationConfiguration, int serverPort) {
        this.replicationRole = replicationConfiguration.getRole();
        this.masterHost = replicationConfiguration.getHost();
        this.masterPort = replicationConfiguration.getPort();
        this.serverPort = serverPort;
        this.connectedReplicas = new ArrayList<>();
    }


    public void setCommandHandler(RedisCommandHandler commandHandler) {
        if(this.commandHandler != null) {
            throw new RuntimeException("You should only set the command handler once");
        }
        this.commandHandler = commandHandler;
    }

    public void startReplicationService() {
        if(commandHandler == null) throw new RuntimeException("CommandHandler not set");
        System.out.println("redis: starting replication service as replica");
        ReplicationClient replicationClient = new ReplicationClient(serverPort, masterHost, masterPort, commandHandler);
        new Thread(replicationClient).start();
    }

    public void registerReplica(RedisClient client) {
        if(commandHandler == null) throw new RuntimeException("CommandHandler not set");
        try {
            connectedReplicas.add(client);
        } catch (Exception e) {

            System.err.println("redis: repl failed to register client: " + e.getMessage());
        }
    }

    public void propagate(List<String> args) {
        try {
            for (RedisClient client : connectedReplicas) {
                System.out.println("redis: propagating to " + client.getId());
                client.queueData(ByteBuffer.wrap(RespParser.fromArray(args)));
            }

        } catch (Exception e) {
            System.out.println("dis: failed to propagate: " + e.getMessage());
        }
    }

    public ReplicationRole getRole() {
        return replicationRole;
    }

    //todo toString() instead
    public List<String> getReplicationInfo() {
        List<String> info = new ArrayList<>();

        info.add("role:" + replicationRole.toString().toLowerCase());
        info.add("master_replid:" + id);
        info.add("master_repl_offset:" + offset);
        return info;
    }

    public String getId() {
        return id;
    }

    public int getOffset() {
        return offset;
    }
}
