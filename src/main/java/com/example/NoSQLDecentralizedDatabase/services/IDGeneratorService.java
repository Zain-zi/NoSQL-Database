package com.example.NoSQLDecentralizedDatabase.services;

import java.util.UUID;

public class IDGeneratorService {
    public static String createId() {
        return UUID.randomUUID().toString();
    }
}
