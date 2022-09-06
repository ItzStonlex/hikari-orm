package com.itzstonlex.hikari;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

@Getter
@ToString
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true)
public class Query {

    public static int hash(TransactionExecuteType executeType, String query) {
        return executeType.hashCode() & query.hashCode();
    }

    @ToString.Include
    private TransactionExecuteType executeType;

    @ToString.Include
    private String query;
    @ToString.Include
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
        return hash(executeType, query);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Query) {
            return obj.hashCode() == hashCode();
        }

        return false;
    }
}