package com.itzstonlex.hikari.orm;

import com.itzstonlex.hikari.HikariTransaction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class HikariObjectListStream<T> extends HikariObjectStream<T> {

    private int limit;

    public HikariObjectListStream(Class<T> cls, HikariTransaction transaction) {
        super(cls, transaction);
    }

    public HikariObjectStream<T> mapFirst() {
        return new HikariObjectStream<>(cls, transaction);
    }

    public HikariObjectListStream<T> limit(int limit) {
        this.limit = limit;
        return this;
    }

    public HikariObjectListStream<T> unlimit() {
        return limit(0);
    }

    @Override
    public CompletableFuture<T> toObjectFuture() {
        throw new UnsupportedOperationException();
    }

    public CompletableFuture<List<T>> toListFuture() {
        CompletableFuture<List<T>> completableFuture = new CompletableFuture<>();

        super.transaction.setResponseConsumer(resultSet -> {

            List<T> list = new ArrayList<>();

            int counter = 0;
            while (resultSet.next()) {

                if (limit > 0 && counter++ >= limit) {
                    break;
                }

                list.add(OBJECT_TYPE_PARSER.parseObjectBySQL(cls, resultSet));

                if (resultSet.isLast()) {
                    break;
                }
            }

            completableFuture.complete(list);
        });

        super.transaction.commit();
        return completableFuture;
    }
}
