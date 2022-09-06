package com.itzstonlex.hikari;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

import java.io.Closeable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter(AccessLevel.PACKAGE)
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true)
public class HikariTransaction implements Closeable {

    private static final ExecutorService CACHED_POOL
            = Executors.newCachedThreadPool();

    private boolean async;

    @NonFinal
    private boolean closed;

    private TransactionCache parentCache;

    private HikariProxy hikariProxy;

    @NonFinal
    private TransactionCache localCache = new TransactionCache();

    @NonFinal
    private List<Query> queue = new ArrayList<>();

    @NonFinal
    private int hashCode;

    @Setter
    @NonFinal
    private HikariResponseConsumer responseConsumer;

    public void push(TransactionExecuteType type, String sql, Object... elements) {
        if (isClosed()) {
            throw new IllegalArgumentException("transaction closed");
        }

        Query query = localCache.peek(Query.hash(type, sql));

        if (query == null) {

            query = new Query(type, sql);
            query.create(hikariProxy);

            localCache.push(query);
        }

        query.setElements(elements);
        queue.add(query.clone());

        int queryHash = query.hashCode();

        if (hashCode != 0) {
            hashCode &= queryHash;
        }
        else {
            hashCode = queryHash;
        }
    }

    public void commit() {
        if (isClosed()) {
            throw new IllegalArgumentException("transaction closed");
        }

        Runnable task = () -> {
            Exception error = null;

            for (Query query : queue) {
                try (ResultSet response = query.execute()) {

                    if (responseConsumer != null) {
                        responseConsumer.accept(response);
                    }
                }
                catch (SQLException exception) {
                    error = exception;
                    break;
                }
            }

            if (error == null) {
                hikariProxy.commit();
            }
            else {
                error.printStackTrace();
            }

            this.close();
        };

        if (async) {
            CACHED_POOL.submit(task);
        }
        else {
            task.run();
        }
    }

    @Override
    public void close() {
        if (isClosed()) {
            throw new IllegalArgumentException("transaction already closed");
        }

        parentCache.pushAll(localCache);

        localCache.clear();
        localCache = null;

        queue.clear();
        queue = null;

        closed = true;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof HikariTransaction) {
            return obj.hashCode() == hashCode;
        }

        return false;
    }
}
