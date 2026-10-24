package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public AuthData register(UserData user) throws DataAccessException, BadRequestException, AlreadyTakenException {
        if (user == null || user.username() == null || user.password() == null || user.email() == null
                || user.username().isBlank() || user.password().isBlank() || user.email().isBlank()) {
            throw new BadRequestException("Error: bad request");
        }

        if (userDAO.getUser(user.username()) != null) {
            throw new AlreadyTakenException("Error: already taken");
        }

        userDAO.createUser(user);
        return authDAO.createAuth(user.username());
    }

    public AuthData login(UserData loginRequest) throws DataAccessException, BadRequestException, UnauthorizedException {
        if (loginRequest == null || loginRequest.username() == null || loginRequest.password() == null
                || loginRequest.username().isBlank() || loginRequest.password().isBlank()) {
            throw new BadRequestException("Error: bad request");
        }

        UserData existingUser = userDAO.getUser(loginRequest.username());
        if (existingUser == null || !existingUser.password().equals(loginRequest.password())) {
            throw new UnauthorizedException("Error: unauthorized");
        }

        return authDAO.createAuth(existingUser.username());
    }

    public void logout(String authToken) throws DataAccessException, UnauthorizedException {
        if (authToken == null || authDAO.getAuth(authToken) == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }

        authDAO.deleteAuth(authToken);
    }
}
