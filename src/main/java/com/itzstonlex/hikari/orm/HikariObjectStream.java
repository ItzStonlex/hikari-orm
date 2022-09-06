package com.itzstonlex.hikari.orm;

import com.itzstonlex.hikari.HikariTransaction;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class HikariObjectStream<T> {

    protected static final HikariObjectTypeParser OBJECT_TYPE_PARSER
            = new HikariObjectTypeParser();

    protected final Class<T> cls;
    protected final HikariTransaction transaction;

    public HikariObjectListStream<T> mapToList() {
        return new HikariObjectListStream<>(cls, transaction);
    }

    public CompletableFuture<T> toObjectFuture() {
        CompletableFuture<T> completableFuture = new CompletableFuture<>();

        transaction.setResponseConsumer(resultSet -> {

            if (resultSet.next()) {
                completableFuture.complete(OBJECT_TYPE_PARSER.parseObjectBySQL(cls, resultSet));
            }
            else completableFuture.completeExceptionally(new NullPointerException("response"));
        });

        transaction.commit();
        return completableFuture;
    }

    public T toObject() {
        return toObjectFuture().join();
    }

}
