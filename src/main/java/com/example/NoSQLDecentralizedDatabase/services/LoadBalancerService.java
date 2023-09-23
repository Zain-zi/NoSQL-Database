package com.example.NoSQLDecentralizedDatabase.services;

import com.example.NoSQLDecentralizedDatabase.models.Node;
import com.example.NoSQLDecentralizedDatabase.models.User;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@NoArgsConstructor
public class LoadBalancerService {

    public static int balanceUsersToNodes(List<User> users, List<Node> nodes) {
        int numNodes = nodes.size();
        int numUsers = users.size();

        for (int i = 0; i < numUsers; i++) {
            Node node = nodes.get(i % numNodes);
            User user = users.get(i);
            node.getAssignedUsers().add(user);
            FileService.updateJSONFile(FileService.getPath() + "/system/nodes/" + node.getNodeID() + ".json", node);
        }
        return  (numUsers % numNodes);
    }
}
