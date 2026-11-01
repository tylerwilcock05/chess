package dataaccess.sql;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import model.AuthData;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SQLAuthDAO implements AuthDAO {
    
    public SQLAuthDAO() throws DataAccessException {
        SQLDAO.configureDatabase();
    }

    @Override
    public AuthData createAuth(String username) throws DataAccessException {
        String token = UUID.randomUUID().toString();
        String sql = "INSERT INTO `auth` (authToken, username) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, token);
                    stmt.setString(2, username);
                    stmt.executeUpdate();
                    return new AuthData(token, username);
                } catch (SQLException e) {
                    throw new DataAccessException("Error creating auth token: " + e.getMessage(), e);
                }
    }

    @Override 
    public AuthData getAuth(String authToken) throws DataAccessException {
        String sql = "SELECT authToken, username FROM `auth` WHERE authToken = ?";
        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, authToken);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return new AuthData(
                            rs.getString("authToken"),
                            rs.getString("username")
                        );
                    }
                }
                return null;
            } catch (SQLException e) {
                throw new DataAccessException("Error getting auth token: " + e.getMessage(), e);
            }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        String sql = "DELETE FROM `auth` WHERE authToken = ?";
        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, authToken);
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new DataAccessException("Error deleting auth token: " + e.getMessage(), e);
            }
    }

    @Override
    public void clear() throws DataAccessException {
        String sql = "TRUNCATE `auth`";
        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new DataAccessException("Error clearing auth table: " + e.getMessage(), e);
            }
    }
}