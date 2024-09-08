package core;

import replication.Replication;

import java.util.HashMap;
import java.util.Map;

public class RedisContext {

    private final Map<String, Object> store;
    //TODO look into active expiration in separate thread?
    private final Map<String, Long> expiration;

    private final Replication replication;

    protected RedisContext() {
        // maybe concurrent hashmap later but we're single threaded for now
        this.store = new HashMap<>();
        this.expiration = new HashMap<>();
        this.replication = new Replication("master");
    }


    public Map<String, Object> getStore() {
        return  store;
    }

    public Map<String, Long> getExpiration() {
        return expiration;
    }

    public Replication getReplication() {
        return replication;
    }
}
