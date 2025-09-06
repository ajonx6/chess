package org.ajonx.games;

import org.ajonx.Board;
import org.ajonx.Window;
import org.ajonx.moves.MoveHandler;

public class GameInstances {
	public Board board;
	public MoveHandler moveHandler;
	public Window window;

	public GameInstances(Window window, Board board, MoveHandler moveHandler) {
		this.window = window;
		this.board = board;
		this.moveHandler = moveHandler;
	}
}
