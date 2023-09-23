package com.example.NoSQLDecentralizedDatabase.controllers;

import com.example.NoSQLDecentralizedDatabase.services.CollectionsService;
import com.example.NoSQLDecentralizedDatabase.services.NodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class CollectionsController {

    @PostMapping("/createCollection")
    public ResponseEntity<Void> createCollection(@RequestParam String databaseName, @RequestParam String collectionName) {
        if (NodeService.isBootstrap()) {
            System.out.println("BAD REQUEST");
        }
        boolean created = CollectionsService.createCollection(databaseName, collectionName);
        if (created) {
            System.out.println("Collection with the name " + collectionName + " successfully created in the database " + databaseName + ".");
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            System.out.println("Unable to create collection.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/deleteCollection")
    public ResponseEntity<Void> deleteCollection(@RequestParam String databaseName, @RequestParam String collectionName) {
        if (NodeService.isBootstrap()) {
            System.out.println("BAD REQUEST");
        }
        boolean deleted = CollectionsService.deleteCollection(databaseName, collectionName);
        if (deleted) {
            System.out.println("Collection with the name " + collectionName + " successfully deleted from the database " + databaseName + ".");
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            System.out.println("Unable to delete collection.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/readCollection")
    public void getCollection(@RequestParam String databaseName, @RequestParam String collectionName) {

    }
}
