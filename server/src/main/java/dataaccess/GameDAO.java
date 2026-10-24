package dataaccess;

import model.GameData;

public interface GameDAO {
    public GameData createGame(String authToken, GameData game) throws DataAccessException;
    public GameData getGame(String authToken, int gameID) throws DataAccessException;
    public GameData[] listGames(String authToken) throws DataAccessException;
    public void updateGame(int gameID, String authToken, ChessGame game) throws DataAccessException;
    public void clear() throws DataAccessException;
}