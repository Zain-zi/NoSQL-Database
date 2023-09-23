package com.example.NoSQLDecentralizedDatabase.models;

import com.example.NoSQLDecentralizedDatabase.security.Role;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class User {
    private String ID;
    private String name;
    private String password;
    private String email;
    private Role role;
    private String nodeIP;

}
