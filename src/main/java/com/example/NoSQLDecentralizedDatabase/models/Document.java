package com.example.NoSQLDecentralizedDatabase.models;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class Document {
    String ID;
    String collectionName;
    String databaseName;
}
