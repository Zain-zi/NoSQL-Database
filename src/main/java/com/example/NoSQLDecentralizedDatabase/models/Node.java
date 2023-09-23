package com.example.NoSQLDecentralizedDatabase.models;

import lombok.*;

import java.util.ArrayList;
import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Node {
    String nodeID;
    String IPAddress;
    List<Database> databaseList;
    List<User> assignedUsers;
}
