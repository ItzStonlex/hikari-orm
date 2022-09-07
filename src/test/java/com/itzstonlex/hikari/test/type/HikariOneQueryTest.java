package com.itzstonlex.hikari.test.type;

import com.itzstonlex.hikari.HikariProxy;
import com.itzstonlex.hikari.Query;
import com.itzstonlex.hikari.TransactionExecuteType;
import com.itzstonlex.hikari.test.HikariTester;
import lombok.NonNull;

import java.sql.ResultSet;
import java.sql.SQLException;

public class HikariOneQueryTest extends HikariTester {
    static {
        setProvider(new HikariOneQueryTest());
    }

    @Override
    public void run(@NonNull HikariProxy hikariProxy) {

        try (Query query = hikariProxy.createQuery(TransactionExecuteType.FETCH, "show tables");
             ResultSet resultSet = query.execute()) {

            log(resultSet.next());
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
