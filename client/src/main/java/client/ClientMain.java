package client;

import chess.*;
import ui.BoardRenderer;
import ui.Repl;

public class ClientMain {
    public static void main(String[] args) {
        var serverUrl = "http://localhost:7070";
        if (args.length == 1) {
            serverUrl = args[0];
        }

        new Repl(serverUrl).run();
    }
}
