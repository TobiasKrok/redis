package configuration;

import replication.ReplicationRole;

public final class RedisConfiguration {
    private final int port;

    private final ReplicationConfiguration replicationConfiguration;

    // Private constructor to restrict instantiation
    private RedisConfiguration(Builder builder) {
        this.port = builder.port;
        this.replicationConfiguration = builder.replicationConfiguration;

    }

    // Getters to access the configuration
    public int getPort() {
        return port;
    }

    public ReplicationConfiguration getReplicationConfiguration() {
        return replicationConfiguration;
    }

    // Static Builder class
    public static class Builder {
        private int port = 6379;

        private ReplicationConfiguration replicationConfiguration = new ReplicationConfiguration();

        public Builder withPort(int port) {
            this.port = port;
            return this;
        }

        public Builder withReplication(String replicationHost, int port) {
            this.replicationConfiguration = new ReplicationConfiguration(replicationHost, port);
            return this;
        }


        // Build method to create a Configuration instance
        public RedisConfiguration build() {
            return new RedisConfiguration(this);
        }
    }
}