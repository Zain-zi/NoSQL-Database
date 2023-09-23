package com.example.NoSQLDecentralizedDatabase.services;

import com.example.NoSQLDecentralizedDatabase.models.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class DatabaseService {
    public static boolean createDatabase(String databaseName,List<CollectionSchema> collectionSchemas) {
        if (FileService.createFolder("", databaseName)) {
            Database database = new Database(databaseName);
            FileService.addToMetadata(database);
            for (CollectionSchema collectionSchema : collectionSchemas) {
                String collectionName = collectionSchema.getCollectionName();
                CollectionsService.createCollection(databaseName, collectionName);
                FileService.createJSONFileGivenPath(databaseName + File.separator + collectionName + File.separator + collectionName + "_schema.json", collectionSchema);
                FileService.addToMetadata(new Document(collectionName + "_schema", collectionName, databaseName));
            }
            return true;
        }
        return false;
    }

    public static boolean deleteDatabase(String databaseName) {
        boolean deleted = FileService.deleteFolder("", databaseName);
        if (deleted) {
            FileService.removeFromMetadata(new Database(databaseName));
            return true;
        }
        return false;
    }
    public static List<CollectionSchema> getCollectionSchemas(int numCollections, HttpServletRequest request) {
        List<CollectionSchema> collectionSchemas = new ArrayList<>();
        for (int i = 0; i < numCollections; i++) {
            int numProperties = Integer.parseInt(request.getParameter("numProperties" + i));
            List<PropertySchema> propertySchemas = new ArrayList<>();
            for (int j = 0; j < numProperties; j++) {
                String propertyName = request.getParameter("propertyName" + i + "-" + j);
                String propertyType = request.getParameter("propertyType" + i + "-" + j);
                PropertySchema propertySchema = new PropertySchema(propertyName, propertyType);
                propertySchemas.add(propertySchema);
            }
            CollectionSchema collectionSchema = new CollectionSchema(request.getParameter("collectionName" + i), propertySchemas);
            collectionSchemas.add(collectionSchema);
        }
        return collectionSchemas;
    }
}
