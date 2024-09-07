package core;

import java.util.HashMap;
import java.util.Map;

public class RedisContext {

    private final Map<String, Object> store;

    protected RedisContext() {
        // maybe concurrent hashmap later but we're single threaded for now
        this.store = new HashMap<>();
    }


    public Map<String, Object> getStore() {
        return  store;
    }
}
