package com.itzstonlex.hikari.test.type;

import com.itzstonlex.hikari.HikariProxy;
import com.itzstonlex.hikari.HikariTransactionManager;
import com.itzstonlex.hikari.TransactionExecuteType;
import com.itzstonlex.hikari.test.HikariTester;
import com.itzstonlex.hikari.test.orm.Player;
import lombok.NonNull;

import java.util.List;

public class HikariOrmTest extends HikariTester {

    static {
        setProvider(new HikariOrmTest());
    }

    @Override
    public void run(@NonNull HikariProxy hikariProxy) {
        HikariTransactionManager transactionManager = hikariProxy.createTransactionManager();

        transactionManager.beginTransaction(false)
                .push(TransactionExecuteType.UPDATE, "create table `users` (`id` bigint not null, `uuid` text not null, `name` text not null)")
                .commit();

        transactionManager.beginTransaction(false)
                .push(TransactionExecuteType.UPDATE, "insert into `users` values (?, ?, ?)", 104, "44dbc8fb-afe0-4592-b653-5defcbb6201f", "Misha")
                .push(TransactionExecuteType.UPDATE, "insert into `users` values (?, ?, ?)", 12, "df3419e9-ae0b-4ade-9d4c-ac1fb60c7fd7", "Egor")
                .push(TransactionExecuteType.UPDATE, "insert into `users` values (?, ?, ?)", 53, "e1e26bfd-d827-4c7d-9ba8-4fcd80193df8", "Sergey")
                .push(TransactionExecuteType.UPDATE, "insert into `users` values (?, ?, ?)", 41, "b48a79d0-5da2-4caf-a92b-23626628b0f4", "Nikolay")
                .commit();

        transactionManager.beginTransaction(true)
                .push(TransactionExecuteType.FETCH, "select * from `users`")
                .consumeResponse(resultSet -> {

                    while (resultSet.next()) {
                        log(resultSet.getString("name"));
                    }
                })
                .commit();

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
