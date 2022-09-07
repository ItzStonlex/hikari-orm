package com.itzstonlex.hikari;

import com.itzstonlex.hikari.orm.HikariObjectStream;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class HikariTransactionManager {

    private final HikariProxy hikariProxy;
    private final TransactionCache transactionCache = new TransactionCache();

    private HikariTransaction currentTransaction;

    public HikariTransactionManager beginTransaction(boolean async) {
        currentTransaction = new HikariTransaction(async, transactionCache, hikariProxy);
        return this;
    }

    public HikariTransactionManager push(TransactionExecuteType type, String sql, Object... elements) {
        Objects.requireNonNull(currentTransaction, "transaction");

        synchronized (transactionCache) {
            currentTransaction.push(type, sql, elements);
        }

        return this;
    }

    public HikariTransactionManager consumeResponse(HikariResponseConsumer responseConsumer) {
        Objects.requireNonNull(currentTransaction, "transaction");

        synchronized (transactionCache) {
            currentTransaction.setResponseConsumer(responseConsumer);
        }

        return this;
    }

    public void commit() {
        Objects.requireNonNull(currentTransaction, "transaction");

        synchronized (transactionCache) {

            currentTransaction.commit();
            currentTransaction = null;
        }
    }

    public <T> HikariObjectStream<T> asStream(Class<T> cls) {
        Objects.requireNonNull(currentTransaction, "transaction");

        synchronized (transactionCache) {
            return new HikariObjectStream<>(cls, currentTransaction);
        }
    }

}
