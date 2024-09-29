package org.example.app;

import org.example.app.network.ChatServer;

public class Main {
    public static void main(String[] args) throws Exception {
        ChatServer server = new ChatServer();
        server.start();
    }
}
