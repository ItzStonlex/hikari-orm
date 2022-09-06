package com.itzstonlex.hikari.orm;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.itzstonlex.hikari.HikariTransaction;
import com.itzstonlex.hikari.TransactionExecuteType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

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
        return mapFirst().toObjectFuture();
    }

    @Override
    public T toObject() {
        return mapFirst().toObject();
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

    public List<T> toList() {
        return toListFuture().join();
    }

    public HikariObjectListStream<T> push(List<T> sourcesList, String nativeQuery) {
        StringBuilder queryBuilder = new StringBuilder("INSERT ").append(nativeQuery).append(" ");

        for (T source : sourcesList) {

            JsonObject jsonObject = OBJECT_TYPE_PARSER.parseEntries(source);
            Set<Map.Entry<String, JsonElement>> entriesSet = jsonObject.entrySet();

            if (!queryBuilder.toString().contains(") VALUES (")) {
                queryBuilder.append("(")
                        .append(entriesSet.stream().map(e -> String.format("`%s`", e.getKey())).collect(Collectors.joining(", ")))
                        .append(") VALUES ");
            }

            queryBuilder.append("(")
                    .append(entriesSet.stream().map(e -> e.getValue().toString().replace("\"", "'")).collect(Collectors.joining(", ")))
                    .append("), ");
        }

        String query = queryBuilder.toString();
        transaction.push(TransactionExecuteType.UPDATE, query.substring(0, query.length() - 2));

        return this;
    }
}
