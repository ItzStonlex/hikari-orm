package com.itzstonlex.hikari.orm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class HikariObjectTypeParser {

    private final Gson googleJson = new Gson();
    private final ObjectMapper jacksonMapper = new ObjectMapper();

    public Object fromJson(String json) {
        return googleJson.fromJson(json, Object.class);
    }

    public JsonObject parseEntries(Object source) {
        return JsonParser.parseString(googleJson.toJson(source)).getAsJsonObject();
    }

    public Set<String> parseLabels(Class<?> type) {
        return Arrays.stream(type.getDeclaredFields()).map(Field::getName).collect(Collectors.toSet());
    }

    public <T> T parseObjectBySQL(Class<T> type, ResultSet resultSet)
    throws SQLException {

        JsonObject jsonObject = new JsonObject();

        Set<String> labelsSet = parseLabels(type);

        for (String label : labelsSet) {
            JsonElement element = _toJsonElement(resultSet.getObject(label));

            if (element != null) {
                jsonObject.add(label, element);
            }
        }

        return googleJson.fromJson(jsonObject.toString(), type);
    }

    private boolean _isJsonPrimitive(Object object) {
        return object instanceof Number || object instanceof Boolean || object instanceof Character || object instanceof String;
    }

    private JsonPrimitive _toJsonPrimitive(Object object) {
        if (object instanceof Number) {
            return new JsonPrimitive((Number)object);
        }

        if (object instanceof Boolean) {
            return new JsonPrimitive((Boolean)object);
        }

        if (object instanceof Character) {
            return new JsonPrimitive((Character)object);
        }

        if (object instanceof String) {
            return new JsonPrimitive((String)object);
        }

        return new JsonPrimitive(object.toString());
    }

    private boolean _isJson(String value) {
        try {
            jacksonMapper.readTree(value);
            return true;
        }
        catch (IOException exception) {
            return false;
        }
    }

    private JsonElement stringToJsonElement(String json) {
        return JsonParser.parseString(json);
    }

    private JsonElement _toJsonElement(Object object) {
        JsonElement element = null;

        if (object == null) {
            element = JsonNull.INSTANCE;
        }
        else {

            if (object instanceof Object[]) {
                JsonArray jsonArray = new JsonArray();

                for (Object arrElement : (Object[]) object) {
                    JsonElement jsonElement = _toJsonPrimitive(arrElement);

                    if (_isJson(arrElement.toString())) {
                        jsonElement = stringToJsonElement(arrElement.toString());
                    }

                    jsonArray.add(jsonElement);
                }

                element = jsonArray;
            }

            if (_isJsonPrimitive(object)) {
                element = _toJsonPrimitive(object);

                // maybe also json?
                if (_isJson(object.toString())) {
                    element = stringToJsonElement(object.toString());
                }
            }
        }

        return element;
    }

}
