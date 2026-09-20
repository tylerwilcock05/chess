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
        int dirY = 1;
        int dirX = 1;
        int newXPos;
        int newYPos;

        if (piece.getPieceType() == PieceType.BISHOP || piece.getPieceType() == PieceType.QUEEN) {
            for (i = 0; i < 4; i++) {
                for (j = 1; j < 8; j++) {
                    newXPos = row + dirX * j;
                    newYPos = col + dirY * j;
                    ChessPosition newChessPosition = new ChessPosition(newXPos, newYPos);
                    if (newXPos > 8 || newXPos < 1 || newYPos > 8 || newYPos < 1) {
                        break;
                    }
                    ChessPiece pieceAtNewPos = board.getPiece(newChessPosition);
                    // Checks if there's a piece where the new position is. If so, it replaces a piece if it has opposite color or if it's the same color, it doesn't add move to list of valid moves
                    if (pieceAtNewPos != null) {
                        if (pieceAtNewPos.pieceColor != piece.pieceColor) {
                            validMovesArray.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(newXPos, newYPos), null));
                        }
                        break;
                    }
                    validMovesArray.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(newXPos, newYPos), null));

                }
                if (i % 2 == 0) {
                    dirY = dirY * -1;
                }
                else {
                    dirX = dirX * -1;
                }
            }
            if (piece.getPieceType() != PieceType.QUEEN) {
                return validMovesArray;
            }

        }

        if (piece.getPieceType() == PieceType.ROOK || piece.getPieceType() == PieceType.QUEEN) {
            dirY = 0;
            for (i = 0; i < 4; i++) {
                for (j = 1; j < 8; j++) {
                    newXPos = row + dirX * j;
                    newYPos = col + dirY * j;
                    ChessPosition newChessPosition = new ChessPosition(newXPos, newYPos);
                    if (newXPos > 8 || newXPos < 1 || newYPos > 8 || newYPos < 1) {
                        break;
                    }
                    ChessPiece pieceAtNewPos = board.getPiece(newChessPosition);
                    // Checks if there's a piece where the new position is. If so, it replaces a piece if it has opposite color or if it's the same color, it doesn't add move to list of valid moves
                    if (pieceAtNewPos != null) {
                        if (pieceAtNewPos.pieceColor != piece.pieceColor) {
                            validMovesArray.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(newXPos, newYPos), null));
                        }
                        break;
                    }
                    validMovesArray.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(newXPos, newYPos), null));

                }
                if (dirX > 0 || dirY > 0) {
                    int temp = dirX * -1;
                    dirX = dirY * -1;
                    dirY = temp;
                }
                else {
                    dirY = dirY * -1;
                }
            }
            return validMovesArray;
        }

        else if (piece.getPieceType() == PieceType.KNIGHT) {
            dirY = 2;
            dirX = 1;
            for (i = 0; i < 8; i++) {
                if (i % 2 == 0) {
                    int temp = dirY;
                    dirY = dirX;
                    dirX = temp;
                }
                else {
                    dirX = dirX * -1;
                }
                newXPos = row + dirX;
                newYPos = col + dirY;
                ChessPosition newChessPosition = new ChessPosition(newXPos, newYPos);
                if (newXPos > 8 || newXPos < 1 || newYPos > 8 || newYPos < 1) {
                    continue;
                }
                ChessPiece pieceAtNewPos = board.getPiece(newChessPosition);
                // Checks if there's a piece where the new position is. If so, it replaces a piece if it has opposite color or if it's the same color, it doesn't add move to list of valid moves
                if (pieceAtNewPos != null) {
                    if (pieceAtNewPos.pieceColor != piece.pieceColor) {
                        validMovesArray.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(newXPos, newYPos), null));
                    }
                    continue;
                }
                validMovesArray.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(newXPos, newYPos), null));
            }
            return validMovesArray;
        }

        else if (piece.getPieceType() == PieceType.KING) {
            dirX = 0;
            dirY = 1;
            for (i = 0; i < 8; i++) {
                if (i != 1 && i != 2 && i != 5 && i != 6) {
                    dirX = dirX + dirY;
                }
                else {
                    dirY = dirY - dirX;
                }
                newXPos = row + dirX;
                newYPos = col + dirY;
                ChessPosition newChessPosition = new ChessPosition(newXPos, newYPos);
                if (newXPos > 8 || newXPos < 1 || newYPos > 8 || newYPos < 1) {
                    continue;
                }
                ChessPiece pieceAtNewPos = board.getPiece(newChessPosition);
                // Checks if there's a piece where the new position is. If so, it replaces a piece if it has opposite color or if it's the same color, it doesn't add move to list of valid moves
                if (pieceAtNewPos != null) {
                    if (pieceAtNewPos.pieceColor != piece.pieceColor) {
                        validMovesArray.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(newXPos, newYPos), null));
                    }
                    continue;
                }
                validMovesArray.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(newXPos, newYPos), null));
            }
            return validMovesArray;
        }

        else if (piece.getPieceType() == PieceType.PAWN) {
            boolean startState = false;
            boolean isPromoting = false;
            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                dirY = 1;
                startState = (row == 2);
                isPromoting = (row == 7); // About to step onto row 8
            } else {
                dirY = -1;
                startState = (row == 7);
                isPromoting = (row == 2); // About to step onto row 1
            }
            newXPos = row;
            newYPos = col + dirY;

            for (i = -1; i < 2; i = i + 2) {
                newYPos = col + i;
                newXPos = row + dirY;
                if (newYPos > 8 || newYPos < 1) {
                    continue;
                }

                ChessPosition newChessPosition = new ChessPosition(newXPos, newYPos);
                ChessPiece pieceAtNewPos = board.getPiece(newChessPosition);
                if (pieceAtNewPos != null) {
                    if (isPromoting && pieceAtNewPos.pieceColor != piece.pieceColor) {
                        validMovesArray.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(newXPos, newYPos), PieceType.QUEEN));
                        validMovesArray.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(newXPos, newYPos), PieceType.KNIGHT));
                        validMovesArray.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(newXPos, newYPos), PieceType.BISHOP));
                        validMovesArray.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(newXPos, newYPos), PieceType.ROOK));
                    }
                    else if (pieceAtNewPos.pieceColor != piece.pieceColor) {
                        validMovesArray.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(newXPos, newYPos), null));
                    }
                }
            }

            ChessPosition newChessPosition = new ChessPosition(row + dirY, col);
            ChessPiece pieceAtNewPos = board.getPiece(newChessPosition);
            if (pieceAtNewPos == null && !isPromoting) {
                validMovesArray.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(row + dirY, col), null));
            }
            if (isPromoting) {
                validMovesArray.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(row + dirY, col), PieceType.QUEEN));
                validMovesArray.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(row + dirY, col), PieceType.KNIGHT));
                validMovesArray.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(row + dirY, col), PieceType.BISHOP));
                validMovesArray.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(row + dirY, col), PieceType.ROOK));
            }
            if (startState) {
                ChessPosition move2 = new ChessPosition(row + (dirY * 2), col);
                ChessPiece pieceAtMove2 = board.getPiece(move2);
                if (pieceAtMove2 == null && pieceAtNewPos == null) {
                    validMovesArray.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(row + (dirY * 2), col), null));
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
