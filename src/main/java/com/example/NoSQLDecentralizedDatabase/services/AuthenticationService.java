package com.example.NoSQLDecentralizedDatabase.services;

import com.example.NoSQLDecentralizedDatabase.models.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class AuthenticationService {

    public static User findUser(String username, String password, String nodeIP) {
        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            throw new IllegalArgumentException("Email and password cannot be null or empty");
        }

        List<User> userList = (List<User>) DatalayerService.getList("/system", "users", User.class);

        for (User user : userList) {
            if (user.getName().equals(username)) {
                if (Objects.equals(HashingService.hash(password), user.getPassword())) {
                    if (user.getNodeIP().equals(nodeIP)) {
                        return user;
                    }
                }
            }
        }
        return null;
    }
    public static boolean findUser(String email, String password) {
        if (email == null || password == null || email.isEmpty() || password.isEmpty()) {
            throw new IllegalArgumentException("Email and password cannot be null or empty");
        }
        List<User> userList = (List<User>) DatalayerService.getList("/system", "users", User.class);
        for (User user : userList) {
            if (user.getEmail().equals(email)) {
                if (HashingService.hash(password).equals(user.getPassword())) {
                        return true;
                }
            }
        }
        return false;
    }
}
