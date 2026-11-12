package client;

import chess.ChessGame;
import model.*;

public class ServerFacade {
    private final String serverUrl;

    public ServerFacade(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public ServerFacade(int port) {
        this("http://localhost:" + port);
    }

    public AuthData register(UserData user) throws ResponseException {
        return makeRequest("POST", "/user", user, AuthData.class, null);
    }

    public AuthData login(UserData user) throws ResponseException {
        return makeRequest("POST", "/session", user, AuthData.class, null);
    }

    public void logout(String authToken) throws ResponseException {
        makeRequest("DELETE", "/session", null, null, authToken);
    }

    
}
