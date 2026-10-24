package server;

import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.*;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import io.javalin.http.Context;
import service.ClearService;


public class Server {

    private final Javalin javalin;
    private final UserDAO userDAO = new MemoryUserDAO();
    private final AuthDAO authDAO = new MemoryAuthDAO();
    private final GameDAO gameDAO = new MemoryGameDAO();
    private final ClearService clearService = new ClearService(userDAO, authDAO, gameDAO);

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.

        javalin.delete("/db", this::clearDB);
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


    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
