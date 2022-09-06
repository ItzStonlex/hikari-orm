package com.itzstonlex.hikari;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TransactionCache {

    private final Map<Integer, Query> cache = new ConcurrentHashMap<>();

    public boolean contains(Query query) {
        return cache.containsKey(query.hashCode());
    }

    public void push(Query query) {
        cache.put(query.hashCode(), query);
    }

    public Query peek(int hashCode) {
        return cache.get(hashCode);
    }

}
