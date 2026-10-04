package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

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
        int castleRow = 8;
        if (piece.getTeamColor() == TeamColor.WHITE) {
            castleRow = 1;
        }
        ChessMove kingSideCastle = new ChessMove(new ChessPosition(castleRow, 5), new ChessPosition(castleRow, 7), null);
        ChessMove queenSideCastle = new ChessMove(new ChessPosition(castleRow, 5), new ChessPosition(castleRow, 3), null);
        Collection<ChessMove> validMoves = piece.pieceMoves(Board, startPosition);
        Collection<ChessMove> invalidMoves = new ArrayList<>();
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            validMoves.removeIf(move -> !isInCheckPos(turn, move.getEndPosition().getRow(), move.getEndPosition().getColumn(), true).isEmpty());
        }
        for (ChessMove move : validMoves) {
            ChessPiece tempPiece = Board.getPiece(move.getEndPosition());
            Board.addPiece(move.getEndPosition(), piece);
            Board.removePiece(startPosition);
            if (isInCheck(piece.getTeamColor())) {
                invalidMoves.add(move);
            }
            Board.addPiece(startPosition, piece);
            if (tempPiece != null) {
                Board.addPiece(move.getEndPosition(), tempPiece);
            }
            else {
                Board.removePiece(move.getEndPosition());
            }

        }
        for (ChessMove invalidMove : invalidMoves) {
            validMoves.remove(invalidMove);
        }
        if (canCastleKingside(piece.getTeamColor()) && piece.getPieceType() == ChessPiece.PieceType.KING) {
            validMoves.add(kingSideCastle);
        }
        if (canCastleQueenside(piece.getTeamColor()) && piece.getPieceType() == ChessPiece.PieceType.KING) {
            validMoves.add(queenSideCastle);
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
        ChessPosition startPos = move.getStartPosition();
        ChessPosition endPos = move.getEndPosition();
        ChessPiece.PieceType promotionPiece = move.getPromotionPiece();
        ChessPiece newPiece = Board.getPiece(new ChessPosition(startPos.getRow(), startPos.getColumn()));
        if (newPiece == null) {
            throw new InvalidMoveException("Invalid move: staring piece is null");
        }
        Collection<ChessMove> validMoves = validMoves(startPos);
        int castleRow = 8;
        if (newPiece.getTeamColor() == TeamColor.WHITE) {
            castleRow = 1;
        }
        ChessMove kingSideCastle = new ChessMove(new ChessPosition(castleRow, 5), new ChessPosition(castleRow, 7), null);
        ChessMove queenSideCastle = new ChessMove(new ChessPosition(castleRow, 5), new ChessPosition(castleRow, 3), null);
        if (validMoves == null || !validMoves.contains(move) || newPiece.getTeamColor() != turn) {
            throw new InvalidMoveException("Invalid move: " + move);
        }
        if (move.equals(kingSideCastle)) {
            if (newPiece.getTeamColor() == TeamColor.WHITE) {
                if (Board.hasMovedRWR() || Board.hasMovedWK()) {
                    throw new InvalidMoveException("Invalid move: " + move);
                }
                Board.addPiece(endPos, newPiece);
                Board.removePiece(startPos);
                Board.addPiece(new ChessPosition(1, 6), new ChessPiece(TeamColor.WHITE, ChessPiece.PieceType.ROOK));
                Board.removePiece(new ChessPosition(1, 8));
                Board.setHasMovedWK(true);
                Board.setHasMovedRWR(true);
            }
            else {
                if (Board.hasMovedRBR() || Board.hasMovedBK()) {
                    throw new InvalidMoveException("Invalid move: " + move);
                }
                Board.addPiece(endPos, newPiece);
                Board.removePiece(startPos);
                Board.addPiece(new ChessPosition(8, 6), new ChessPiece(TeamColor.BLACK, ChessPiece.PieceType.ROOK));
                Board.removePiece(new ChessPosition(8, 8));
                Board.setHasMovedBK(true);
                Board.setHasMovedRBR(true);
            }
        }
        if (move.equals(queenSideCastle)) {
            if (newPiece.getTeamColor() == TeamColor.WHITE) {
                if (Board.hasMovedLWR() || Board.hasMovedWK()) {
                    throw new InvalidMoveException("Invalid move: " + move);
                }
                Board.addPiece(endPos, newPiece);
                Board.removePiece(startPos);
                Board.addPiece(new ChessPosition(1, 4), new ChessPiece(TeamColor.WHITE, ChessPiece.PieceType.ROOK));
                Board.removePiece(new ChessPosition(1, 1));
                Board.setHasMovedWK(true);
                Board.setHasMovedLWR(true);
            }
            else {
                if (Board.hasMovedLBR() || Board.hasMovedBK()) {
                    throw new InvalidMoveException("Invalid move: " + move);
                }
                Board.addPiece(endPos, newPiece);
                Board.removePiece(startPos);
                Board.addPiece(new ChessPosition(8, 4), new ChessPiece(TeamColor.BLACK, ChessPiece.PieceType.ROOK));
                Board.removePiece(new ChessPosition(8, 1));
                Board.setHasMovedBK(true);
                Board.setHasMovedLBR(true);
            }
        }
        else {
            if (promotionPiece != null) {
                Board.addPiece(endPos, new ChessPiece(newPiece.getTeamColor(), promotionPiece));
            } else {
                Board.addPiece(endPos, newPiece);
            }
            Board.removePiece(startPos);
        }
        if (newPiece.getPieceType() == ChessPiece.PieceType.KING) {
            if (newPiece.getTeamColor() == TeamColor.WHITE) {
                Board.setHasMovedWK(true);
            } else {
                Board.setHasMovedBK(true);
            }
        } else if (newPiece.getPieceType() == ChessPiece.PieceType.ROOK) {
            if (newPiece.getTeamColor() == TeamColor.WHITE) {
                if (startPos.getRow() == 1 && startPos.getColumn() == 1) {
                    Board.setHasMovedLWR(true);
                } else if (startPos.getRow() == 1 && startPos.getColumn() == 8) {
                    Board.setHasMovedRWR(true);
                }
            } else {
                if (startPos.getRow() == 8 && startPos.getColumn() == 1) {
                    Board.setHasMovedLBR(true);
                } else if (startPos.getRow() == 8 && startPos.getColumn() == 8) {
                    Board.setHasMovedRBR(true);
                }
            }
        }
        if (newPiece.getTeamColor() == TeamColor.WHITE) {
            turn = TeamColor.BLACK;
        }
        else {
            turn = TeamColor.WHITE;
        }
    }

    public boolean canCastleQueenside(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }
        int castleRow;
        int kingRow;
        int kingCol;
        if (teamColor == TeamColor.WHITE) {
            if (Board.hasMovedWK() || Board.hasMovedLWR()) {
                return false;
            }
            castleRow = 1;
            kingRow = Board.getWhiteKingPos()[0];
            kingCol = Board.getWhiteKingPos()[1];
        }
        else {
            if (Board.hasMovedBK() || Board.hasMovedLBR()) {
                return false;
            }
            castleRow = 8;
            kingRow = Board.getBlackKingPos()[0];
            kingCol = Board.getBlackKingPos()[1];
        }
        ChessMove queenSideCastle = new ChessMove(new ChessPosition(castleRow, 5), new ChessPosition(castleRow, 3), null);
        if (!queenSideCastle.equals(new ChessMove(new ChessPosition(kingRow, kingCol), new ChessPosition(castleRow, 3), null))) {
            return false;
        }
        for (int i = 2; i < 5; i++) {
            if (Board.getPiece(new ChessPosition(castleRow, i)) != null) {
                return false;
            }
        }
        Board.addPiece(new ChessPosition(castleRow, 4), new ChessPiece(teamColor, ChessPiece.PieceType.KING));
        Board.removePiece(new ChessPosition(castleRow, 5));
        if (isInCheck(teamColor)) {
            Board.addPiece(new ChessPosition(castleRow, 5), new ChessPiece(teamColor, ChessPiece.PieceType.KING));
            Board.removePiece(new ChessPosition(castleRow, 4));
            return false;
        }
        Board.addPiece(new ChessPosition(castleRow, 3), new ChessPiece(teamColor, ChessPiece.PieceType.KING));
        Board.removePiece(new ChessPosition(castleRow, 4));
        if (isInCheck(teamColor)) {
            Board.addPiece(new ChessPosition(castleRow, 5), new ChessPiece(teamColor, ChessPiece.PieceType.KING));
            Board.removePiece(new ChessPosition(castleRow, 3));
            return false;
        }
        Board.addPiece(new ChessPosition(castleRow, 5), new ChessPiece(teamColor, ChessPiece.PieceType.KING));
        Board.removePiece(new ChessPosition(castleRow, 3));
        return true;
    }

    public boolean canCastleKingside(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }
        int castleRow;
        int kingRow;
        int kingCol;
        if (teamColor == TeamColor.WHITE) {
            if (Board.hasMovedWK() || Board.hasMovedRWR()) {
                return false;
            }
            castleRow = 1;
            kingRow = Board.getWhiteKingPos()[0];
            kingCol = Board.getWhiteKingPos()[1];
        }
        else {
            if (Board.hasMovedBK() || Board.hasMovedRBR()) {
                return false;
            }
            castleRow = 8;
            kingRow = Board.getBlackKingPos()[0];
            kingCol = Board.getBlackKingPos()[1];
        }
        ChessMove kingSideCastle = new ChessMove(new ChessPosition(castleRow, 5), new ChessPosition(castleRow, 7), null);
        if (!kingSideCastle.equals(new ChessMove(new ChessPosition(kingRow, kingCol), new ChessPosition(castleRow, 7), null))) {
            return false;
        }
        for (int i = 6; i < 8; i++) {
            if (Board.getPiece(new ChessPosition(castleRow, i)) != null) {
                return false;
            }
        }
        Board.addPiece(new ChessPosition(castleRow, 6), new ChessPiece(teamColor, ChessPiece.PieceType.KING));
        Board.removePiece(new ChessPosition(castleRow, 5));
        if (isInCheck(teamColor)) {
            Board.addPiece(new ChessPosition(castleRow, 5), new ChessPiece(teamColor, ChessPiece.PieceType.KING));
            Board.removePiece(new ChessPosition(castleRow, 6));
            return false;
        }
        Board.addPiece(new ChessPosition(castleRow, 7), new ChessPiece(teamColor, ChessPiece.PieceType.KING));
        Board.removePiece(new ChessPosition(castleRow, 6));
        if (isInCheck(teamColor)) {
            Board.addPiece(new ChessPosition(castleRow, 5), new ChessPiece(teamColor, ChessPiece.PieceType.KING));
            Board.removePiece(new ChessPosition(castleRow, 7));
            return false;
        }
        Board.addPiece(new ChessPosition(castleRow, 5), new ChessPiece(teamColor, ChessPiece.PieceType.KING));
        Board.removePiece(new ChessPosition(castleRow, 7));
        return true;
    }



    private int[] getAttackingPiece(int kingRow, int kingCol, int[][] directions, ChessPiece.PieceType[] enemyTypes, TeamColor color, boolean isSlider) {
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
                                return new int[]{newRow, newCol};
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
        return new int[]{};
    }

    public ArrayList<int[]> isInCheckPos(TeamColor teamColor, int kingRow, int kingCol, boolean includeKing) {
        int[][] diagonal_indexes = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
        int[][] vertical_indexes = {{0, 1}, {0, -1}, {-1, 0}, {1, 0}};
        int[][] knight_indexes = {{1, 2}, {1, -2}, {-1, 2}, {-1, -2}, {2, 1}, {2, -1}, {-2, 1}, {-2, -1}};
        int[][] king_indexes = {{-1, 1}, {0, 1}, {1, 1}, {-1, 0}, {1, 0}, {-1, -1}, {0, -1}, {1, -1}};
        int[][] pawn_indexes;
        ArrayList<int[]> attackingPieces = new ArrayList<>();
        int[] attackingPiece;
        if (teamColor == TeamColor.WHITE) {
            pawn_indexes = new int[][]{{1, 1}, {1, -1}};
        }
        else {
            pawn_indexes = new int[][]{{-1, 1}, {-1, -1}};
        }
        attackingPiece = getAttackingPiece(kingRow, kingCol, diagonal_indexes, new ChessPiece.PieceType[]{ChessPiece.PieceType.QUEEN}, teamColor, true);
        if (!Arrays.equals(attackingPiece, new int[]{})) {
            attackingPieces.add(attackingPiece);
        }
        attackingPiece = getAttackingPiece(kingRow, kingCol, diagonal_indexes, new ChessPiece.PieceType[]{ChessPiece.PieceType.BISHOP}, teamColor, true);
        if (!Arrays.equals(attackingPiece, new int[]{})) {
            attackingPieces.add(attackingPiece);
        }
        attackingPiece = getAttackingPiece(kingRow, kingCol, vertical_indexes, new ChessPiece.PieceType[]{ChessPiece.PieceType.ROOK}, teamColor, true);
        if (!Arrays.equals(attackingPiece, new int[]{})) {
            attackingPieces.add(attackingPiece);
        }
        attackingPiece = getAttackingPiece(kingRow, kingCol, vertical_indexes, new ChessPiece.PieceType[]{ChessPiece.PieceType.QUEEN}, teamColor, true);
        if (!Arrays.equals(attackingPiece, new int[]{})) {
            attackingPieces.add(attackingPiece);
        }
        attackingPiece = getAttackingPiece(kingRow, kingCol, knight_indexes, new ChessPiece.PieceType[]{ChessPiece.PieceType.KNIGHT}, teamColor, false);
        if (!Arrays.equals(attackingPiece, new int[]{})) {
            attackingPieces.add(attackingPiece);
        }
        attackingPiece = getAttackingPiece(kingRow, kingCol, pawn_indexes, new ChessPiece.PieceType[]{ChessPiece.PieceType.PAWN}, teamColor, false);
        if (!Arrays.equals(attackingPiece, new int[]{})) {
            attackingPieces.add(attackingPiece);
        }
        if (includeKing) {
            attackingPiece = getAttackingPiece(kingRow, kingCol, king_indexes, new ChessPiece.PieceType[]{ChessPiece.PieceType.KING}, teamColor, false);
            if (!Arrays.equals(attackingPiece, new int[]{})) {
                attackingPieces.add(attackingPiece);
            }
        }
        return attackingPieces;
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
        if (teamColor == TeamColor.WHITE) {
            kingRow = Board.getWhiteKingPos()[0];
            kingCol = Board.getWhiteKingPos()[1];
        }
        else {
            kingRow = Board.getBlackKingPos()[0];
            kingCol = Board.getBlackKingPos()[1];
        }
        return !isInCheckPos(teamColor, kingRow, kingCol, true).isEmpty();
    }

    public boolean isTheAttackingPieceCapturable(TeamColor teamColor, int kingRow, int kingCol) {
        TeamColor enemyColor;
        if (teamColor == TeamColor.WHITE) {
            enemyColor = TeamColor.BLACK;
        }
        else {
            enemyColor = TeamColor.WHITE;
        }
        ArrayList<int[]> attackingPieces;
        ArrayList<int[]> attackingPiecesOfAttackingPiece = new ArrayList<>();
        attackingPieces = isInCheckPos(teamColor, kingRow, kingCol, false);
        if (attackingPieces.size() == 2) {
            return false;
        }
        int[] attackingPiece = attackingPieces.getFirst();
        attackingPiecesOfAttackingPiece = isInCheckPos(enemyColor, attackingPiece[0], attackingPiece[1], false);
        return !attackingPiecesOfAttackingPiece.isEmpty();
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        int kingRow;
        int kingCol;
        if (teamColor == TeamColor.WHITE) {
            kingRow = Board.getWhiteKingPos()[0];
            kingCol = Board.getWhiteKingPos()[1];
        }
        else {
            kingRow = Board.getBlackKingPos()[0];
            kingCol = Board.getBlackKingPos()[1];
        }
        if (isInCheck(teamColor) && validMoves(new ChessPosition(kingRow, kingCol)).isEmpty()) {
            return !isTheAttackingPieceCapturable(teamColor, kingRow, kingCol);
        }
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        int kingRow;
        int kingCol;
        if (teamColor == TeamColor.WHITE) {
            kingRow = Board.getWhiteKingPos()[0];
            kingCol = Board.getWhiteKingPos()[1];
        }
        else {
            kingRow = Board.getBlackKingPos()[0];
            kingCol = Board.getBlackKingPos()[1];
        }
        // need to check if all pieces have no moves, not just the king
        if (!isInCheck(teamColor) && validMoves(new ChessPosition(kingRow, kingCol)).isEmpty()) {
            for (int i = 1; i < 9; i++) {
                for (int j = 1; j < 9; j++) {
                    ChessPiece newPiece = Board.getPiece(new ChessPosition(i, j));
                    if (newPiece != null && newPiece.getTeamColor() == teamColor) {
                        if (!validMoves(new ChessPosition(i, j)).isEmpty()) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }
        return false;
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

    @Override
    public String toString() {
        return "ChessGame{" +
                "Board=" + Board +
                ", turn=" + turn +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(Board, chessGame.Board) && turn == chessGame.turn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(Board, turn);
    }
}
