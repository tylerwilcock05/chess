package service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.*;

public class UserServiceTests {
    private UserDAO userDAO;
    private AuthDAO authDAO;
    private UserService userService;

    @BeforeEach
    public void setup() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        userService = new UserService(userDAO, authDAO);
    }

    @Test
    @DisplayName("Register Success")
    public void registerSuccess() {
        // Positive register test
    }

    @Test
    @DisplayName("Register Negative - Already Taken or Bad Request")
    public void registerNegative() {
        // Negative register test
    }

    @Test
    @DisplayName("Login Success")
    public void loginSuccess() {
        // Positive login test
    }

    @Test
    @DisplayName("Login Negative - Bad Request or Wrong Password")
    public void loginNegative() {
        // Negative login test
    }

    @Test
    @DisplayName("Logout Success")
    public void logoutSuccess() {
        // Positive logout test
    }

    @Test
    @DisplayName("Logout Negative - Invalid Auth Token")
    public void logoutNegative() {
        // Negative logout test
    }
}
