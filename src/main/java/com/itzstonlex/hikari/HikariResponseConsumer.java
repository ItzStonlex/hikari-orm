package com.itzstonlex.hikari;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface HikariResponseConsumer {

    void accept(ResultSet resultSet) throws SQLException;
}
