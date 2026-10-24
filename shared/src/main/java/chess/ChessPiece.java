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

    public static boolean isInBounds(int row, int col) {
        if (row < 1 || row > 8 || col < 1 || col > 8) {
            return false;
        }
        return true;
    }

    public Collection<ChessMove> getSliderValidMoves(ChessBoard board, ChessPosition myPosition, int[][] directions, boolean onlyOneHop) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        int newRow;
        int newCol;
        Collection<ChessMove> validMoves = new ArrayList<>(27);
        ChessPiece currentPiece = board.getPiece(myPosition);
        ChessGame.TeamColor currentColor = currentPiece.pieceColor;
        int max = 8;
        if (onlyOneHop) {
            max = 2;
        }
        for (int[] dir : directions) {
            for (int i= 1; i < max; i++) {
                newRow = row + dir[0] * i;
                newCol = col + dir[1] * i;
                if (isInBounds(newRow, newCol)) {
                    ChessPiece newPiece = board.getPiece(new ChessPosition(newRow, newCol));
                    if (newPiece == null || newPiece.pieceColor != currentColor) {
                        validMoves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(newRow, newCol), null));
                    }
                    if (newPiece != null) {
                        break;
                    }
                }
            }
        }
        return validMoves;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece currentPiece = board.getPiece(myPosition);
        if (currentPiece == null) {
            return List.of();
        }

        if (currentPiece.getPieceType() == PieceType.KNIGHT) {
            int[][] indexes = {{1, 2}, {1, -2}, {-1, 2}, {-1, -2}, {2, 1}, {2, -1}, {-2, 1}, {-2, -1}};
            return getSliderValidMoves(board, myPosition, indexes, true);
        }

        if (currentPiece.getPieceType() == PieceType.BISHOP) {
            int[][] directions = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
            return getSliderValidMoves(board, myPosition, directions, false);
        }

        if (currentPiece.getPieceType() == PieceType.ROOK) {
            int[][] directions = {{0, 1}, {0, -1}, {-1, 0}, {1, 0}};
            return getSliderValidMoves(board, myPosition, directions, false);
        }

        if (currentPiece.getPieceType() == PieceType.QUEEN) {
            int[][] directions = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}, {0, 1}, {0, -1}, {-1, 0}, {1, 0}};
            return getSliderValidMoves(board, myPosition, directions, false);
        }

        if (currentPiece.getPieceType() == PieceType.KING) {
            int[][] indexes = {{-1, 1}, {0, 1}, {1, 1}, {-1, 0}, {1, 0}, {-1, -1}, {0, -1}, {1, -1}};
            return getSliderValidMoves(board, myPosition, indexes, true);
        }

        if (currentPiece.getPieceType() == PieceType.PAWN) {
            return getPawnMoves(board, myPosition, currentPiece);
        }

        return List.of();
    }

    private Collection<ChessMove> getPawnMoves(ChessBoard board, ChessPosition myPosition, ChessPiece currentPiece) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        Collection<ChessMove> validMoves = new ArrayList<>(27);
        ChessGame.TeamColor currentColor = currentPiece.pieceColor;

        int startRow = 2;
        int promotionRow = 8;
        int direction = 1;
        if (currentColor != ChessGame.TeamColor.WHITE) {
            startRow = 7;
            promotionRow = 1;
            direction = -1;
        }

        int newRow = row + direction;
        if (isInBounds(newRow, col) && board.getPiece(new ChessPosition(newRow, col)) == null) {
            addPawnMove(validMoves, row, col, newRow, col, newRow == promotionRow);

            int up2Row = row + direction * 2;
            if (row == startRow && isInBounds(up2Row, col) && board.getPiece(new ChessPosition(up2Row, col)) == null) {
                validMoves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(up2Row, col), null));
            }
        }

        for (int i = -1; i <= 1; i += 2) {
            int newCol = col + i;
            if (isInBounds(newRow, newCol)) {
                ChessPiece newPiece = board.getPiece(new ChessPosition(newRow, newCol));
                if (newPiece != null && newPiece.pieceColor != currentColor) {
                    addPawnMove(validMoves, row, col, newRow, newCol, newRow == promotionRow);
                }
            }
        }

        return validMoves;
    }

    private void addPawnMove(Collection<ChessMove> validMoves, int fromRow, int fromCol,
                             int toRow, int toCol, boolean isPromotion) {
        ChessPosition from = new ChessPosition(fromRow, fromCol);
        ChessPosition to = new ChessPosition(toRow, toCol);
        if (isPromotion) {
            validMoves.add(new ChessMove(from, to, PieceType.QUEEN));
            validMoves.add(new ChessMove(from, to, PieceType.KNIGHT));
            validMoves.add(new ChessMove(from, to, PieceType.BISHOP));
            validMoves.add(new ChessMove(from, to, PieceType.ROOK));
        } else {
            validMoves.add(new ChessMove(from, to, null));
        }
    }

    @Override
    public String toString() {
        return String.format("%s %s", pieceColor, type);
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