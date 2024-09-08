package replication;

import java.util.ArrayList;
import java.util.List;

public class Replication {

    private String role;

    public Replication(String role) {
        this.role = role;
    }


    public String getRole() {
        return role;
    }

    public List<String> getReplicationInfo() {
        List<String> info = new ArrayList<>();

        info.add("role:" + role);

        return info;
    }
}
