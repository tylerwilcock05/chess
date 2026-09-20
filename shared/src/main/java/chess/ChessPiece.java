package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {

        ChessPiece piece = board.getPiece(myPosition);
        Collection<ChessMove> validMovesArray = new ArrayList<>(27);
        int validMoves = 0;
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        int i;
        int j;
        int dirX = 1;
        int dirY = 1;
        int newXPos;
        int newYPos;
        if (piece.getPieceType() == PieceType.BISHOP) {
            for (i = 0; i < 4; i++) {
                for (j = 1; j < 8; j++) {
                    newXPos = row + dirY * j;
                    newYPos = col + dirX * j;
                    if (newXPos > 8 || newXPos < 1 || newYPos > 8 || newYPos < 1) {
                        break;
                    }
                    validMovesArray.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(newXPos, newYPos), null));
                }
                if (i % 2 == 0) {
                    dirX = dirX * -1;
                }
                else {
                    dirY = dirY * -1;
                }
            }
            return validMovesArray;
        }
        return List.of();
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", type=" + type +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}
