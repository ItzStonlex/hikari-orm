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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true)
public class HikariTransaction {

    private static final ExecutorService CACHED_POOL
            = Executors.newCachedThreadPool();

    private final boolean async;

    private HikariProxy hikariProxy;
    private Queue<Query> queryQueue = new ConcurrentLinkedDeque<>();

    @NonFinal
    private int hashCode;

    @Setter
    @NonFinal
    private HikariResponseConsumer responseConsumer;

    public void push(TransactionExecuteType type, String sql, Object... elements) {
        Query query = new Query(type, sql, elements);
        query.create();

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
            flush();
        };

        if (async) {
            CACHED_POOL.submit(task);
        }
        else {
            task.run();
        }
    }

    public void flush() {
        queryQueue.clear();
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

    @Getter
    @ToString
    @RequiredArgsConstructor
    @FieldDefaults(makeFinal = true)
    class Query {

        @ToString.Include
        private TransactionExecuteType executeType;

        @ToString.Include
        private String query;
        @ToString.Include
        private Object[] elements;

        @NonFinal
        private PreparedStatement statement;

        @SneakyThrows
        public void create() {
            statement = hikariProxy.createStatement(query);
            statement.setPoolable(true);
        }

        public ResultSet execute()
        throws SQLException {

            for (int idx = 1; idx <= elements.length; idx++) {
                Object obj = elements[idx - 1];

                if (obj != null) {
                    statement.setObject(idx, obj);

                } else {

                    statement.setNull(idx, Types.NULL);
                }
            }

            statement.closeOnCompletion();
            return executeType.execute(statement, query);
        }

        @Override
        public int hashCode() {
            return executeType.hashCode() & query.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Query) {
                return obj.hashCode() == hashCode();
            }

            return false;
        }
    }
}
