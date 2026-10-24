package server;

import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.Javalin;
import io.javalin.http.Context;
import model.*;
import org.jetbrains.annotations.NotNull;
import service.*;

import java.util.Map;

public class Server {

    private final Javalin javalin;
    private final Gson gson = new Gson();
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
            sendError(ctx, 500, "Error: " + e.getMessage());
        }
    }

    private void register(Context ctx) {
        try {
            UserData user = gson.fromJson(ctx.body(), UserData.class);
            AuthData auth = userService.register(user);
            ctx.status(200);
            ctx.result(gson.toJson(auth));
        } catch (BadRequestException e) {
            sendError(ctx, 400, e.getMessage());
        } catch (AlreadyTakenException e) {
            sendError(ctx, 403, e.getMessage());
        } catch (Exception e) {
            sendError(ctx, 500, "Error: " + e.getMessage());
        }
    }

    private void login(Context ctx) {
        try {
            UserData user = gson.fromJson(ctx.body(), UserData.class);
            AuthData auth = userService.login(user);
            ctx.status(200);
            ctx.result(gson.toJson(auth));
        } catch (BadRequestException e) {
            sendError(ctx, 400, e.getMessage());
        } catch (UnauthorizedException e) {
            sendError(ctx, 401, e.getMessage());
        } catch (Exception e) {
            sendError(ctx, 500, "Error: " + e.getMessage());
        }
    }

    private void logout(Context ctx) {
        try {
            String authToken = ctx.header("Authorization");
            userService.logout(authToken);
            ctx.status(200);
            ctx.result("{}");
        } catch (UnauthorizedException e) {
            sendError(ctx, 401, e.getMessage());
        } catch (Exception e) {
            sendError(ctx, 500, "Error: " + e.getMessage());
        }
    }

    private void createGame(Context ctx) {
        try {
            String authToken = ctx.header("Authorization");
            CreateGameRequest req = gson.fromJson(ctx.body(), CreateGameRequest.class);
            CreateGameResult result = gameService.createGame(authToken, req);
            ctx.status(200);
            ctx.result(gson.toJson(result));
        } catch (BadRequestException e) {
            sendError(ctx, 400, e.getMessage());
        } catch (UnauthorizedException e) {
            sendError(ctx, 401, e.getMessage());
        } catch (Exception e) {
            sendError(ctx, 500, "Error: " + e.getMessage());
        }
    }

    private void listGames(Context ctx) {
        try {
            String authToken = ctx.header("Authorization");
            GameData[] games = gameService.listGames(authToken);
            ctx.status(200);
            ctx.result(gson.toJson(Map.of("games", games)));
        } catch (UnauthorizedException e) {
            sendError(ctx, 401, e.getMessage());
        } catch (Exception e) {
            sendError(ctx, 500, "Error: " + e.getMessage());
        }
    }

    private void joinGame(Context ctx) {
        try {
            String authToken = ctx.header("Authorization");
            JoinGameRequest req = gson.fromJson(ctx.body(), JoinGameRequest.class);
            gameService.joinGame(authToken, req);
            ctx.status(200);
            ctx.result("{}");
        } catch (BadRequestException e) {
            sendError(ctx, 400, e.getMessage());
        } catch (UnauthorizedException e) {
            sendError(ctx, 401, e.getMessage());
        } catch (AlreadyTakenException e) {
            sendError(ctx, 403, e.getMessage());
        } catch (Exception e) {
            sendError(ctx, 500, "Error: " + e.getMessage());
        }
    }

    private void sendError(Context ctx, int statusCode, String message) {
        ctx.status(statusCode);
        ctx.result(gson.toJson(Map.of("message", message)));
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
