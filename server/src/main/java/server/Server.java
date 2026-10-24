package server;

import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.*;
import io.javalin.http.BadRequestResponse;
import model.*;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import io.javalin.http.Context;
import service.*;


public class Server {

    private final Javalin javalin;
    private final UserDAO userDAO = new MemoryUserDAO();
    private final AuthDAO authDAO = new MemoryAuthDAO();
    private final GameDAO gameDAO = new MemoryGameDAO();
    private final ClearService clearService = new ClearService(userDAO, authDAO, gameDAO);
    private final UserService userService = new UserService(userDAO, authDAO);
    private final GameService gameService = new GameService(gameDAO, authDAO);

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.

        javalin.delete("/db", this::clearDB);
        javalin.post("/user", this::register);
        javalin.post("/session", this::login);
        javalin.delete("/session", this::logout);
        javalin.post("/game", this::createGame);
        javalin.get("/game", this::listGames);
        javalin.put("/game", this::joinGame);
    }

    private void clearDB(@NotNull Context ctx) {
        try {
            clearService.clear();
            ctx.status(200);
            ctx.result("{}");
        } catch (DataAccessException e) {
            ctx.status(500);
            ctx.result(new Gson().toJson(Map.of("message", "Error: " + e.getMessage())));
        }
    }

    private void register(Context ctx) {
        try {
            UserData user = new Gson().fromJson(ctx.body(), UserData.class);
            AuthData auth = userService.register(user);
            ctx.status(200);
            ctx.result(new Gson().toJson(auth));
        } catch (BadRequestException e) {
            ctx.status(400);
            ctx.result(new Gson().toJson(Map.of("message", e.getMessage())));
        } catch (AlreadyTakenException e) {
            ctx.status(403);
            ctx.result(new Gson().toJson(Map.of("message", e.getMessage())));
        } catch (Exception e) {
            ctx.status(500);
            ctx.result(new Gson().toJson(Map.of("message", "Error: " + e.getMessage())));
        }
    }

    private void login(Context ctx) {
        try {
            UserData user = new Gson().fromJson(ctx.body(), UserData.class);
            AuthData auth = userService.login(user);
            ctx.status(200);
            ctx.result(new Gson().toJson(auth));
        } catch (BadRequestException e) {
            ctx.status(400);
            ctx.result(new Gson().toJson(Map.of("message", e.getMessage())));
        } catch (UnauthorizedException e) {
            ctx.status(401);
            ctx.result(new Gson().toJson(Map.of("message", e.getMessage())));
        } catch (Exception e) {
            ctx.status(500);
            ctx.result(new Gson().toJson(Map.of("message", "Error: " + e.getMessage())));
        }
    }

    private void logout(Context ctx) {
        try {
            String authToken = ctx.header("Authorization");
            userService.logout(authToken);
            ctx.status(200);
            ctx.result("{}");
        } catch (UnauthorizedException e) {
            ctx.status(401);
            ctx.result(new Gson().toJson(Map.of("message", e.getMessage())));
        } catch (Exception e) {
            ctx.status(500);
            ctx.result(new Gson().toJson(Map.of("message", "Error: " + e.getMessage())));
        }
    }

    private void createGame(Context ctx) {
        try {
            String authToken = ctx.header("Authorization");
            CreateGameRequest req = new Gson().fromJson(ctx.body(), CreateGameRequest.class);
            CreateGameResult result = gameService.createGame(authToken, req);
            ctx.status(200);
            ctx.result(new Gson().toJson(result));
        } catch (BadRequestException e) {
            ctx.status(400);
            ctx.result(new Gson().toJson(Map.of("message", e.getMessage())));
        } catch (UnauthorizedException e) {
            ctx.status(401);
            ctx.result(new Gson().toJson(Map.of("message", e.getMessage())));
        } catch (Exception e) {
            ctx.status(500);
            ctx.result(new Gson().toJson(Map.of("message", "Error: " + e.getMessage())));
        }
    }

    private void listGames(Context ctx) {
        try {
            String authToken = ctx.header("Authorization");
            GameData[] games = gameService.listGames(authToken);
            ctx.status(200);
            ctx.result(new Gson().toJson(Map.of("games", games)));
        } catch (UnauthorizedException e) {
            ctx.status(401);
            ctx.result(new Gson().toJson(Map.of("message", e.getMessage())));
        } catch (Exception e) {
            ctx.status(500);
            ctx.result(new Gson().toJson(Map.of("message", "Error: " + e.getMessage())));
        }
    }

    private void joinGame(Context ctx) {
        try {
            String authToken = ctx.header("Authorization");
            JoinGameRequest req = new Gson().fromJson(ctx.body(), JoinGameRequest.class);
            gameService.joinGame(authToken, req);
            ctx.status(200);
            ctx.result("{}");
        } catch (BadRequestException e) {
            ctx.status(400);
            ctx.result(new Gson().toJson(Map.of("message", e.getMessage())));
        } catch (UnauthorizedException e) {
            ctx.status(401);
            ctx.result(new Gson().toJson(Map.of("message", e.getMessage())));
        } catch (AlreadyTakenException e) {
            ctx.status(403);
            ctx.result(new Gson().toJson(Map.of("message", e.getMessage())));
        } catch (Exception e) {
            ctx.status(500);
            ctx.result(new Gson().toJson(Map.of("message", "Error: " + e.getMessage())));
        }
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
