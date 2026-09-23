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

    private boolean isInBounds(int row, int col) {
        if (row < 1 || row > 8 || col < 1 || col > 8) {
            return false;
        }
        return true;
    }

    private Collection<ChessMove> getSliderValidMoves(ChessBoard board, ChessPosition myPosition, int[][] directions) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        int newRow;
        int newCol;
        Collection<ChessMove> validMoves = new ArrayList<>(27);
        ChessPiece ogPiece = board.getPiece(myPosition);
        ChessGame.TeamColor ogColor = ogPiece.pieceColor;

        for (int[] dir : directions) {
            for (int i= 1; i < 8; i++) {
                newRow = row + dir[0] * i;
                newCol = col + dir[1] * i;
                if (isInBounds(newRow, newCol)) {
                    ChessPiece newPiece = board.getPiece(new ChessPosition(newRow, newCol));
                    if (newPiece == null || newPiece.pieceColor != ogColor) {
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
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        int newRow;
        int newCol;
        Collection<ChessMove> validMoves = new ArrayList<>(27);
        ChessPiece ogPiece = board.getPiece(myPosition);
        ChessGame.TeamColor ogColor = ogPiece.pieceColor;

        if (ogPiece.getPieceType() == PieceType.KNIGHT) {
            int[][] indexes = {{1, 2}, {1, -2}, {-1, 2}, {-1, -2}, {2, 1}, {2, -1}, {-2, 1}, {-2, -1}};
            for (int[] index : indexes) {
                newRow = row + index[0];
                newCol = col + index[1];
                if (isInBounds(newRow, newCol)) {
                    ChessPiece newPiece = board.getPiece(new ChessPosition(newRow, newCol));
                    if (newPiece == null || newPiece.pieceColor != ogColor) {
                        validMoves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(newRow, newCol), null));
                    }
                }
            }
            return validMoves;
        }

        if (ogPiece.getPieceType() == PieceType.BISHOP) {
            int[][] directions = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
            return getSliderValidMoves(board, myPosition, directions);
        }

        if (ogPiece.getPieceType() == PieceType.ROOK) {
            int[][] directions = {{0, 1}, {0, -1}, {-1, 0}, {1, 0}};
            return getSliderValidMoves(board, myPosition, directions);
        }

        if (ogPiece.getPieceType() == PieceType.QUEEN) {
            int[][] directions = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}, {0, 1}, {0, -1}, {-1, 0}, {1, 0}};
            return getSliderValidMoves(board, myPosition, directions);
        }

        if (ogPiece.getPieceType() == PieceType.KING) {
            int[][] indexes = {{-1, 1}, {0, 1}, {1, 1}, {-1, 0}, {1, 0}, {-1, -1}, {0, -1}, {1, -1}};
            for (int[] index : indexes) {
                newRow = row + index[0];
                newCol = col + index[1];
                if (isInBounds(newRow, newCol)) {
                    ChessPiece newPiece = board.getPiece(new ChessPosition(newRow, newCol));
                    if (newPiece == null || newPiece.pieceColor != ogColor) {
                        validMoves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(newRow, newCol), null));
                    }
                }
            }
            return validMoves;
        }

        if (ogPiece.getPieceType() == PieceType.PAWN) {
            int startRow;
            int promotionRow;
            int dir;
            if (ogPiece.pieceColor == ChessGame.TeamColor.WHITE) {
                startRow = 2;
                promotionRow = 8;
                dir = 1;
            }
            else {
                startRow = 7;
                promotionRow = 1;
                dir = -1;
            }
            newRow = row + dir;
            newCol = col;

            // check up one
            if (isInBounds(newRow, newCol)) {
                ChessPiece newPiece = board.getPiece(new ChessPosition(newRow, newCol));
                if (newPiece == null) {
                    if (newRow == promotionRow) {
                        validMoves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(newRow, newCol), PieceType.QUEEN));
                        validMoves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(newRow, newCol), PieceType.KNIGHT));
                        validMoves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(newRow, newCol), PieceType.BISHOP));
                        validMoves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(newRow, newCol), PieceType.ROOK));
                    }
                    else {
                        validMoves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(newRow, newCol), null));
                    }

                    // check up two
                    int up2Row = row + dir * 2;
                    if (isInBounds(up2Row, newCol) && row == startRow) {
                        ChessPiece up2Piece = board.getPiece(new ChessPosition(up2Row, newCol));
                        if (up2Piece == null)
                        {
                            validMoves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(up2Row, newCol), null));
                        }
                    }
                }
            }

            // check captures
            for (int i = -1; i < 2; i = i + 2) {
                newRow = row + dir;
                newCol = col + i;
                if (isInBounds(newRow, newCol)) {
                    ChessPiece newPiece = board.getPiece(new ChessPosition(newRow, newCol));
                    if (newPiece != null && newPiece.pieceColor != ogColor) {
                        if (newRow == promotionRow) {
                            validMoves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(newRow, newCol), PieceType.QUEEN));
                            validMoves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(newRow, newCol), PieceType.KNIGHT));
                            validMoves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(newRow, newCol), PieceType.BISHOP));
                            validMoves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(newRow, newCol), PieceType.ROOK));
                        }
                        else {
                            validMoves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(newRow, newCol), null));
                        }

                    }
                }
            }

            return validMoves;
        }

        return List.of();
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