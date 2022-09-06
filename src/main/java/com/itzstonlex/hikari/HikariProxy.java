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
            dataSource.setAutoCommit(false);

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

    public String nativeSQL(String sql) {
        if (connection == null) {
            testConnection();
        }

        try {
            return connection.nativeSQL(sql);
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
    }
}
