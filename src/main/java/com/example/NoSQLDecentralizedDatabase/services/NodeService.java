package com.example.NoSQLDecentralizedDatabase.services;

import lombok.Setter;

public class NodeService {
    private static boolean isBootstrap = false;

    public static boolean isBootstrap() {
        return isBootstrap;
    }

    public static void setIsBootstrap(boolean isBootstrap) {
        NodeService.isBootstrap = isBootstrap;
    }
}
