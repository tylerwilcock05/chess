package client;

import chess.*;
import ui.BoardRenderer;

public class ClientMain {
    public static void main(String[] args) {
        var board = new ChessBoard();
        board.resetBoard();

        System.out.println("♕ 240 Chess Client: ");
        System.out.println("White's Perspective:");
        BoardRenderer.renderBoard(board, ChessGame.TeamColor.WHITE);
        System.out.println("Black's Perspective:");
        BoardRenderer.renderBoard(board, ChessGame.TeamColor.BLACK);
    }
}
