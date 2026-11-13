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

    // Login Tests

    @Test
    public void LoginSuccess() throws ResponseException {
        facade.register(new UserData("player1", "password", "jo@gmail.com"));
        var authData = facade.login(new UserData("player1", "password", null));

        assertNotNull(authData);
        assertEquals("player1", authData.username());
        assertNotNull(authData.authToken());
    }

    @Test
    public void loginWrongPassword() throws ResponseException {
        facade.register(new UserData("player1", "password", "jo@gmail.com"));

        assertThrows(ResponseException.class, () -> facade.login(new UserData("player1", "wrong_password", null)));
    }

    // Logout Tests
    @Test
    public void logoutSuccess() throws ResponseException {
        var auth = facade.register(new UserData("player1", "password", "jo@gmail.com"));

        assertDoesNotThrow(() -> facade.logout(auth.authToken()));
    }

    @Test
    public void logoutInvalidToken() {
        assertThrows(ResponseException.class, () -> facade.logout("invalid-auth-token"));
    }

    

}
