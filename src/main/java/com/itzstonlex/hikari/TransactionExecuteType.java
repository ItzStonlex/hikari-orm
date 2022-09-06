package com.itzstonlex.hikari;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public enum TransactionExecuteType {

    UPDATE {
        @Override
        public ResultSet execute(PreparedStatement statement, String query) throws SQLException {
            statement.executeUpdate();
            return statement.getGeneratedKeys();
        }
    },

    FETCH {
        @Override
        public ResultSet execute(PreparedStatement statement, String query) throws SQLException {
            return statement.executeQuery();
        }
    };

    public abstract ResultSet execute(PreparedStatement statement, String query) throws SQLException;
}
