package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.*;

public class GameService {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public CreateGameResult createGame(String authToken, CreateGameRequest req)
            throws DataAccessException, BadRequestException, UnauthorizedException {
        if (authToken == null || authDAO.getAuth(authToken) == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }

        if (req == null || req.gameName() == null || req.gameName().isBlank()) {
            throw new BadRequestException("Error: bad request");
        }

        GameData newGame = new GameData(0, null, null, req.gameName(), new ChessGame());
        GameData createdGame = gameDAO.createGame(newGame);
        return new CreateGameResult(createdGame.gameID());
    }

    public GameData[] listGames(String authToken) throws DataAccessException, UnauthorizedException {
        if (authToken == null || authDAO.getAuth(authToken) == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }

        return gameDAO.listGames();
    }

    public void joinGame(String authToken, JoinGameRequest req)
            throws DataAccessException, BadRequestException, UnauthorizedException, AlreadyTakenException {
        AuthData auth = authDAO.getAuth(authToken);
        if (auth == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }

        if (req == null || req.gameID() == null || req.playerColor() == null) {
            throw new BadRequestException("Error: bad request");
        }

        GameData game = gameDAO.getGame(req.gameID());
        if (game == null) {
            throw new BadRequestException("Error: bad request");
        }

        if (req.playerColor() == ChessGame.TeamColor.WHITE) {
            if (game.whiteUsername() != null && !game.whiteUsername().isEmpty()) {
                throw new AlreadyTakenException("Error: already taken");
            }
            game = game.withWhiteUsername(auth.username());
        } else if (req.playerColor() == ChessGame.TeamColor.BLACK) {
            if (game.blackUsername() != null && !game.blackUsername().isEmpty()) {
                throw new AlreadyTakenException("Error: already taken");
            }
            game = game.withBlackUsername(auth.username());
        } else {
            throw new BadRequestException("Error: bad request");
        }

        gameDAO.updateGame(game);
    }
}
