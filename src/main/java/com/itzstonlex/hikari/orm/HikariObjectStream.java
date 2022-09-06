package com.itzstonlex.hikari.orm;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.itzstonlex.hikari.HikariTransaction;
import com.itzstonlex.hikari.TransactionExecuteType;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

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

    public HikariObjectStream<T> push(T source, String nativeQuery) {
        JsonObject jsonObject = OBJECT_TYPE_PARSER.parseEntries(source);

        StringBuilder queryBuilder = new StringBuilder("INSERT ")
                .append(nativeQuery)
                .append(" ");

        Set<Map.Entry<String, JsonElement>> entriesSet = jsonObject.entrySet();

        queryBuilder.append("(")
                .append(entriesSet.stream().map(e -> String.format("`%s`", e.getKey())).collect(Collectors.joining(", ")))
                .append(") VALUES (")
                .append(entriesSet.stream().map(e -> "?").collect(Collectors.joining(", ")));

        transaction.push(TransactionExecuteType.UPDATE, queryBuilder.append(")").toString(),
                entriesSet.stream().map(e -> OBJECT_TYPE_PARSER.fromJson(e.getValue().toString())).toArray());

        return this;
    }

    public final void commit() {
        transaction.commit();
    }

}
