package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static chess.ChessPiece.isInBounds;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessBoard Board = new ChessBoard();
    private TeamColor turn;


    public ChessGame() {
        turn = TeamColor.WHITE;
        Board.resetBoard();

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        turn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = Board.getPiece(startPosition);
        if (piece == null) {
            return null;
        }
        Collection<ChessMove> validMoves = piece.pieceMoves(Board, startPosition);
        for (ChessMove move : validMoves) {
            if (isInCheck(turn)) {
                validMoves.remove(move);
            }
        }

        return validMoves;
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {

    }

    private boolean isAttackedByPieces(int kingRow, int kingCol, int[][] directions, ChessPiece.PieceType[] enemyTypes, TeamColor color, boolean isSlider) {
        int newRow;
        int newCol;
        int max = 2;
        if (isSlider) {
            max = 8;
        }
        for (int[] dir : directions) {
            for (int i= 1; i < max; i++) {
                newRow = kingRow + dir[0] * i;
                newCol = kingCol + dir[1] * i;
                if (isInBounds(newRow, newCol)) {
                    ChessPiece newPiece = Board.getPiece(new ChessPosition(newRow, newCol));
                    if (newPiece != null) {
                        for (ChessPiece.PieceType type : enemyTypes) {
                            if (type == newPiece.getPieceType() && newPiece.getTeamColor() != color) {
                                return true;
                            }
                        }

                        break;
                    }
                }
                else {
                    break;
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        int kingRow;
        int kingCol;
        int[][] diagonal_indexes = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
        int[][] vertical_indexes = {{0, 1}, {0, -1}, {-1, 0}, {1, 0}};
        int[][] knight_indexes = {{1, 2}, {1, -2}, {-1, 2}, {-1, -2}, {2, 1}, {2, -1}, {-2, 1}, {-2, -1}};
        int[][] pawn_indexes;
        if (teamColor == TeamColor.WHITE) {
            kingRow = Board.getWhiteKingPos()[0];
            kingCol = Board.getWhiteKingPos()[1];
            pawn_indexes = new int[][]{{1, 1}, {1, -1}};
        }
        else {
            kingRow = Board.getBlackKingPos()[0];
            kingCol = Board.getBlackKingPos()[1];
            pawn_indexes = new int[][]{{-1, 1}, {-1, -1}};
        }
        if (isAttackedByPieces(kingRow, kingCol, diagonal_indexes, new ChessPiece.PieceType[]{ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.BISHOP}, teamColor, true)) {
            return true;
        }
        if (isAttackedByPieces(kingRow, kingCol, vertical_indexes, new ChessPiece.PieceType[]{ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.ROOK}, teamColor, true)) {
            return true;
        }
        if (isAttackedByPieces(kingRow, kingCol, knight_indexes, new ChessPiece.PieceType[]{ChessPiece.PieceType.KNIGHT}, teamColor, false)) {
            return true;
        }
        if (isAttackedByPieces(kingRow, kingCol, pawn_indexes, new ChessPiece.PieceType[]{ChessPiece.PieceType.PAWN}, teamColor, false)) {
            return true;
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        Board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return Board;
    }
}
