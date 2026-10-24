public interface GameDAO {
    public GameData createGame(String authToken, String gameName) throws DataAccessException;
    public GameData getGame(String authToken, int gameID) throws DataAccessException;
    public GameData[] listGames(String authToken) throws DataAccessException;
    public GameData updateGame(String authToken, int gameID) throws DataAccessException;
    public void clear() throws DataAccessException;
}