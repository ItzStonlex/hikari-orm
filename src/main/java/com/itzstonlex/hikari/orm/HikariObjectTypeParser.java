package com.itzstonlex.hikari.orm;

import com.google.gson.*;
import com.google.gson.internal.UnsafeAllocator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class HikariObjectTypeParser {

    private static final UnsafeAllocator UNSAFE_ALLOCATOR = UnsafeAllocator.create();

    private final Gson gson = new Gson();

    public Object fromJson(String json) {
        return gson.fromJson(json, Object.class);
    }

    public JsonObject parseEntries(Object source) {
        return JsonParser.parseString(gson.toJson(source)).getAsJsonObject();
    }

    public Set<String> parseTypeLabels(Class<?> type) {
        return Arrays.stream(type.getDeclaredFields()).map(Field::getName).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Set<String> parseSQLLabels(ResultSet resultSet)
    throws SQLException {

        Set<String> result = new LinkedHashSet<>();

        ResultSetMetaData metadata = resultSet.getMetaData();

        for (int columnIndex = 1; columnIndex <= metadata.getColumnCount(); columnIndex++) {
            result.add(metadata.getColumnLabel(columnIndex));
        }

        return result;
    }

    private Object _adaptive(Object object, Class<?> type)
    throws IOException {

        return gson.getAdapter(type).fromJson(gson.toJson(object));
    }

    public <T> T parseObjectBySQL(Class<T> type, ResultSet resultSet)
    throws SQLException {

        Set<String> typeLabels = parseTypeLabels(type);
        Set<String> sqlLabels = parseSQLLabels(resultSet);

        try {
            T sourceResult = UNSAFE_ALLOCATOR.newInstance(type);

            typeLabels.stream().filter(typeLabel -> sqlLabels.stream().anyMatch(sql -> sql.equalsIgnoreCase(typeLabel)))
                    .forEachOrdered(label -> {

                        try {
                            Field labelField = type.getDeclaredField(label);

                            labelField.setAccessible(true);
                            labelField.set(sourceResult, this._adaptive(resultSet.getObject(label), labelField.getType()));
                        }
                        catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    });

            return sourceResult;
        }
        catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

}
