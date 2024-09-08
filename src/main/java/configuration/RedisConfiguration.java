package configuration;

import replication.ReplicationRole;

public final class RedisConfiguration {
    private final int port;

    private final ReplicationRole replicationRole;

    // Private constructor to restrict instantiation
    private RedisConfiguration(Builder builder) {
        this.port = builder.port;
        this.replicationRole = builder.role;
    }

    // Getters to access the configuration
    public int getPort() {
        return port;
    }

    public ReplicationRole getReplicationRole() {
        return replicationRole;
    }

    // Static Builder class
    public static class Builder {
        private int port;

        private ReplicationRole role;
        public Builder withPort(int port) {
            this.port = port;
            return this;
        }

        public Builder withReplicationRole(ReplicationRole role) {
            this.role = role;
            return this;
        }


        // Build method to create a Configuration instance
        public RedisConfiguration build() {
            return new RedisConfiguration(this);
        }
    }
}