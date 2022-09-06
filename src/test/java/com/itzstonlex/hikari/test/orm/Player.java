package com.itzstonlex.hikari.test.orm;

import lombok.Data;
import lombok.ToString;

import java.util.UUID;

@ToString
@Data
public class Player {

    private final long id;

    private final UUID uuid;
    private final String name;
}
