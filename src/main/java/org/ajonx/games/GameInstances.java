package org.ajonx.games;

import org.ajonx.Board;
import org.ajonx.ChessApp;
import org.ajonx.moves.MoveHandler;

public class GameInstances {
	public Board board;
	public MoveHandler moveHandler;
	public ChessApp chessApp;

	public GameInstances(ChessApp chessApp, Board board, MoveHandler moveHandler) {
		this.chessApp = chessApp;
		this.board = board;
		this.moveHandler = moveHandler;
	}
}
