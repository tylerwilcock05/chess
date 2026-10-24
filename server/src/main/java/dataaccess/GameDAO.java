package dataaccess;

import chess.ChessGame;
import model.GameData;

public interface GameDAO {
    public GameData createGame(GameData game) throws DataAccessException;

    public GameData getGame(int gameID) throws DataAccessException;

    public GameData[] listGames() throws DataAccessException;

    public void updateGame(GameData game) throws DataAccessException;

    public void clear() throws DataAccessException;
}