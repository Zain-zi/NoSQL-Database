package com.example.NoSQLDecentralizedDatabase.services;

import com.example.NoSQLDecentralizedDatabase.models.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class FileService {
    private static final File path = new File("C:\\Users\\zainz\\IdeaProjects\\NoSQLDecentralizedDatabase\\src\\main\\java\\com\\example\\NoSQLDecentralizedDatabase\\storage");
    private static final File METADATA = new File("C:\\Users\\zainz\\IdeaProjects\\NoSQLDecentralizedDatabase\\src\\main\\java\\com\\example\\NoSQLDecentralizedDatabase\\METADATA.json");

    public static File getPath() {
        return path;
    }

    public static boolean createFolder(String folderPath, String folderName) {
        Path directory = Paths.get(path.toString(), folderPath);
        File folder = new File(directory.toString(), folderName);
        if (!folder.exists()) {
            return folder.mkdirs();
        }
        return false;
    }

    public static boolean deleteFolder(String folderPath, String folderName) {
        Path directory = Paths.get(path.toString(), folderPath);
        File folder = new File(directory.toString(), folderName);
        try (Stream<Path> paths = Files.walk(folder.toPath())) {
            paths.sorted(Comparator.reverseOrder()) // reverse order is to get the deepest content first
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (IOException e) {
            System.out.println("An issue in deleting the database has occurred");
            e.printStackTrace();
        }
        try {
            folder.delete();
            return true;
        } catch (RuntimeException e) {
            System.out.println("An issue in deleting the folder has occurred");
            e.printStackTrace();
        }
        return false;
    }

    public static List<String> getIDsFromMetadata(String collectionName) {
        List<String> nameValues = new ArrayList<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            File jsonFile = new File(String.valueOf(Paths.get(METADATA.toURI())));
            JsonNode jsonNode = objectMapper.readTree(jsonFile);
            nameValues = getMetaFileNames(jsonNode, collectionName);
            if (!nameValues.isEmpty()) {
                System.out.println("Names of '" + collectionName + "': " + nameValues);
            } else {
                System.out.println("No names found for '" + collectionName + "'.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return nameValues;
    }

    public static List<String> getMetaFileNames(JsonNode node, String targetName) {
        List<String> nameValues = new ArrayList<>();
        if (node.isObject()) {
            JsonNode nameNode = node.get("name");
            if (nameNode != null && nameNode.isTextual() && nameNode.asText().equals(targetName)) {
                JsonNode contentsNode = node.get("contents");
                if (contentsNode != null && contentsNode.isArray()) {
                    for (JsonNode itemNode : contentsNode) {
                        JsonNode itemNameNode = itemNode.get("name");
                        if (itemNameNode != null && itemNameNode.isTextual()) {
                            nameValues.add(itemNameNode.asText());
                        }
                    }
                }
            }
            for (JsonNode childNode : node) {
                nameValues.addAll(getMetaFileNames(childNode, targetName));
            }
        } else if (node.isArray()) {
            for (JsonNode arrayElement : node) {
                nameValues.addAll(getMetaFileNames(arrayElement, targetName));
            }
        }
        return nameValues;
    }

    public static void createJSONFileGivenPath(String givenPath, Object data) {
        String directory = String.valueOf(Paths.get(path.toString(), givenPath));
        createJSONFile(String.valueOf(directory), data);
    }

    public static boolean createJSONFile(String fileName, Object data) {
        try (FileWriter writer = new FileWriter(fileName)) {
            Gson gson = new Gson();
            gson.toJson(data, writer);
            System.out.println("JSON file created successfully.");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to create JSON file.");
            return false;
        }
    }

    public static <T> T readJSONFile(String filePath, String collectionName, Class<T> dataType, String fileName) {
        Path directory = Paths.get(path.toString(), filePath, collectionName);
        File folder = new File(directory.toString(), fileName);
        try (FileReader reader = new FileReader(folder)) {
            Gson gson = new Gson();
            return gson.fromJson(reader, dataType);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to read JSON file at: " + folder);
            return null;
        }
    }

    public static Optional<String> readProperty(String filePath, String collectionName, String fileName, String propertyName) {
        JsonObject jsonObject = readJSONFile(filePath, collectionName, JsonObject.class, fileName);
        assert jsonObject != null;
        if (jsonObject.has(propertyName)) {
            return Optional.of(jsonObject.get(propertyName).getAsString());
        } else {
            return Optional.empty(); // Property not found
        }
    }

    public static List<String> getJSONFileNames(String Path) {
        List<String> jsonFileNames = new ArrayList<>();
        File directory = new File(Path);

        // Check if the directory exists
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".json") && !name.toLowerCase().contains("schema"));

            if (files != null) {
                for (File file : files) {
                    jsonFileNames.add(file.getName());
                }
            }
        } else {
            System.err.println("Invalid directory path: " + Path);
        }

        return jsonFileNames;
    }

    public static String readJSONSchema(String filePath) {
        try {
            Path path = Paths.get(filePath);
            return new String(Files.readAllBytes(path));
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to read JSON schema file at: " + filePath);
            return null;
        }
    }

    public static void updateJSONFile(String fileName, Object newData) {
        try (FileReader reader = new FileReader(fileName)) {
            Gson gson = new Gson();
            JsonObject existingData = gson.fromJson(reader, JsonObject.class);

            if (existingData != null) {
                // Merge the existing data with the new data
                JsonObject newDataJson = gson.toJsonTree(newData).getAsJsonObject();
                existingData.entrySet().forEach(entry -> {
                    if (!newDataJson.has(entry.getKey())) {
                        newDataJson.add(entry.getKey(), entry.getValue());
                    }
                });

                // Write the merged JSON object back to the file
                try (FileWriter writer = new FileWriter(fileName)) {
                    gson.toJson(newDataJson, writer);
                    System.out.println("JSON file updated successfully at: " + fileName);
                }
            } else {
                System.err.println("Failed to update JSON file. JSON object is null.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to update JSON file at: " + fileName);
        }
    }

    public static boolean deleteJSONFile(String fileName, String fileID) {
        File file = new File(path, fileName + File.separator + fileID);

        if (file.exists() && file.isFile()) {
            boolean deleted = file.delete();
            if (deleted) {
                System.out.println("JSON file deleted successfully.");
                return true;
            } else {
                System.err.println("Failed to delete JSON file.");
            }
        } else {
            System.out.println("JSON file does not exist.");
        }
        return false;
    }

    private static JsonNode findDatabaseByName(JsonNode rootNode, String databaseName) {
        if (rootNode.isObject()) {
            return getJsonNode(rootNode, databaseName);
        }
        return null;
    }

    private static JsonNode findCollectionByName(JsonNode databaseNode, String collectionName) {
        if (databaseNode != null && databaseNode.has("contents")) {
            return getJsonNode(databaseNode, collectionName);
        }
        return null;
    }

    private static JsonNode getJsonNode(JsonNode databaseNode, String collectionName) {
        JsonNode contentsNode = databaseNode.get("contents");
        if (contentsNode != null && contentsNode.isArray()) {
            for (JsonNode node : contentsNode) {
                if (node.isObject() && node.has("name") && node.get("name").asText().equals(collectionName)) {
                    return node;
                }
            }
        }
        return null;
    }

    public static void updateSystemMetaData(String directoryName, String collectionName, String ID) {
        try {
            // Read the existing metadata file
            BufferedReader bufferedReader = new BufferedReader(new FileReader(String.valueOf(Paths.get(METADATA.toURI()))));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            bufferedReader.close();

            // Parse the JSON content
            JSONObject metadata = new JSONObject(stringBuilder.toString());

            // Find the 'users' directory
            JSONArray systemContents = metadata.getJSONArray("contents");
            for (int i = 0; i < systemContents.length(); i++) {
                JSONObject entry = systemContents.getJSONObject(i);
                if (entry.getString("name").equals(directoryName)) {
                    JSONArray systemContentsInner = entry.getJSONArray("contents");
                    for (int j = 0; j < systemContentsInner.length(); j++) {
                        JSONObject innerEntry = systemContentsInner.getJSONObject(j);
                        if (innerEntry.getString("name").equals(collectionName)) {
                            // Add the new file entry
                            JSONObject newFileEntry = new JSONObject();
                            newFileEntry.put("name", ID + ".json");
                            newFileEntry.put("type", "file");
                            innerEntry.getJSONArray("contents").put(newFileEntry);
                            break;
                        }
                    }
                    break;
                }
            }

            // Write the updated metadata back to the file
            FileWriter fileWriter = new FileWriter(String.valueOf(Paths.get(METADATA.toURI())));
            fileWriter.write(metadata.toString(4)); // Use 4 for indentation level
            fileWriter.close();

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    public static void addToMetadata(Object obj) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            // Read the existing METADATA.json file into a JsonNode
            File metadataFile = new File(String.valueOf(Paths.get(METADATA.toURI())));
            JsonNode rootNode = objectMapper.readTree(metadataFile);

            // Determine where to add the new object based on its type
            if (obj instanceof Database) {
                // Create a JsonNode representation of the new database
                JsonNode newDatabaseNode = objectMapper.valueToTree(obj);

                // Add the new database to the "contents" array of the root node
                ((ObjectNode) rootNode).withArray("contents").add(newDatabaseNode);
            } else if (obj instanceof Collection) {
                // Create a JsonNode representation of the new collection
                JsonNode newCollectionNode = objectMapper.valueToTree(obj);

                // Find the database where the collection should be added
                String databaseName = ((Collection) obj).getDatabaseName();
                JsonNode databaseNode = findDatabaseByName(rootNode, databaseName);

                if (databaseNode != null) {
                    // Add the new collection to the "contents" array of the database
                    if (!databaseNode.has("contents")) {
                        ((ObjectNode) databaseNode).set("contents", JsonNodeFactory.instance.arrayNode());
                    }
                    ((ArrayNode) databaseNode.get("contents")).add(newCollectionNode);
                }
            } else if (obj instanceof Document) {
                // Create a JsonNode representation of the new document
                JsonNode newDocumentNode = objectMapper.valueToTree(obj);

                // Find the collection where the document should be added
                String databaseName = ((Document) obj).getDatabaseName();
                String collectionName = ((Document) obj).getCollectionName();
                JsonNode databaseNode = findDatabaseByName(rootNode, databaseName);

                if (databaseNode != null) {
                    JsonNode collectionNode = findCollectionByName(databaseNode, collectionName);

                    if (collectionNode != null) {
                        // Add the new document to the "contents" array of the collection
                        if (!collectionNode.has("contents")) {
                            ((ObjectNode) collectionNode).set("contents", JsonNodeFactory.instance.arrayNode());
                        }
                        ((ArrayNode) collectionNode.get("contents")).add(newDocumentNode);
                    }
                }
            }

            // Write the updated JsonNode back to the METADATA.json file
            ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
            objectWriter.writeValue(metadataFile, rootNode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void removeFromMetadata(Object obj) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(METADATA);

            if (obj instanceof Database) {
                removeDatabase(rootNode, ((Database) obj).getName());
            } else if (obj instanceof Collection) {
                removeCollection(rootNode, (Collection) obj);
            } else if (obj instanceof Document) {
                removeDocument(rootNode, (Document) obj);
            }
            // Write the updated JsonNode back to the METADATA.json file
            ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
            objectWriter.writeValue(METADATA, rootNode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void removeDatabase(JsonNode rootNode, String databaseName) {
        findContent(rootNode, databaseName);
    }

    private static void findContent(JsonNode rootNode, String databaseName) {
        ArrayNode contents = (ArrayNode) rootNode.withArray("contents");
        for (int i = 0; i < contents.size(); i++) {
            JsonNode databaseNode = contents.get(i);
            if (databaseNode.has("name") && databaseNode.get("name").asText().equals(databaseName)) {
                contents.remove(i);
                break;
            }
        }
    }

    private static void removeCollection(JsonNode rootNode, Collection collection) {
        String databaseName = collection.getDatabaseName();
        String collectionName = collection.getName();
        JsonNode databaseNode = findDatabaseByName(rootNode, databaseName);

        if (databaseNode != null) {
            findContent(databaseNode, collectionName);
        }
    }

    private static void removeDocument(JsonNode rootNode, Document document) {
        String databaseName = document.getDatabaseName();
        String collectionName = document.getCollectionName();
        String documentId = document.getID();

        JsonNode databaseNode = findDatabaseByName(rootNode, databaseName);

        if (databaseNode != null) {
            JsonNode collectionNode = findCollectionByName(databaseNode, collectionName);

            if (collectionNode != null && collectionNode.has("contents")) {
                ArrayNode contents = (ArrayNode) collectionNode.get("contents");

                for (int i = 0; i < contents.size(); i++) {
                    JsonNode documentNode = contents.get(i);

                    if (documentNode.has("id") && documentNode.get("id").asText().equals(documentId)) {
                        contents.remove(i);
                        break;
                    }
                }
            }
        }
    }

    public static void clearAssignedUsers() {
        File folder = new File(String.valueOf(path), File.separator + "system" + File.separator + "nodes");
        System.out.println(folder);

        File[] files = folder.listFiles();

        // Iterate through each file in the directory
        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".json")) {
                try {
                    // Read the JSON content from the file
                    String content = new String(Files.readAllBytes(file.toPath()));
                    JSONObject json = new JSONObject(content);

                    // Clear the assignedUsers array
                    json.put("assignedUsers", new JSONArray());

                    // Write the modified JSON back to the file
                    FileWriter writer = new FileWriter(file);
                    writer.write(json.toString());
                    writer.close();
                } catch (IOException e) {
                    System.err.println("Error processing file: " + file.getName());
                    e.printStackTrace();
                }
            }
        }
    }
}

