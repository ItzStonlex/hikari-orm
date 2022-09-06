package com.itzstonlex.hikari.test;

import com.itzstonlex.hikari.HikariProxy;
import lombok.NonNull;
import lombok.Setter;

import java.text.SimpleDateFormat;

public abstract class HikariTester {

    private static final SimpleDateFormat LOGGER_DATE_FORMAT = new SimpleDateFormat("HH:mm:ss.SSS");

    @Setter
    private static HikariTester provider;

    public static void main(String[] args) {
        provider.run(createHikariProxy());
    }

    public static void log(Object message, Object... replacements) {
        System.out.printf("[%s]: %s%n", LOGGER_DATE_FORMAT.format(System.currentTimeMillis()), String.format(message.toString(), replacements));
    }

    public static HikariProxy createHikariProxy() {
        return new HikariProxy("jdbc:h2:mem:default", "root", "pass");
    }

    public abstract void run(@NonNull HikariProxy hikariProxy);
}
