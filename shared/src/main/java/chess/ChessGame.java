package chess;

import java.util.ArrayList;
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

    private ChessBoard board = new ChessBoard();
    private TeamColor turn;

    public ChessGame() {
        turn = TeamColor.WHITE;
        board.resetBoard();
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
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {
            return null;
        }
        int castleRow = (piece.getTeamColor() == TeamColor.WHITE) ? 1 : 8;
        ChessMove kingSideCastle = new ChessMove(new ChessPosition(castleRow, 5), new ChessPosition(castleRow, 7), null);
        ChessMove queenSideCastle = new ChessMove(new ChessPosition(castleRow, 5), new ChessPosition(castleRow, 3), null);
        Collection<ChessMove> validMoves = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> invalidMoves = new ArrayList<>();
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            validMoves.removeIf(move -> {
                int r = move.getEndPosition().getRow();
                int c = move.getEndPosition().getColumn();
                return !isInCheckPos(turn, r, c, true).isEmpty();
            });
        }
        for (ChessMove move : validMoves) {
            ChessPiece tempPiece = board.getPiece(move.getEndPosition());
            board.addPiece(move.getEndPosition(), piece);
            board.removePiece(startPosition);
            if (isInCheck(piece.getTeamColor())) {
                invalidMoves.add(move);
            }
            board.addPiece(startPosition, piece);
            if (tempPiece != null) {
                board.addPiece(move.getEndPosition(), tempPiece);
            } else {
                board.removePiece(move.getEndPosition());
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
        ChessPiece newPiece = board.getPiece(startPos);
        if (newPiece == null) {
            throw new InvalidMoveException("Invalid move: starting piece is null");
        }
        Collection<ChessMove> validMoves = validMoves(startPos);
        int castleRow = (newPiece.getTeamColor() == TeamColor.WHITE) ? 1 : 8;
        ChessMove kingSideCastle = new ChessMove(new ChessPosition(castleRow, 5), new ChessPosition(castleRow, 7), null);
        ChessMove queenSideCastle = new ChessMove(new ChessPosition(castleRow, 5), new ChessPosition(castleRow, 3), null);

        if (validMoves == null || !validMoves.contains(move) || newPiece.getTeamColor() != turn) {
            throw new InvalidMoveException("Invalid move: " + move);
        }

        if (!applyCastleMove(move, newPiece, kingSideCastle, queenSideCastle)) {
            if (move.getPromotionPiece() != null) {
                board.addPiece(endPos, new ChessPiece(newPiece.getTeamColor(), move.getPromotionPiece()));
            } else {
                board.addPiece(endPos, newPiece);
            }
            board.removePiece(startPos);
        }

        updateMovedPieces(newPiece, startPos);
        if (newPiece.getTeamColor() == TeamColor.WHITE) {
            turn = TeamColor.BLACK;
        } else {
            turn = TeamColor.WHITE;
        }
    }

    private boolean applyCastleMove(ChessMove move, ChessPiece piece,
                                    ChessMove kingSideCastle,
                                    ChessMove queenSideCastle) throws InvalidMoveException {
        if (move.equals(kingSideCastle)) {
            applyKingSideCastle(piece, move);
            return true;
        } else if (move.equals(queenSideCastle)) {
            applyQueenSideCastle(piece, move);
            return true;
        }
        return false;
    }

    private void applyKingSideCastle(ChessPiece piece, ChessMove move) throws InvalidMoveException {
        boolean isWhite = piece.getTeamColor() == TeamColor.WHITE;
        if (isWhite ? (board.hasMovedRWR() || board.hasMovedWK()) : (board.hasMovedRBR() || board.hasMovedBK())) {
            throw new InvalidMoveException("Invalid move: " + move);
        }
        int row = isWhite ? 1 : 8;
        board.addPiece(move.getEndPosition(), piece);
        board.removePiece(move.getStartPosition());
        board.addPiece(new ChessPosition(row, 6), new ChessPiece(piece.getTeamColor(), ChessPiece.PieceType.ROOK));
        board.removePiece(new ChessPosition(row, 8));
        if (isWhite) {
            board.setHasMovedWK(true);
            board.setHasMovedRWR(true);
        } else {
            board.setHasMovedBK(true);
            board.setHasMovedRBR(true);
        }
    }

    private void applyQueenSideCastle(ChessPiece piece, ChessMove move) throws InvalidMoveException {
        boolean isWhite = piece.getTeamColor() == TeamColor.WHITE;
        if (isWhite ? (board.hasMovedLWR() || board.hasMovedWK()) : (board.hasMovedLBR() || board.hasMovedBK())) {
            throw new InvalidMoveException("Invalid move: " + move);
        }
        int row = isWhite ? 1 : 8;
        board.addPiece(move.getEndPosition(), piece);
        board.removePiece(move.getStartPosition());
        board.addPiece(new ChessPosition(row, 4), new ChessPiece(piece.getTeamColor(), ChessPiece.PieceType.ROOK));
        board.removePiece(new ChessPosition(row, 1));
        if (isWhite) {
            board.setHasMovedWK(true);
            board.setHasMovedLWR(true);
        } else {
            board.setHasMovedBK(true);
            board.setHasMovedLBR(true);
        }
    }

    private void updateMovedPieces(ChessPiece piece, ChessPosition startPos) {
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            if (piece.getTeamColor() == TeamColor.WHITE) {
                board.setHasMovedWK(true);
            } else {
                board.setHasMovedBK(true);
            }
        } else if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {
            updateRookMoved(piece.getTeamColor(), startPos.getRow(), startPos.getColumn());
        }
    }

    private void updateRookMoved(TeamColor color, int row, int col) {
        if (color == TeamColor.WHITE) {
            if (row == 1 && col == 1) {
                board.setHasMovedLWR(true);
            } else if (row == 1 && col == 8) {
                board.setHasMovedRWR(true);
            }
        } else {
            if (row == 8 && col == 1) {
                board.setHasMovedLBR(true);
            } else if (row == 8 && col == 8) {
                board.setHasMovedRBR(true);
            }
        }
    }

    public boolean canCastleQueenside(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }
        boolean isWhite = teamColor == TeamColor.WHITE;
        if (isWhite ? (board.hasMovedWK() || board.hasMovedLWR()) : (board.hasMovedBK() || board.hasMovedLBR())) {
            return false;
        }
        int castleRow = isWhite ? 1 : 8;
        int[] kingPos = isWhite ? board.getWhiteKingPos() : board.getBlackKingPos();
        ChessMove queenSideCastle = new ChessMove(new ChessPosition(castleRow, 5), new ChessPosition(castleRow, 3), null);
        ChessMove actualKingMove = new ChessMove(new ChessPosition(kingPos[0], kingPos[1]), new ChessPosition(castleRow, 3), null);
        if (!queenSideCastle.equals(actualKingMove)) {
            return false;
        }
        for (int i = 2; i < 5; i++) {
            if (board.getPiece(new ChessPosition(castleRow, i)) != null) {
                return false;
            }
        }
        return isCastlePathClear(teamColor, castleRow, 4, 3);
    }

    public boolean canCastleKingside(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }
        boolean isWhite = teamColor == TeamColor.WHITE;
        if (isWhite ? (board.hasMovedWK() || board.hasMovedRWR()) : (board.hasMovedBK() || board.hasMovedRBR())) {
            return false;
        }
        int castleRow = isWhite ? 1 : 8;
        int[] kingPos = isWhite ? board.getWhiteKingPos() : board.getBlackKingPos();
        ChessMove kingSideCastle = new ChessMove(new ChessPosition(castleRow, 5), new ChessPosition(castleRow, 7), null);
        ChessMove actualKingMove = new ChessMove(new ChessPosition(kingPos[0], kingPos[1]), new ChessPosition(castleRow, 7), null);
        if (!kingSideCastle.equals(actualKingMove)) {
            return false;
        }
        for (int i = 6; i < 8; i++) {
            if (board.getPiece(new ChessPosition(castleRow, i)) != null) {
                return false;
            }
        }
        return isCastlePathClear(teamColor, castleRow, 6, 7);
    }

    private boolean isCastlePathClear(TeamColor teamColor, int row, int intermediateCol, int targetCol) {
        board.addPiece(new ChessPosition(row, intermediateCol), new ChessPiece(teamColor, ChessPiece.PieceType.KING));
        board.removePiece(new ChessPosition(row, 5));
        if (isInCheck(teamColor)) {
            board.addPiece(new ChessPosition(row, 5), new ChessPiece(teamColor, ChessPiece.PieceType.KING));
            board.removePiece(new ChessPosition(row, intermediateCol));
            return false;
        }
        board.addPiece(new ChessPosition(row, targetCol), new ChessPiece(teamColor, ChessPiece.PieceType.KING));
        board.removePiece(new ChessPosition(row, intermediateCol));
        if (isInCheck(teamColor)) {
            board.addPiece(new ChessPosition(row, 5), new ChessPiece(teamColor, ChessPiece.PieceType.KING));
            board.removePiece(new ChessPosition(row, targetCol));
            return false;
        }
        board.addPiece(new ChessPosition(row, 5), new ChessPiece(teamColor, ChessPiece.PieceType.KING));
        board.removePiece(new ChessPosition(row, targetCol));
        return true;
    }

    private int[] getAttackingPiece(int kingRow, int kingCol, int[][] directions,
                                    ChessPiece.PieceType[] enemyTypes,
                                    TeamColor color, boolean isSlider) {
        int max = isSlider ? 8 : 2;
        for (int[] dir : directions) {
            for (int i = 1; i < max; i++) {
                int newRow = kingRow + dir[0] * i;
                int newCol = kingCol + dir[1] * i;
                if (!isInBounds(newRow, newCol)) {
                    break;
                }
                ChessPiece newPiece = board.getPiece(new ChessPosition(newRow, newCol));
                if (newPiece != null) {
                    if (matchesEnemy(newPiece, enemyTypes, color)) {
                        return new int[]{newRow, newCol};
                    }
                    break;
                }
            }
        }
        return new int[]{};
    }

    private boolean matchesEnemy(ChessPiece piece, ChessPiece.PieceType[] enemyTypes, TeamColor color) {
        if (piece.getTeamColor() == color) {
            return false;
        }
        for (ChessPiece.PieceType type : enemyTypes) {
            if (type == piece.getPieceType()) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<int[]> isInCheckPos(TeamColor teamColor, int kingRow, int kingCol, boolean includeKing) {
        int[][] diagonalIndexes = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
        int[][] verticalIndexes = {{0, 1}, {0, -1}, {-1, 0}, {1, 0}};
        int[][] knightIndexes = {{1, 2}, {1, -2}, {-1, 2}, {-1, -2}, {2, 1}, {2, -1}, {-2, 1}, {-2, -1}};
        int[][] kingIndexes = {{-1, 1}, {0, 1}, {1, 1}, {-1, 0}, {1, 0}, {-1, -1}, {0, -1}, {1, -1}};
        int[][] pawnIndexes;
        ArrayList<int[]> attackingPieces = new ArrayList<>();
        if (teamColor == TeamColor.WHITE) {
            pawnIndexes = new int[][]{{1, 1}, {1, -1}};
        } else {
            pawnIndexes = new int[][]{{-1, 1}, {-1, -1}};
        }

        addThreat(attackingPieces, kingRow, kingCol, diagonalIndexes,
                new ChessPiece.PieceType[]{ChessPiece.PieceType.QUEEN}, teamColor, true);
        addThreat(attackingPieces, kingRow, kingCol, diagonalIndexes,
                new ChessPiece.PieceType[]{ChessPiece.PieceType.BISHOP}, teamColor, true);
        addThreat(attackingPieces, kingRow, kingCol, verticalIndexes,
                new ChessPiece.PieceType[]{ChessPiece.PieceType.ROOK}, teamColor, true);
        addThreat(attackingPieces, kingRow, kingCol, verticalIndexes,
                new ChessPiece.PieceType[]{ChessPiece.PieceType.QUEEN}, teamColor, true);
        addThreat(attackingPieces, kingRow, kingCol, knightIndexes,
                new ChessPiece.PieceType[]{ChessPiece.PieceType.KNIGHT}, teamColor, false);
        addThreat(attackingPieces, kingRow, kingCol, pawnIndexes,
                new ChessPiece.PieceType[]{ChessPiece.PieceType.PAWN}, teamColor, false);
        if (includeKing) {
            addThreat(attackingPieces, kingRow, kingCol, kingIndexes,
                    new ChessPiece.PieceType[]{ChessPiece.PieceType.KING}, teamColor, false);
        }
        return attackingPieces;
    }

    private void addThreat(ArrayList<int[]> attackingPieces, int kingRow, int kingCol,
                           int[][] directions, ChessPiece.PieceType[] enemyTypes,
                           TeamColor teamColor, boolean isSlider) {
        int[] attacker = getAttackingPiece(kingRow, kingCol, directions, enemyTypes, teamColor, isSlider);
        if (attacker.length > 0) {
            attackingPieces.add(attacker);
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        int[] kingPos = (teamColor == TeamColor.WHITE) ? board.getWhiteKingPos() : board.getBlackKingPos();
        return !isInCheckPos(teamColor, kingPos[0], kingPos[1], true).isEmpty();
    }

    public boolean isTheAttackingPieceCapturable(TeamColor teamColor, int kingRow, int kingCol) {
        TeamColor enemyColor = (teamColor == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
        ArrayList<int[]> attackingPieces = isInCheckPos(teamColor, kingRow, kingCol, false);
        if (attackingPieces.size() == 2 || attackingPieces.isEmpty()) {
            return false;
        }
        int[] attackingPiece = attackingPieces.getFirst();
        ArrayList<int[]> attackingPiecesOfAttackingPiece =
                isInCheckPos(enemyColor, attackingPiece[0], attackingPiece[1], false);
        return !attackingPiecesOfAttackingPiece.isEmpty();
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        int[] kingPos = (teamColor == TeamColor.WHITE) ? board.getWhiteKingPos() : board.getBlackKingPos();
        if (isInCheck(teamColor) && validMoves(new ChessPosition(kingPos[0], kingPos[1])).isEmpty()) {
            return !isTheAttackingPieceCapturable(teamColor, kingPos[0], kingPos[1]);
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
        if (isInCheck(teamColor)) {
            return false;
        }
        int[] kingPos = (teamColor == TeamColor.WHITE) ? board.getWhiteKingPos() : board.getBlackKingPos();
        if (!validMoves(new ChessPosition(kingPos[0], kingPos[1])).isEmpty()) {
            return false;
        }
        return !hasAnyValidMoves(teamColor);
    }

    private boolean hasAnyValidMoves(TeamColor teamColor) {
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition pos = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(pos);
                if (piece != null && piece.getTeamColor() == teamColor && !validMoves(pos).isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "board=" + board +
                ", turn=" + turn +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && turn == chessGame.turn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, turn);
    }
}
