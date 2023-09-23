package com.example.NoSQLDecentralizedDatabase.services;

import com.example.NoSQLDecentralizedDatabase.models.Collection;
import org.springframework.stereotype.Service;

@Service
public class CollectionsService {
    public static boolean createCollection(String databaseName, String collectionName) {
        boolean created = FileService.createFolder(databaseName, collectionName);
        if (created) {
            FileService.addToMetadata(new Collection(collectionName, databaseName));
            return true;
        }
        return false;
    }

    public static boolean deleteCollection(String databaseName, String collectionName) {
        boolean deleted =  FileService.deleteFolder(databaseName, collectionName);
        if (deleted) {
            FileService.removeFromMetadata(new Collection(collectionName, databaseName));
            return true;
        }
        return false;
    }
}
