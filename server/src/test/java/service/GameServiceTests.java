package service;

import chess.ChessGame;
import dataaccess.*;
import model.*;
import org.junit.jupiter.api.*;

public class GameServiceTests {
    private GameDAO gameDAO;
    private AuthDAO authDAO;
    private GameService gameService;

    @BeforeEach
    public void setup() {
        gameDAO = new MemoryGameDAO();
        authDAO = new MemoryAuthDAO();
        gameService = new GameService(gameDAO, authDAO);
    }

    @Test
    @DisplayName("Create Game Success")
    public void createGameSuccess() {
        // Positive create game test
    }

    @Test
    @DisplayName("Create Game Negative - Unauthorized or Bad Request")
    public void createGameNegative() {
        // Negative create game test
    }

    @Test
    @DisplayName("List Games Success")
    public void listGamesSuccess() {
        // Positive list games test
    }

    @Test
    @DisplayName("List Games Negative - Unauthorized")
    public void listGamesNegative() {
        // Negative list games test
    }

    @Test
    @DisplayName("Join Game Success")
    public void joinGameSuccess() {
        // Positive join game test
    }

    @Test
    @DisplayName("Join Game Negative - Color Taken or Bad Game ID")
    public void joinGameNegative() {
        // Negative join game test
    }
}
