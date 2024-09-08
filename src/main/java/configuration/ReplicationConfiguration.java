package configuration;

import replication.ReplicationRole;

public class ReplicationConfiguration {

    private final ReplicationRole role;
    private String host;

    private int port;

    public ReplicationConfiguration() {
        this.role = ReplicationRole.MASTER;
    }

    public ReplicationConfiguration(String host, int port) {
        this.role =  ReplicationRole.SLAVE;
        this.host = host;
        this.port = port;
    }

    public ReplicationRole getRole() {
        return role;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
