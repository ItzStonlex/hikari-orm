package com.itzstonlex.hikari.test.type;

import com.itzstonlex.hikari.HikariProxy;
import com.itzstonlex.hikari.HikariTransactionManager;
import com.itzstonlex.hikari.TransactionExecuteType;
import com.itzstonlex.hikari.test.HikariTester;
import com.itzstonlex.hikari.test.PlayerDao;
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
                .push(TransactionExecuteType.UPDATE, "create table `users` (`id` bigint not null auto_increment, `uuid` text not null, `name` text not null)")
                .commit();

        // Insert all players object query.
        List<PlayerDao> playersToPush = Arrays.asList(
                new PlayerDao(UUID.randomUUID(), "Misha"),
                new PlayerDao(UUID.randomUUID(), "Egor"),
                new PlayerDao(UUID.randomUUID(), "Sergey"),
                new PlayerDao(UUID.randomUUID(), "Nikolay")
        );

        transactionManager.beginTransaction(false)
                .asStream(PlayerDao.class)
                .mapToList()
                .pushAll(playersToPush, "into users")
                .commit();

        // Get 3 first players from db query.
        List<PlayerDao> players = transactionManager.beginTransaction(true)
                .push(TransactionExecuteType.FETCH, "select * from `users`")
                .asStream(PlayerDao.class)
                .mapToList()
                .limit(3)
                .toList();

        for (PlayerDao player : players) {
            log("ID: %s | UUID: %s | Name: %s", player.getId(), player.getUuid(), player.getName());
        }
    }

}
