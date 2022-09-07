package com.itzstonlex.hikari;

import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class HikariProxy {

    private Connection connection;
    private final HikariDataSource dataSource = new HikariDataSource();

    public HikariProxy(String url, String username, String password) {
        try {
            dataSource.setLoginTimeout(3);

            dataSource.setJdbcUrl(url);

            dataSource.setUsername(username);
            dataSource.setPassword(password);
        }
        catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
    }

    public HikariTransactionManager createTransactionManager() {
        return new HikariTransactionManager(this);
    }

    public void setAutoCommit(boolean flag) {
        dataSource.setAutoCommit(flag);
    }

    public boolean testConnection() {
        try {
            connection = dataSource.getConnection();
            return true;
        }
        catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
    }

    public PreparedStatement createStatement(String sql) {
        if (connection == null) {
            testConnection();
        }

        try {
            return connection.prepareStatement(sql);
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
    }

    public Query createQuery(TransactionExecuteType executeType, String sql, Object... parameters) {
        Query query = new Query(executeType, sql);

        query.create(this);
        query.setElements(parameters);

        return query;
    }

    public void commit() {
        if (connection == null) {
            testConnection();
        }

        try {
            connection.commit();
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void rollback() {
        if (connection == null) {
            testConnection();
        }

        try {
            connection.rollback();
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
    }

}
