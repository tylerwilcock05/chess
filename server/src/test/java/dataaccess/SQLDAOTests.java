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
}