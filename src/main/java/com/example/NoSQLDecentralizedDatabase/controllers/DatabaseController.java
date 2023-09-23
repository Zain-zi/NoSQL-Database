package com.example.NoSQLDecentralizedDatabase.controllers;

import com.example.NoSQLDecentralizedDatabase.models.CollectionSchema;
import com.example.NoSQLDecentralizedDatabase.services.DatabaseService;
import com.example.NoSQLDecentralizedDatabase.services.NodeService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@Controller
public class DatabaseController {

    @GetMapping("/database")
    public String getAdminPage() {
        return "adminDashboard";
    }
    @GetMapping("/createDatabase")
    public String showCreateDatabasePage() {
        if (NodeService.isBootstrap()) {
            System.out.println("BAD REQUEST");
        }
        return "createDatabase";
    }

    @PostMapping("/createDatabase")
    public ResponseEntity<Void> createDatabase(@RequestParam ("databaseName") String databaseName, @RequestParam("numCollections") int numCollections, HttpServletRequest request) {
        if (NodeService.isBootstrap()) {
            System.out.println("BAD REQUEST");
        }
        List<CollectionSchema> collectionSchemas = DatabaseService.getCollectionSchemas(numCollections, request);
        boolean created = DatabaseService.createDatabase(databaseName, collectionSchemas);
        if (created) {
            System.out.println("Database with the name " + databaseName + " successfully created.");
            return new ResponseEntity<>(HttpStatus.CREATED);
        } else {
            System.out.println("Unable to create database.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/deleteDatabase")
    public ResponseEntity<Void> deleteDatabase(@RequestParam String databaseName) {
        if (NodeService.isBootstrap()) {
            System.out.println("BAD REQUEST");
        }
        boolean deleted = DatabaseService.deleteDatabase(databaseName);
        if (deleted) {
            System.out.println("Database with the name " + databaseName + " successfully deleted.");
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            System.out.println("Unable to delete database.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/read/{databaseName}")
    public void showAllDataBases(@PathVariable String databaseName) {
        // databaseService logic of returning the databases to showcase them to the users
    }
}
