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
        SQLAuthDAO.configureDatabase();
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
                    return new AuthData(token, username)
                } catch (SQLException e) {
                    throw new DataAccessException("Error creating auth token: " + e.getMessage(), e);
                }
    }
}