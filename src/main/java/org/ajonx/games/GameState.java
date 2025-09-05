package org.ajonx.games;

import org.ajonx.Board;
import org.ajonx.ChessApp;
import org.ajonx.MoveHandler;

import java.awt.*;

public class GameState {
	public Board board;
	public MoveHandler moveHandler;
	public ChessApp chessApp;

	public GameState(ChessApp chessApp, Board board, MoveHandler moveHandler) {
		this.chessApp = chessApp;
		this.board = board;
		this.moveHandler = moveHandler;
	}

	public boolean isGameOver() {
		// implement checkmate/stalemate detection
		return false;
	}
}
