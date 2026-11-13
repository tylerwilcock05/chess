package ui;

import java.util.Scanner;

public class Repl {
    private final ChessClient client;

    public Repl(String serverUrl) {
        this.client = new ChessClient(serverUrl);
    }

    public void run() {
        System.out.println("♕ 240 Chess Client. Type 'help' for a list of commands.");
        Scanner scanner = new Scanner(System.in);
        String result = "";

        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.print(result);
            } catch (Throwable e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        System.out.println("Thanks for playing!");
    }

    private void printPrompt() {
        System.out.print("\n" + client.getStatePrompt() + " >>> ");
    }
}