package com.example.NoSQLDecentralizedDatabase.models;

public class Credentials {
    private final String username;
    private final String password;
    private final String nodeIPAddress;

    public Credentials(String email, String password, String nodeIPAddress) {
        this.username = email;
        this.password = password;
        this.nodeIPAddress = nodeIPAddress;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
    public String getNodeIPAddress() { return nodeIPAddress; }
}
