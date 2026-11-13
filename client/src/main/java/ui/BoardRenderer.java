package ui;

import chess.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public class BoardRenderer {

    private static final String BORDER_BG = SET_BG_COLOR_BLACK;
    private static final String BORDER_TEXT = SET_TEXT_COLOR_WHITE;

    public static void renderBoard(ChessBoard board, ChessGame.TeamColor perspective) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        boolean isWhite = (perspective == ChessGame.TeamColor.WHITE);
        int startRow = isWhite ? 8 : 1;
        int endRow = isWhite ? 1 : 8;
        int rowStep = isWhite ? -1 : 1;
        int startCol = isWhite ? 1 : 8;
        int endCol = isWhite ? 8 : 1;
        int colStep = isWhite ? 1 : -1;

        drawHeaderFooter(out, startCol, endCol, colStep);

        if (isWhite) {
            for (int row = startRow; row >= endRow; row += rowStep) {
                drawRow(out, board, row, startCol, endCol, colStep);
            }
        } else {
            for (int row = startRow; row <= endRow; row += rowStep) {
                drawRow(out, board, row, startCol, endCol, colStep);
            }
        }

        drawHeaderFooter(out, startCol, endCol, colStep);

        out.print(RESET_BG_COLOR + RESET_TEXT_COLOR);
        out.println();

    }

    private static void drawHeaderFooter(PrintStream out, int startCol, int endCol, int step) {
        out.print(BORDER_BG + BORDER_TEXT + "   ");
        for (int col = startCol; step > 0 ? col <= endCol : col >= endCol; col += step) {
            char colChar = (char) ('a' + (col - 1));
            out.print(" " + colChar + " ");
        }
        out.print("   " + RESET_BG_COLOR + RESET_TEXT_COLOR);
        out.println();
    }

    private static void drawRow(PrintStream out, ChessBoard board, int row, int startCol, int endCol, int step) {
        out.print(BORDER_BG + BORDER_TEXT + " " + row + " ");

        for (int col = startCol; step > 0 ? col <= endCol : col >= endCol; col += step) {
            boolean isLightSquare = (row + col) % 2 != 0;
            String bgColor = isLightSquare ? SET_BG_COLOR_LIGHT_GREY : SET_BG_COLOR_DARK_GREY;
            out.print(bgColor);

            ChessPiece piece = board.getPiece(new ChessPosition(row, col));
            out.print(getPieceString(piece));
        }

        out.print(BORDER_BG + BORDER_TEXT + " " + row + " " + RESET_BG_COLOR + RESET_TEXT_COLOR);
        out.println();
    }

    private static String getPieceString(ChessPiece piece) {
        if (piece == null) {
            return EMPTY;
        }

        boolean isWhite = piece.getTeamColor() == ChessGame.TeamColor.WHITE;
        String textColor = isWhite ? SET_TEXT_COLOR_WHITE : SET_TEXT_COLOR_BLACK;
        ChessPiece.PieceType type = piece.getPieceType();

        String glyph = EMPTY;
        if (type == ChessPiece.PieceType.KING) {
            glyph = isWhite ? WHITE_KING : BLACK_KING;
        } else if (type == ChessPiece.PieceType.QUEEN) {
            glyph = isWhite ? WHITE_QUEEN : BLACK_QUEEN;
        } else if (type == ChessPiece.PieceType.BISHOP) {
            glyph = isWhite ? WHITE_BISHOP : BLACK_BISHOP;
        } else if (type == ChessPiece.PieceType.KNIGHT) {
            glyph = isWhite ? WHITE_KNIGHT : BLACK_KNIGHT;
        } else if (type == ChessPiece.PieceType.ROOK) {
            glyph = isWhite ? WHITE_ROOK : BLACK_ROOK;
        } else if (type == ChessPiece.PieceType.PAWN) {
            glyph = isWhite ? WHITE_PAWN : BLACK_PAWN;
        }
        
        return textColor + glyph;
    }
}