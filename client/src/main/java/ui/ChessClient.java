package ui;

import chess.ChessGame;
import client.*;
import model.*;

import java.util.*;

public class ChessClient {
    private final ServerFacade server;
    private final String serverUrl;
    private String authToken = null;
    private String currentUsername = null;
    private List<GameData> cachedGames = new ArrayList<>();

    public ChessClient(String serverUrl) {
        this.serverUrl = serverUrl;
        this.server = new ServerFacade(serverUrl);
    }

    public String getStatePrompt() {
        return (authToken == null) ? "[LOGGED_OUT]" : "[LOGGED_IN]";
    }

    public String eval(String input) {
        var tokens = input.trim().split("\\s+");
        if (tokens.length == 0 || tokens[0].isEmpty()) {
            return "";
        }

        var cmd = tokens[0].toLowerCase();
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);

        return (authToken == null) ? evalPreLogin(cmd, params) : evalPostLogin(cmd, params);
    }

    private String evalPreLogin(String cmd, String[] params) {
        try {
            if (cmd.equals("login")) {
                return login(params);
            } else if (cmd.equals("register")) {
                return register(params);
            } else if (cmd.equals("help")) {
                return helpPreLogin();
            } else if (cmd.equals("quit")) {
                return "quit";
            }
            return "Unkown command. Type 'help' for list of commands.\n";
        } catch (ResponseException e) {
            return e.getMessage() + "\n";
        }
    }

    private String evalPostLogin(String cmd, String[] params) {
        try {
            if (cmd.equals("create")) {
                return createGame(params);
            } else if (cmd.equals("list")) {
                return listGames();
            } else if (cmd.equals("play")) {
                return playGame(params);
            } else if (cmd.equals("observe")) {
                return observeGame(params);
            } else if (cmd.equals("logout")) {
                return logout();
            } else if (cmd.equals("help")) {
                return helpPostLogin();
            } else if (cmd.equals("quit")) {
                return "quit";
            }
            return "Unkown command. Type 'help' for list of commands.\n";
        } catch (ResponseException e) {
            return e.getMessage() + "\n";
        }
    }

    private String login(String[] params) throws ResponseException {
        if (params.length < 2) {
            return "Usage: login <username> <password>\n";
        }
        var auth = server.login(new UserData(params[0], params[1], null));
        this.authToken = auth.authToken();
        return "Logged in as " + auth.username() + "\n";
    }

    private String register(String[] params) throws ResponseException {
        if (params.length < 3) {
            return "Usage: register <username> <password> <email>\n";
        }
        var auth = server.register(new UserData(params[0], params[1], params[2]));
        this.authToken = auth.authToken();
        return "Logged in as " + auth.username() + "\n";
    }

    private String createGame(String[] params) throws ResponseException {
        if (params.length < 1) {
            return "Usage: create <name>\n";
        }
        var res = server.createGame(authToken, new CreateGameRequest(params[0]));
        return "Created game " + res.gameID() + "\n";
    }

    private String listGames() throws ResponseException {
        var games = server.listGames(authToken);
        var sb = new StringBuilder();
        for (var g : games) {
            sb.append("ID: ").append(g.gameID())
              .append(" | Name: ").append(g.gameName())
              .append(" | White: ").append(g.whiteUsername())
              .append(" | Black: ").append(g.blackUsername())
              .append("\n");
        }
        return sb.toString();
    }

    private String playGame(String[] params) throws ResponseException {
        if (params.length < 2) {
            return "Usage: play <id> [WHITE|BLACK]\n";
        }
        int id = Integer.parseInt(params[0]);
        var color = ChessGame.TeamColor.valueOf(params[1].toUpperCase());
        server.joinGame(authToken, new JoinGameRequest(color, id));

        var board = new chess.ChessBoard();
        board.resetBoard();
        BoardRenderer.renderBoard(board, color);
        return "";
    }

    private String observeGame(String[] params) throws ResponseException {
        if (params.length < 1) {
            return "Usage: observe <id>\n";
        }
        int id = Integer.parseInt(params[0]);
        server.joinGame(authToken, new JoinGameRequest(null, id));

        var board = new chess.ChessBoard();
        board.resetBoard();
        BoardRenderer.renderBoard(board, ChessGame.TeamColor.WHITE);
        return "";
    }

    private String logout() throws ResponseException {
        server.logout(authToken);
        this.authToken = null;
        return "Logged out.\n";
    }

    private String helpPreLogin() {
        return "Commands:\n"
                + "register <username> <password> <email>\n"
                + "login <username> <password>\n"
                + "help\n"
                + "quit\n";
    }

    private String helpPostLogin() {
        return "Commands:\n"
                + "create <name>\n"
                + "list\n"
                + "play <id> [WHITE|BLACK]\n"
                + "observe <id>\n"
                + "logout\n"
                + "help\n"
                + "quit\n";
    }

}