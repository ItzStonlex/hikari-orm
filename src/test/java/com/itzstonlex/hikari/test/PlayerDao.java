package com.itzstonlex.hikari.test;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

import java.util.UUID;

@Data
@ToString
@FieldDefaults(makeFinal = true)
public class PlayerDao {

    // "transient" keyword is required here to exclude the field from serialization for SQL queries.
    @NonFinal
    private transient long id;

    private UUID uuid;
    private String name;
}
