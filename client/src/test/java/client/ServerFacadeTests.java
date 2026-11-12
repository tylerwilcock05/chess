package client;

import org.junit.jupiter.api.*;
import server.Server;
import chess.ChessGame;
import model.*;
import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static Server server;
    public static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }
    
    @BeforeEach
    public void clear() throws ResponseException {
        facade.clear();
    }

    // Register Tests

    @Test
    public void registerSuccess() throws ResponseException {
        var user = new UserData("player1", "password", "jo@gmail.com");
        var authData = facade.register(user);

        assertNotNull(authData);
        assertEquals("player1", authData.username());
        assertNotNull(authData.authToken());
        assertFalse(authData.authToken().isBlank());
    }

    @Test
    public void registerDuplicateUsername() throws ResponseException {
        var user = new UserData("player1", "password", "jo@gmail.com");
        facade.register(user);

        assertThrows(ResponseException.class, () -> facade.register(user));
    }

}
