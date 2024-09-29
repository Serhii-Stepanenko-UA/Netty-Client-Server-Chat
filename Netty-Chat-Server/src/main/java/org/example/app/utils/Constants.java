package org.example.app.utils;

public final class Constants {

    private Constants() {
    }

    // Порт, на якому сервер буде прослуховувати з'єднання
    public static final int PORT = 8003;
    public static final String SERVER_START_MSG = "Server started and " +
            "waiting for clients...";
    public static final String CLIENT_CLOSE_MSG = "exit";
}
