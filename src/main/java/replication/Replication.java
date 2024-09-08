package replication;

import java.util.ArrayList;
import java.util.List;

public class Replication {

    private ReplicationRole replicationRole;

    public Replication(ReplicationRole replicationRole) {
        this.replicationRole = replicationRole;
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
