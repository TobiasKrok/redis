package core;

import replication.Replication;
import replication.ReplicationRole;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RedisContext {

    private final ConcurrentHashMap<String, Object> store;
    //TODO look into active expiration in separate thread?
    private final ConcurrentHashMap<String, Long> expiration;

    private final Replication replication;

    private final ConcurrentHashMap<String, RedisClient> connectedClients = new ConcurrentHashMap<>();

    protected RedisContext(Replication replication) {
        // maybe concurrent hashmap later but we're single threaded for now
        this.store = new ConcurrentHashMap<>();
        this.expiration = new ConcurrentHashMap<>();
        this.replication = replication;
    }


    public ConcurrentHashMap<String, RedisClient> getConnectedClients() {
        return connectedClients;
    }

    public ConcurrentHashMap<String, Object> getStore() {
        return  store;
    }

    public ConcurrentHashMap<String, Long> getExpiration() {
        return expiration;
    }

    public Replication getReplication() {
        return replication;
    }
}
