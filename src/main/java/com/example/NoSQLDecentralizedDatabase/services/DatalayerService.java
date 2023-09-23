package com.example.NoSQLDecentralizedDatabase.services;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DatalayerService {
    public static List<?> getList(String path, String collectionName, Class<?> dataType) {
        List<Object> list = new ArrayList<>();
        List<String> IDs = FileService.getIDsFromMetadata(collectionName);
        for (String ID : IDs) {
            Object object = FileService.readJSONFile(path, collectionName, dataType, ID);
            list.add(object);
        }
        return list;
    }
    public static <T> void create(T object, String ID, String collectionName) {
        boolean created = FileService.createJSONFile(FileService.getPath() + "/system/" + collectionName + "/" + ID + ".json", object);
        if (created) {
            FileService.updateSystemMetaData("system" , collectionName, ID);
        }
    }
}
