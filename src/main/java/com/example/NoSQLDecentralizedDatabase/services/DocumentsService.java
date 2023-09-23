package com.example.NoSQLDecentralizedDatabase.services;

import com.example.NoSQLDecentralizedDatabase.models.Document;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

@Service
public class DocumentsService {
    public static boolean createDocument(String databaseName, String collectionName, String data) {
        Gson gson = new Gson();
        String ID = IDGeneratorService.createId();
        JsonObject jsonObject = gson.fromJson(data, JsonObject.class);
        boolean created = FileService.createJSONFile(FileService.getPath() + File.separator + databaseName + File.separator + collectionName + File.separator + ID + ".json", jsonObject);
        if (created) {
            FileService.addToMetadata(new Document(ID, collectionName, databaseName));
            return true;
        }
        return false;
    }
    public static boolean deleteDocument(String databaseName, String collectionName, String documentId) {
        boolean deleted = FileService.deleteJSONFile(databaseName + File.separator + collectionName, documentId + ".json");
        if (deleted) {
            FileService.removeFromMetadata(new Document(documentId, collectionName, databaseName));
            return true;
        }
        return false;
    }
    public static String readDocument(String databaseName, String collectionName, String documentId) {
        return String.valueOf(FileService.readJSONFile(databaseName, collectionName, JsonObject.class, documentId + ".json"));
    }
    public static String readProperty(String databaseName, String collectionName, String documentId, String propertyName) {
        Optional<String> propertyValue =  FileService.readProperty(databaseName, collectionName, documentId + ".json", propertyName);
        return propertyValue.orElse("not found.");
    }
    public static boolean updateProperty(String databaseName, String collectionName, String documentId, String propertyName, String updatedValue) {
        JsonObject jsonObject =  FileService.readJSONFile(databaseName, collectionName, JsonObject.class, documentId + ".json");
        assert jsonObject != null;
        jsonObject.addProperty(propertyName, updatedValue);
        if (deleteDocument(databaseName, collectionName, documentId)) {
            FileService.createJSONFile(FileService.getPath() + File.separator + databaseName + File.separator + collectionName + File.separator + documentId + ".json", jsonObject);
            return true;
        }
        return false;
    }
    public static Map<String, JsonObject> getAllDocumentsInCollection(String databaseName, String collectionName) {
        Map<String, JsonObject> JsonFiles = new HashMap<>();
        Gson gson = new Gson();
        List<String> JsonFilesIDs = FileService.getJSONFileNames(FileService.getPath() +  File.separator + databaseName + File.separator + collectionName);
        for (String ID: JsonFilesIDs) {
            JsonFiles.put(ID, gson.fromJson(String.valueOf(FileService.readJSONFile(databaseName, collectionName, JsonObject.class, ID)), JsonObject.class));
        }
        return JsonFiles;
    }
}
