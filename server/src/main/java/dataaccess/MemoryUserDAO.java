package dataaccess;

import model.UserData;
import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {
    HashMap<String, UserData> userDatabase = new HashMap<>();
    
    @Override
    public UserData createUser(UserData user) throws DataAccessException {
        if (userDatabase.containsKey(user.username())) {
            throw new DataAccessException("User already exists");
        }
        userDatabase.put(user.username(), user);
        return user;
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return userDatabase.get(username);
    }

    @Override
    public void clear() throws DataAccessException {
        userDatabase.clear();
    }
}
