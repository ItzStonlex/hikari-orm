package com.itzstonlex.hikari;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter(AccessLevel.PACKAGE)
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true)
public class HikariTransaction {

    private static final ExecutorService CACHED_POOL
            = Executors.newCachedThreadPool();

    private boolean async;

    private TransactionCache transactionCache;

    private HikariProxy hikariProxy;

    private Queue<Query> queryQueue = new ConcurrentLinkedQueue<>();

    @NonFinal
    private int hashCode;

    @Setter
    @NonFinal
    private HikariResponseConsumer responseConsumer;

    public void push(TransactionExecuteType type, String sql, Object... elements) {
        Query query = transactionCache.peek(Query.hash(type, sql));

        if (query == null) {
            query = new Query(type, sql, elements);
            query.create(hikariProxy);

            transactionCache.push(query);
        }

        queryQueue.offer(query);

        int queryHash = query.hashCode();

        if (hashCode != 0) {
            hashCode &= queryHash;
        }
        else {
            hashCode = queryHash;
        }
    }

    public void commit() {
        Runnable task = () -> {
            Query query;

            while ((query = queryQueue.poll()) != null) {

                try (ResultSet response = query.execute()) {

                    if (responseConsumer != null) {
                        responseConsumer.accept(response);
                    }
                }
                catch (SQLException exception) {
                    hikariProxy.rollback();
                    exception.printStackTrace();

                    return;
                }
            }

            hikariProxy.commit();
            queryQueue.clear();
        };

        if (async) {
            CACHED_POOL.submit(task);
        }
        else {
            task.run();
        }
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
