package com.itzstonlex.hikari;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TransactionCache {

    private static final int MAX_CACHE_SIZE = 1024 * 1024;

    private final Map<Integer, Query> cache = new ConcurrentHashMap<>();

    private void checkSize() {
        if (cache.size() >= MAX_CACHE_SIZE) {
            cache.clear();
        }
    }

    public void push(Query query) {
        checkSize();
        cache.put(query.hashCode(), query);
    }

    public void pushAll(TransactionCache parent) {
        parent.cache.values().forEach(this::push);
    }

    public Query peek(int hashCode) {
        return cache.get(hashCode);
    }

}
