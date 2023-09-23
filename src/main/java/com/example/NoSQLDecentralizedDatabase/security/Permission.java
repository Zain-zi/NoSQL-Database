package com.example.NoSQLDecentralizedDatabase.security;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Permission {
    private boolean createDatabase;
    private boolean deleteDatabase;
    private boolean createCollection;
    private boolean deleteCollection;
    private boolean createDocument;
    private boolean deleteDocument;
    private boolean updateDocument;
    private boolean readDocument;


     public void setSystemAdminPermissions() {
         this.createDatabase = true;
         this.deleteDatabase = true;
         this.createCollection = true;
         this.deleteCollection = true;
         this.createDocument = true;
         this.deleteDocument = true;
         this.updateDocument = true;
         this.readDocument = true;
     }

     public void setSystemUserPermissions() {
         this.createDatabase = false;
         this.deleteDatabase = false;
         this.createCollection = false;
         this.deleteCollection = false;
         this.createDocument = true;
         this.deleteDocument = true;
         this.updateDocument = true;
         this.readDocument = true;
     }
}

