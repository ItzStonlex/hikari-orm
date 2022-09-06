package com.itzstonlex.hikari.test.type;

import com.itzstonlex.hikari.HikariProxy;
import com.itzstonlex.hikari.test.HikariTester;
import lombok.NonNull;

public class HikariConnectionTest extends HikariTester {

    static {
        setProvider(new HikariConnectionTest());
    }

    @Override
    public void run(@NonNull HikariProxy hikariProxy) {
        log(hikariProxy.testConnection());
    }
}
