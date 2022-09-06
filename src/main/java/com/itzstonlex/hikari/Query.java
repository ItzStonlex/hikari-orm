package com.itzstonlex.hikari;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

import java.io.Closeable;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

@Getter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(makeFinal = true)
public class Query implements Cloneable, Closeable {

    public static int hash(TransactionExecuteType executeType, String query) {
        return executeType.hashCode() & query.hashCode();
    }

    @ToString.Include
    private TransactionExecuteType executeType;

    @ToString.Include
    private String query;

    @ToString.Include
    @NonFinal
    @Setter
    private Object[] elements;

    @NonFinal
    private PreparedStatement statement;

    @SneakyThrows
    public void create(HikariProxy hikariProxy) {
        statement = hikariProxy.createStatement(query);
        statement.setPoolable(true);
    }

    public ResultSet execute()
    throws SQLException {

        statement.clearParameters();

        for (int idx = 1; idx <= elements.length; idx++) {
            Object obj = elements[idx - 1];

            if (obj != null) {
                statement.setObject(idx, obj);

            } else {

                statement.setNull(idx, Types.NULL);
            }
        }

        return executeType.execute(statement, query);
    }

    @Override
    public int hashCode() {
        return hash(executeType, query);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Query) {
            return obj.hashCode() == hashCode();
        }

        return false;
    }

    @Override
    public Query clone() {
        try {
            return (Query) super.clone();
        }
        catch (CloneNotSupportedException exception) {
            throw new AssertionError();
        }
    }

    @Override
    public void close() {
        try {
            statement.close();
        }
        catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
    }
}