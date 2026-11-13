package client;

import chess.ChessGame;
import model.*;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

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

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, String authToken) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);

            if (authToken != null && !authToken.isEmpty()) {
                http.addRequestProperty("Authorization", authToken);
            }

            if (request != null) {
                http.setDoOutput(true);
                http.addRequestProperty("Content-Type", "application/json");
                try (var outputStream = http.getOutputStream()) {
                    outputStream.write(new Gson().toJson(request).getBytes(StandardCharsets.UTF_8));
                }
            }

            http.connect();
            throwIfNotSuccessful(http);

            if (responseClass != null) {
                try (var stream = http.getInputStream()) {
                    return new Gson().fromJson(new InputStreamReader(stream), responseClass);
                }
            }
            return null;
        } catch (ResponseException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseException(500, e.getMessage());
        }
    }
}

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        int status = http.getResponseCode();
        if (status >= 200 && status < 300) {
            return;
        }

        String message = "Error: " + status;
        try (var stream = http.getErrorStream()) {
            if (stream != null) {
                var errorMap = new Gson().fromJson(new InputStreamReader(stream), Map.class);
                var serverMsg = (errorMap != null) ? errorMap.get("message") : null;
                message = (serverMsg != null) ? serverMsg.toString() : message;
            }
        }
        throw new ResponseException(status, message);
    }
