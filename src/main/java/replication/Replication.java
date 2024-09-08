package replication;

import configuration.ReplicationConfiguration;

import java.util.ArrayList;
import java.util.List;

public class Replication {

    private final ReplicationRole replicationRole;

    // can be null
    private final String masterHost;
    // can be null
    private final int masterPort;

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

        return info;
    }
}
