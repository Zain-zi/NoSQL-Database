package com.example.NoSQLDecentralizedDatabase.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CollectionSchema {
    String collectionName;
    private List<PropertySchema> properties;

}
