<div align="center">

# Hikari ORM
Object-Relational Mapping Technology By HikariCP

[![MIT License](https://img.shields.io/github/license/pl3xgaming/Purpur?&logo=github)](LICENSE)

---

</div>

### Feedback

+ **[Discord Server](https://discord.gg/GmT9pUy8af)**
+ **[VKontakte Page](https://vk.com/itzstonlex)**

---

## WHAT IS THIS?

This repository is built on the third party library `HikariCP (com.zaxxer:HikariCP)`.

All requests are performed using transactions, and ORM (Object-Relational Mapping)
technology is also integrated into it

---

## HOW TO USE?
The code that was used to run the tests is in <a href="https://github.com/ItzStonlex/hikari-orm/tree/master/src/test/java/com/itzstonlex/hikari/test/type">this package</a>
<br>
Click on this link to go to the test package!

Example Connection create (as MySQL):
```java
new HikariProxy("jdbc:mysql://localhost:3306/mysql", "root", "**password**");
```

Example Connection testing:
```java
boolean isConnected = hikariProxy.testConnection();
```

Example Transactions using:
```java
HikariTransactionManager transactionManager = hikariProxy.createTransactionManager();
boolean async = true;
```

```java
transactionManager.beginTransaction(async)
        .push(TransactionExecuteType.UPDATE, "insert into `users` (`name`, `age`) values (?, ?)", player.getName(), player.getAge())
        .endpointTransaction()
        .commit();
```

Example ORM using:
```java
List<Player> players = transactionManager.beginTransaction(async)
        .push(TransactionExecuteType.FETCH, "select * from `users` limit 5")
        .endpointTransaction()

        .asStream(Player.class)
        .mapToList()
        .toList().join();
```

---

## SUPPORT ME

<a href="https://www.buymeacoffee.com/itzstonlex" target="_blank"><img src="https://www.buymeacoffee.com/assets/img/custom_images/orange_img.png" alt="Buy Me A Coffee" style="height: 41px !important;width: 174px !important;box-shadow: 0px 3px 2px 0px rgba(190, 190, 190, 0.5) !important;-webkit-box-shadow: 0px 3px 2px 0px rgba(190, 190, 190, 0.5) !important;" ></a>
