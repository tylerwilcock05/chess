package dataaccess.sql;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLGameDAO implements GameDAO {
    public SQLGameDAO() throws DataAccessException {
        SQLDAO.configureDatabase();
    }

    @Override
    public GameData createGame(GameData game) throws DataAccessException {
        String sql = "INSERT INTO `game` (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)";
        ChessGame chessGame = game.game() != null ? game.game() : new ChessGame();
        String gameJson = new Gson().toJson(chessGame);

        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, game.whiteUsername());
                stmt.setString(2, game.blackUsername());
                stmt.setString(3, game.gameName());
                stmt.setString(4, gameJson);
                stmt.executeUpdate();

                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        return new GameData(id, game.whiteUsername(), game.blackUsername(), game.gameName(), chessGame);
                    }
                }
                throw new DataAccessException("Failed to retrieve generated game ID")

            } catch (SQLException e) {
                throw new DataAccessException("Error creating game: " + e.getMessage(), e);
            }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        String sql = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM game WHERE gameID = ?";

        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, gameID);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    }
                }
                return null;

            } catch (SQLException e) {
                throw new DataAccessException("Error getting game: " + e.getMessage(), e);
            }
    }

    @Override
    public GameData[] listGames() throws DataAccessException {
        String sql = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM `game`";
        List<GameData> games = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.pepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    games.add(readGame(rs));
                }
                return games.toArray(new GameData[0]);

            } catch (SQLException e) {
                throw new DataAccessException("Error listing games: " + e.getMessage(), e);
            }
    }

    private GameData readGame(ResultSet rs) throws SQLException {
        int id = rs.getInt("gameID");
        String whiteUsername = rs.getString("whiteUsername");
        String blackUsername = rs.getString("blackUsername");
        String gameName = rs.getString("gameName");
        String json = rs.getString("game");

        ChessGame game = new Gson().fromJson(json, ChessGame.class);
        return new GameData(id, whiteUsername, blackUsername, gameName, game);
    }
}