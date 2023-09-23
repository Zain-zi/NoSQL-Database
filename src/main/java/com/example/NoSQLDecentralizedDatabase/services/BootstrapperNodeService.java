package com.example.NoSQLDecentralizedDatabase.services;

import com.example.NoSQLDecentralizedDatabase.models.Node;
import com.example.NoSQLDecentralizedDatabase.models.User;
import com.example.NoSQLDecentralizedDatabase.security.Role;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@NoArgsConstructor
@Service
public class BootstrapperNodeService {
    static List<Node> nodes;
    static List<User> users;
    static String systemPath = "/system";
    static String userCollection = "users";
    static String nodeCollection = "nodes";
    static int currentNode;

    public static List<Node> getNodes() {
        return nodes;
    }

    public static List<User> getUsers() {
        return users;
    }

    public static void start() {
        clearAssignedUsersInNodes();
        initializeUsers();
        initializeNodes();
        balanceNodes();
        NodeService.setIsBootstrap(true);
        try {
            for(int i = 0; i <= nodes.size(); i++) {
                exec("docker",
                        "run",
                        "-d",
                        "-v volume:/path",
                        "-p", 8000 + i + ":8081",
                        "--name", "node_" + i,
                        "--network", "cluster",
                        "--ip", "10.1.4." + i,
                        "-e BOOTSTRAPPER=false",
                        "DB");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void exec(String... args) throws IOException {
        boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
        List<String> commands;
        if (isWindows)
            commands = Stream.of("cmd.exe", "/c").collect(Collectors.toList());
        else
            commands = Stream.of("sh", "-c").collect(Collectors.toList());
        for (String cmd : args)
            commands.add(cmd);
        ProcessBuilder builder = new ProcessBuilder(commands);
        builder.start();
    }
    private static void clearAssignedUsersInNodes() {
        FileService.clearAssignedUsers();
    }

    public static String createUser(String name, String email, String password) {
        if (AuthenticationService.findUser(email, password)) {
            return null;
        } else {
            Node node = nodes.get(currentNode);
            User user = new User(IDGeneratorService.createId(),name, HashingService.hash(password), email, Role.SYSTEM_USER, node.getIPAddress());
            DatalayerService.create(user, user.getID(), "users");
            users.add(user);
            node.getAssignedUsers().add(user);
            FileService.updateJSONFile(FileService.getPath() + "/system/nodes/" + node.getNodeID() + ".json", node);
            currentNode = (currentNode + 1) % nodes.size();
            return node.getIPAddress();
        }
    }

    private static void initializeUsers() {
        users = (List<User>) DatalayerService.getList(systemPath, userCollection, User.class);
        for (User user:
             users) {
            System.out.println(user);
        }
    }

    private static void initializeNodes() {
        nodes = (List<Node>) DatalayerService.getList(systemPath, nodeCollection, Node.class);
    }

    public static void balanceNodes() {
        currentNode = LoadBalancerService.balanceUsersToNodes(users, nodes);
    }
}

