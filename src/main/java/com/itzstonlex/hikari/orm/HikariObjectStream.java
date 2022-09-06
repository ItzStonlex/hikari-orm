package com.itzstonlex.hikari.orm;

import com.itzstonlex.hikari.HikariTransaction;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class HikariObjectStream<T> {

    protected static final HikariObjectTypeParser OBJECT_TYPE_PARSER
            = new HikariObjectTypeParser();

    protected final Class<T> cls;
    protected final HikariTransaction transaction;

    @Getter
    @Setter
    private Consumer<HikariTransaction> whenCompleted;

    public HikariObjectListStream<T> mapToList() {
        return new HikariObjectListStream<>(cls, transaction);
    }

    public CompletableFuture<T> toSingleton() {
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

}
