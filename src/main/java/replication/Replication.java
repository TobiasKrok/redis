package replication;

import command.PingCommand;
import configuration.ReplicationConfiguration;
import core.RespParser;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.rmi.server.UID;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Replication {

    private final ReplicationRole replicationRole;

    // can be null
    private final String masterHost;
    // can be null
    private final int masterPort;

    private final int serverPort; // the redis server port

    // not good to use uuid but w/e
    private final String id = UUID.randomUUID().toString();

    private final int offset = 0;

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    public Replication(ReplicationConfiguration replicationConfiguration, int serverPort) {
        this.replicationRole = replicationConfiguration.getRole();
        this.masterHost = replicationConfiguration.getHost();
        this.masterPort = replicationConfiguration.getPort();
        this.serverPort = serverPort;
    }


    public void startReplicationService() {
        System.out.println("redis: starting replication service");
        // Replication cron, I think this is how Redis does it?
        executorService.scheduleAtFixedRate(new ReplicationClient(serverPort, masterHost, masterPort), 1,1,  TimeUnit.SECONDS);
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
