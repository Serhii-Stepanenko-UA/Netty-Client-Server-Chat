package org.example.app;

import org.example.app.network.ChatClient;

import java.util.Scanner;

public class Main {

    static Scanner scanner;
    static String userName;

    public static void main(String[] args) throws Exception {
        start();
    }

    private static void start() throws Exception {
        scanner = new Scanner(System.in);
        System.out.println("Start to chat? Y/N");
        String option = scanner.nextLine();
        if (option.equalsIgnoreCase("Y")) {
            System.out.print("Please enter your name: ");
            if (scanner.hasNext()) {
                userName = scanner.nextLine();
                System.out.println("Welcome " + userName + ".\n" +
                        "Type your message to chat or type \"exit\" to leave the chat.");
            }
            ChatClient client = new ChatClient();
            client.start(scanner, userName);
        } else if (option.equalsIgnoreCase("N")){
            System.out.println("Bye.");
        } else {
            System.out.println("Unknown option.");
        }
    }
}
