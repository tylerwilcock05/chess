package dataaccess.sql;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.UserDAO;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SQLUserDAO implements UserDAO {
    public SQLUserDAO() throws DataAccessException {
        SQLDAO.configureDatabase();
    }

    @Override
    public UserData createUser(UserData user) throws DataAccessException {
        String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());

        String sql = "INSERT INTO 'user' (username, password, email) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.username());
            stmt.setString(2, hashedPassword);
            stmt.setString(3, user.email());
            stmt.executeUpdate();
            return new UserData(user.username(), hashedPassword, user.email());
        } catch (SQLException e) {
            throw new DataAccessException("Error creating user: " + e.getMessage(), e);
        }
    }
}
