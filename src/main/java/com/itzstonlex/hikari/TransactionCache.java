package com.itzstonlex.hikari;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TransactionCache {

    private final Map<Integer, HikariTransaction> cache = new ConcurrentHashMap<>();

    public boolean contains(HikariTransaction transaction) {
        return cache.containsKey(transaction.hashCode());
    }

    public void push(HikariTransaction transaction) {
        cache.put(transaction.hashCode(), transaction);
    }

    public HikariTransaction peek(int hashCode) {
        return cache.get(hashCode);
    }

}
