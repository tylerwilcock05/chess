package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private ChessPiece[][] board = new ChessPiece[8][8];
    private int[] whiteKingPos;
    private int[] blackKingPos;
    private boolean hasMovedWhiteKing;
    private boolean hasMovedBlackKing;
    private boolean hasMovedRightWhiteRook;
    private boolean hasMovedLeftWhiteRook;
    private boolean hasMovedRightBlackRook;
    private boolean hasMovedLeftBlackRook;

    public ChessBoard() {
        whiteKingPos = new int[]{1, 5};
        blackKingPos = new int[]{8, 5};
        hasMovedWhiteKing = false;
        hasMovedBlackKing = false;
        hasMovedRightWhiteRook = false;
        hasMovedLeftWhiteRook = false;
        hasMovedRightBlackRook = false;
        hasMovedLeftBlackRook = false;
    }

    public boolean hasMovedWhiteKing() {
        return hasMovedWhiteKing;
    }

    public boolean hasMovedBlackKing() {
        return hasMovedBlackKing;
    }

    void setHasMovedWhiteKing(boolean bool) {
        hasMovedWhiteKing = bool;
    }

    void setHasMovedBlackKing(boolean bool) {
        hasMovedBlackKing = bool;
    }

    public boolean hasMovedRightWhiteRook() {
        return hasMovedRightWhiteRook;
    }

    public boolean hasMovedLeftWhiteRook() {
        return hasMovedLeftWhiteRook;
    }

    public boolean hasMovedRightBlackRook() {
        return hasMovedRightBlackRook;
    }

    public boolean hasMovedLeftBlackRook() {
        return hasMovedLeftBlackRook;
    }

    void setHasMovedRightWhiteRook(boolean bool) {
        hasMovedRightWhiteRook = bool;
    }

    void setHasMovedLeftWhiteRook(boolean bool) {
        hasMovedLeftWhiteRook = bool;
    }

    void setHasMovedRightBlackRook(boolean bool) {
        hasMovedRightBlackRook = bool;
    }

    void setHasMovedLeftBlackRook(boolean bool) {
        hasMovedLeftBlackRook = bool;
    }

    // Retained aliases for backward compatibility
    public boolean hasMovedWK() { return hasMovedWhiteKing(); }
    public boolean hasMovedBK() { return hasMovedBlackKing(); }
    void setHasMovedWK(boolean bool) { setHasMovedWhiteKing(bool); }
    void setHasMovedBK(boolean bool) { setHasMovedBlackKing(bool); }
    public boolean hasMovedRWR() { return hasMovedRightWhiteRook(); }
    public boolean hasMovedLWR() { return hasMovedLeftWhiteRook(); }
    public boolean hasMovedRBR() { return hasMovedRightBlackRook(); }
    public boolean hasMovedLBR() { return hasMovedLeftBlackRook(); }
    void setHasMovedRWR(boolean bool) { setHasMovedRightWhiteRook(bool); }
    void setHasMovedLWR(boolean bool) { setHasMovedLeftWhiteRook(bool); }
    void setHasMovedRBR(boolean bool) { setHasMovedRightBlackRook(bool); }
    void setHasMovedLBR(boolean bool) { setHasMovedLeftBlackRook(bool); }

    public int[] getWhiteKingPos() {
        return whiteKingPos;
    }

    public int[] getBlackKingPos() {
        return blackKingPos;
    }


    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow() - 1][position.getColumn() - 1] = piece;

        if (piece != null) {
            if (piece.getPieceType() == ChessPiece.PieceType.KING) {
                if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                    whiteKingPos = new int[]{position.getRow(), position.getColumn()};
                } else {
                    blackKingPos = new int[]{position.getRow(), position.getColumn()};
                }
            }
        }
    }

    public void removePiece(ChessPosition position) {
        board[position.getRow()-1][position.getColumn()-1] = null;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow()-1][position.getColumn()-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        int i;
        for (i = 1; i < 9; i++) {
            addPiece(new ChessPosition(2, i), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
        }
        for (i = 1; i < 9; i++) {
            addPiece(new ChessPosition(7, i), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        }
        addPiece(new ChessPosition(1, 1), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(1, 2), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(1, 3), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(1, 4), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(1, 5), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING));
        addPiece(new ChessPosition(1, 6), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(1, 7), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(1, 8), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));

        addPiece(new ChessPosition(8, 1), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(8, 2), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(8, 3), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(8, 4), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(8, 5), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING));
        addPiece(new ChessPosition(8, 6), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(8, 7), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(8, 8), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
    }

    @Override
    public String toString() {
        return java.util.Arrays.deepToString(board);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }
}
