package dataaccess;

import chess.ChessGame;
import dataaccess.sql.*;
import model.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class SQLDAOTests {
    
    private UserDAO userDAO;
    private AuthDAO authDAO;
    private GameDAO gameDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        userDAO = new SQLUserDAO();
        authDAO = new SQLAuthDAO();
        gameDAO = new SQLGameDAO();

        authDAO.clear();
        gameDAO.clear();
        userDAO.clear();
    }

    // UserDAO Tests

    @Test
    @DisplayName("Create User - Positive")
    public void createUserSuccess() throws DataAccessException {
        UserData newUser = new UserData("alice", "password123", "alice@byu.edu");
        UserData created = userDAO.createUser(newUser);

        assertNotNull(created);
        assertEquals("alice", created.username());

        UserData expected = userDAO.getUser("alice");
        assertNotNull(expected);
        assertEquals("alice", expected.username());
        assertEquals("alice@byu.edu", expected.email());
    }

    @Test
    @DisplayName("Create User - Negative")
    public void createUserDuplicate() throws DataAccessException {
        UserData newUser = new UserData("alice", "password123", "alice@byu.edu");
        userDAO.createUser(newUser);

        assertThrows(DataAccessException.class, () -> {
            userDAO.createUser(newUser);
        });
    }

    @Test
    @DisplayName("Get User - Positive")
    public void getUserSuccess() throws DataAccessException {
        UserData newUser = new UserData("alice", "password123", "alice@byu.edu");
        userDAO.createUser(newUser);

        UserData found = userDAO.getUser("alice");
        assertNotNull(found);
        assertEquals("alice", found.username());
    }

    @Test
    @DisplayName("Get User - Negative")
    public void getUserFail() throws DataAccessException {
        UserData found = userDAO.getUser("alice");
        assertNull(found);
    }

    @Test
    @DisplayName("Clear Users")
    public void clearUsers() throws DataAccessException {
        userDAO.createUser(new UserData("alice", "password123", "alice@byu.edu"));
        userDAO.clear();

        assertNull(userDAO.getUser("alice"));;
    }

    // AuthDAO Tests

    @Test
    @DisplayName("Create Auth - Positive")
    public void createAuthSuccess() throws DataAccessException {
        AuthData auth = authDAO.createAuth("alice");

        assertNotNull(auth);
        assertNotNull(auth.authToken());
        assertEquals("alice", auth.username());

        AuthData expected = authDAO.getAuth(auth.authToken());
        assertNotNull(expected);
        assertEquals("alice", expected.username());
    }

    @Test
    @DisplayName("Create Auth - Negative")
    public void createAuthFail() {
        assertThrows(DataAccessException.class, () -> {
            authDAO.createAuth(null);
        });
    }

    @Test
    @DisplayName("Get Auth - Positive")
    public void getAuthSuccess() throws DataAccessException {
        AuthData created = authDAO.createAuth("alice");
        AuthData found = authDAO.getAuth(created.authToken());
        assertNotNull(found);
        assertEquals(created.authToken(), found.authToken());
        assertEquals("alice", found.username());
    }

    @Test
    @DisplayName("Get Auth - Negative")
    public void getAuthFail() throws DataAccessException {
        AuthData found = authDAO.getAuth("invalid-token");
        assertNull(found);
    }

    @Test
    @DisplayName("Delete Auth - Positive")
    public void deleteAuthSuccess() throws DataAccessException {
        AuthData created = authDAO.createAuth("alice");
        assertNotNull(authDAO.getAuth(created.authToken()));

        authDAO.deleteAuth(created.authToken());

        assertNull(authDAO.getAuth(created.authToken()));
    }

    @Test
    @DisplayName("Delete Auth - Negative")
    public void deleteAuthFail() {
        assertDoesNotThrow(() -> {
            authDAO.deleteAuth("nonexistent-token");
        });
    }

    @Test
    @DisplayName("Clear Auth")
    public void clearAuth() throws DataAccessException {
        AuthData created = authDAO.createAuth("alice");

        authDAO.clear();

        assertNull(authDAO.getAuth(created.authToken()));
    }

    @Test
    @DisplayName("Create Game - Positive")
    public void createGameSuccess() throws DataAccessException {
        GameData newGame = new GameData(0, null, null, "Cool Game", new ChessGame());
        GameData created = gameDAO.createGame(newGame);

        assertNotNull(created);
        assertTrue(created.gameID() > 0, "Generated gameID should be greater than 0");
        assertEquals("Cool Game", created.gameName());
        assertNotNull(created.game(), "ChessGame board state shouldn't be null");

        GameData expected = gameDAO.getGame(created.gameID());
        assertNotNull(expected);
        assertEquals("Cool Game", expected.gameName());
    }

    @Test
    @DisplayName("Create Game - Negative")
    public void createGameFail() {
        GameData invalidGame = new GameData(0, null, null, null, new ChessGame());
        assertThrows(DataAccessException.class, () -> {
            gameDAO.createGame(invalidGame);
        };
    }
}