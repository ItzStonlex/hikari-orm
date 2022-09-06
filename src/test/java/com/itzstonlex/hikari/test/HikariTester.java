package com.itzstonlex.hikari.test;

import com.itzstonlex.hikari.HikariProxy;
import lombok.NonNull;
import lombok.Setter;

public abstract class HikariTester {

    @Setter
    private static HikariTester provider;

    public static void main(String[] args) {
        provider.run(createHikariProxy());
    }

    public static void log(Object message, Object... replacements) {
        System.out.println("[Logger]: " + String.format(message.toString(), replacements));
    }

    public static HikariProxy createHikariProxy() {
        return new HikariProxy("jdbc:h2:mem:default", "root", "pass");
    }

    public abstract void run(@NonNull HikariProxy hikariProxy);
}
