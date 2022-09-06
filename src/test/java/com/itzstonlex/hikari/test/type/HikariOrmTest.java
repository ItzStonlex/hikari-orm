package com.itzstonlex.hikari.test.type;

import com.itzstonlex.hikari.HikariProxy;
import com.itzstonlex.hikari.HikariTransactionManager;
import com.itzstonlex.hikari.TransactionExecuteType;
import com.itzstonlex.hikari.test.HikariTester;
import com.itzstonlex.hikari.test.orm.Player;
import lombok.NonNull;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class HikariOrmTest extends HikariTester {

    static {
        setProvider(new HikariOrmTest());
    }

    @Override
    public void run(@NonNull HikariProxy hikariProxy) {
        HikariTransactionManager transactionManager = hikariProxy.createTransactionManager();

        // Creating a table query.
        transactionManager.beginTransaction(false)
                .push(TransactionExecuteType.UPDATE, "create table `users` (`id` bigint not null, `uuid` text not null, `name` text not null)")
                .commit();

        // Insert all players object query.
        List<Player> playersToPush = Arrays.asList(
                new Player(104, UUID.randomUUID(), "Misha"),
                new Player(12, UUID.randomUUID(), "Egor"),
                new Player(53, UUID.randomUUID(), "Sergey"),
                new Player(41, UUID.randomUUID(), "Nikolay")
        );

        transactionManager.beginTransaction(false)
                .asStream(Player.class)
                .mapToList()
                .pushAll(playersToPush, "into users")
                .commit();

        // Get 3 first players from db query.
        List<Player> players = transactionManager.beginTransaction(true)
                .push(TransactionExecuteType.FETCH, "select * from `users`")
                .asStream(Player.class)
                .mapToList()
                .limit(3)
                .toList();

        for (Player player : players) {
            log("ID: %s | UUID: %s | Name: %s", player.getId(), player.getUuid(), player.getName());
        }
    }

}
