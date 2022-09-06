package com.itzstonlex.hikari.test.type;

import com.itzstonlex.hikari.HikariProxy;
import com.itzstonlex.hikari.HikariTransactionManager;
import com.itzstonlex.hikari.TransactionExecuteType;
import com.itzstonlex.hikari.test.HikariTester;
import lombok.NonNull;

public class HikariTransactionsTest extends HikariTester {

    static {
        setProvider(new HikariTransactionsTest());
    }

    @Override
    public void run(@NonNull HikariProxy hikariProxy) {
        HikariTransactionManager transactionManager = hikariProxy.createTransactionManager();

        transactionManager.beginTransaction(false)
                .push(TransactionExecuteType.UPDATE, "create table `users` (`name` varchar(255) not null, `age` int not null)")
                .commit();

        transactionManager.beginTransaction(true)
                .push(TransactionExecuteType.UPDATE, "insert into `users` (`name`, `age`) values (?, ?)", "Misha Leyn", 18)
                .push(TransactionExecuteType.FETCH, "select `age` from `users` where `name`=?", "Misha Leyn")

                .consumeResponse(resultSet -> log(resultSet.getFetchSize()))
                .commit();
    }

}
