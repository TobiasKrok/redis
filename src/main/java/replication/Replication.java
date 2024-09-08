package replication;

import configuration.ReplicationConfiguration;

import java.rmi.server.UID;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Replication {

    private final ReplicationRole replicationRole;

    // can be null
    private final String masterHost;
    // can be null
    private final int masterPort;

    // not good to use uuid but w/e
    private final String id = UUID.randomUUID().toString();

    private final int offset = 0;

    public Replication(ReplicationConfiguration replicationConfiguration) {
        this.replicationRole = replicationConfiguration.getRole();
        this.masterHost = replicationConfiguration.getHost();
        this.masterPort = replicationConfiguration.getPort();

    }


    public ReplicationRole getRole() {
        return replicationRole;
    }

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
