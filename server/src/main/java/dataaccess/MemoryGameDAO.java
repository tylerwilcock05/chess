package dataaccess;

import chess.ChessGame;
import model.GameData;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {
    private int nextGameID = 1;
    private final HashMap<Integer, GameData> gameDatabase = new HashMap<>();

    @Override
    public GameData createGame(GameData game) throws DataAccessException {
        int newID = nextGameID++;
        GameData newGame = new GameData(newID, game.whiteUsername(), game.blackUsername(), game.gameName(), new ChessGame());
        gameDatabase.put(newID, newGame);
        return newGame;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return gameDatabase.get(gameID);
    }

    @Override
    public GameData[] listGames() throws DataAccessException {
        return gameDatabase.values().toArray(new GameData[0]);
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        gameDatabase.put(game.gameID(), game);
    }

    @Override
    public void clear() throws DataAccessException {
        gameDatabase.clear();
    }
}
