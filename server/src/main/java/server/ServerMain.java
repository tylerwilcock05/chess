package server;

import chess.*;

public class ServerMain {
    public static void main(String[] args) {
        var port = 7070;
        Server server = new Server();
        server.run(port);

        System.out.println("♕ 240 Chess Server running on port " + port);
    }
}
