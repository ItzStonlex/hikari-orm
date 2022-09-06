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

This repository is built on the third library `HikariCP (com.zaxxer:HikariCP)`.

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
        .push(TransactionExecuteType.UPDATE, "INSERT INTO `users` VALUES (?, ?, ?)", "44dbc8fb-afe0-4592-b653-5defcbb6201f", "Misha")
        .push(TransactionExecuteType.UPDATE, "INSERT INTO `users` VALUES (?, ?, ?)", "df3419e9-ae0b-4ade-9d4c-ac1fb60c7fd7", "Egor")
        .push(TransactionExecuteType.UPDATE, "INSERT INTO `users` VALUES (?, ?, ?)", "e1e26bfd-d827-4c7d-9ba8-4fcd80193df8", "Sergey")
        .push(TransactionExecuteType.UPDATE, "INSERT INTO `users` VALUES (?, ?, ?)", "b48a79d0-5da2-4caf-a92b-23626628b0f4", "Nikolay")
        .commit();
```

Example ORM using:
```java
List<Player> players = transactionManager.beginTransaction(async)
        .push(TransactionExecuteType.FETCH, "SELECT * FROM `users` LIMIT 3")
        .asStream(Player.class)
        .mapToList()
        .toList();
```

Players-List Logger Output:
```
ID: 1 | UUID: 44dbc8fb-afe0-4592-b653-5defcbb6201f | Name: Misha
ID: 2 | UUID: df3419e9-ae0b-4ade-9d4c-ac1fb60c7fd7 | Name: Egor
ID: 3 | UUID: e1e26bfd-d827-4c7d-9ba8-4fcd80193df8 | Name: Sergey
```

---

## SUPPORT ME

<a href="https://www.buymeacoffee.com/itzstonlex" target="_blank"><img src="https://www.buymeacoffee.com/assets/img/custom_images/orange_img.png" alt="Buy Me A Coffee" style="height: 41px !important;width: 174px !important;box-shadow: 0px 3px 2px 0px rgba(190, 190, 190, 0.5) !important;-webkit-box-shadow: 0px 3px 2px 0px rgba(190, 190, 190, 0.5) !important;" ></a>
