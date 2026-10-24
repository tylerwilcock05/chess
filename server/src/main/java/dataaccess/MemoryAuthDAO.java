package dataaccess;

import model.AuthData;

import java.util.HashMap;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO {
    HashMap<String, AuthData> authDatabase = new HashMap<>();

    @Override
    public AuthData createAuth(String username) throws DataAccessException {
        AuthData auth = new AuthData(UUID.randomUUID().toString(), username);
        authDatabase.put(auth.authToken(), auth);
        return auth;
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return authDatabase.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        authDatabase.remove(authToken);
    }

    @Override
    public void clear() throws DataAccessException {
        authDatabase.clear();
    }

}
