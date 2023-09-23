package com.example.NoSQLDecentralizedDatabase.controllers;

import ch.qos.logback.core.joran.conditional.ThenAction;
import com.example.NoSQLDecentralizedDatabase.services.DocumentsService;
import com.example.NoSQLDecentralizedDatabase.services.FileService;
import com.example.NoSQLDecentralizedDatabase.services.FormGeneratorService;
import com.example.NoSQLDecentralizedDatabase.services.NodeService;
import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Controller
public class DocumentsController {

    @GetMapping("/documents")
    public String getPage() {
        return "userDashboard";
    }

    @GetMapping( "/createDocument/{databaseName}/{collectionName}")
    public String getForm(@PathVariable String databaseName, @PathVariable String collectionName) {
        if (NodeService.isBootstrap()) {
            System.out.println("BAD REQUEST");
        }
        String schema = FileService.readJSONSchema(FileService.getPath() + File.separator + databaseName + File.separator + collectionName + File.separator + collectionName + "_schema.json");
        String formHtml = FormGeneratorService.generateFormFromSchema(schema, databaseName);
        try {
            File tempFile = new File("src/main/resources/templates", "form.html");
            FileWriter writer = new FileWriter(tempFile);
            writer.write(formHtml);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return "form";
    }

    @PostMapping("/documents/{databaseName}/{collectionName}")
    public ResponseEntity<Void> createDocument(@PathVariable String databaseName, @PathVariable String collectionName, @RequestBody String data) {
        if (NodeService.isBootstrap()) {
            System.out.println("BAD REQUEST");
        }
        boolean created = DocumentsService.createDocument(databaseName, collectionName, data);
        if (created) {
            System.out.println("File given form");
            return new ResponseEntity<>(HttpStatus.CREATED);
        } else {
            System.out.println("Error creating document form.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    @DeleteMapping("/deleteDocument")
    public ResponseEntity<Void> deleteDocument(@RequestParam String databaseName, @RequestParam String collectionName, @RequestParam String documentId) {
        if (NodeService.isBootstrap()) {
            System.out.println("BAD REQUEST");
        }
        boolean deleted = DocumentsService.deleteDocument(databaseName, collectionName, documentId);
        if (deleted) {
            System.out.println("Deleted file with ID " + documentId);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } else {
            System.out.println("Issue deleting file with ID + " + documentId);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/readDocument")
    public ResponseEntity<String> readDocument(@RequestParam String databaseName, @RequestParam String collectionName, @RequestParam String documentId) {
        if (NodeService.isBootstrap()) {
            System.out.println("BAD REQUEST");
        }
        String document = DocumentsService.readDocument(databaseName, collectionName, documentId);
        if (document != null) {
            System.out.println(document);
            return new ResponseEntity<>(document, HttpStatus.OK);
        } else {
            System.out.println("Issue retrieving document.");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/getAllDocuments")
    public ResponseEntity<Map<String, String>> getAllDocumentsInCollection(@RequestParam String databaseName, @RequestParam String collectionName) {
        if (NodeService.isBootstrap()) {
            System.out.println("BAD REQUEST");
        }
        Map<String, JsonObject> JsonFiles = DocumentsService.getAllDocumentsInCollection(databaseName, collectionName);
        Map<String, String> stringJsonFiles = new HashMap<>();
        for (Map.Entry<String, JsonObject> entry : JsonFiles.entrySet()) {
            stringJsonFiles.put(entry.getKey(), entry.getValue().toString());
        }
        for (Map.Entry<String, JsonObject> entry : JsonFiles.entrySet()) {
            System.out.println("File ID: " + entry.getKey());
            System.out.println("File: " + entry.getValue());
            System.out.println();
        }
        return new ResponseEntity<>(stringJsonFiles, HttpStatus.OK);
    }

    @GetMapping("/readProperty")
    public ResponseEntity<String> readProperty(@RequestParam String databaseName, @RequestParam String collectionName, @RequestParam String documentId, @RequestParam String propertyName) {
        if (NodeService.isBootstrap()) {
            System.out.println("BAD REQUEST");
        }
        String propertyValue = (DocumentsService.readProperty(databaseName, collectionName, documentId, propertyName));
        if (!propertyValue.equals("not found")) {
            String value = ("Value of " + propertyName + " is " + propertyValue);
            return new ResponseEntity<>(value, HttpStatus.OK);
        } else {
            System.out.println("Issue reading property.");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/updateProperty")
    public ResponseEntity<String> updateProperty(@RequestParam String databaseName, @RequestParam String collectionName, @RequestParam String documentId, @RequestParam String propertyName, @PathVariable String updatedValue) {
        if (NodeService.isBootstrap()) {
            System.out.println("BAD REQUEST");
        }
        boolean updated = DocumentsService.updateProperty(databaseName, collectionName, documentId, propertyName, updatedValue);
        if (updated) {
            System.out.println("Document successfully updated.");
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            System.out.println("Issue in updating the document.");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
