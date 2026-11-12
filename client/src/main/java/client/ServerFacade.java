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

    public CreateGameResult createGame(String authToken, CreateGameRequest request) throws ResponseException {
        return makeRequest("POST", "/game", request, CreateGameResult.class, authToken);
    }

    public GameData[] listGames(String authToken) throws ResponseException {
        record ListGamesResponse(GameData[] games) {}
        var response = makeRequest("GET", "/game", null, ListGamesResponse.class, authToken);
        return response.games();
    }

    public void joinGame(String authToken, JoinGameRequest request) throws ResponseException {
        makeRequest("PUT", "/game", request, null, authToken);
    }

    public void clear() throws ResponseException {
        makeRequest("DELETE", "/db", null, null, null);
    }
}
