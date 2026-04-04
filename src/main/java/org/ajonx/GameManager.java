package org.ajonx;

import org.ajonx.moves.Move;

import java.util.ArrayList;
import java.util.List;

public class GameManager {
	private Board board;
	private List<Runnable> listeners = new ArrayList<>();

	public GameManager(Board board) {
		this.board = board;
	}

	public void makeMove(Move move) {
		// Validate move and turn

		board.makeMove(move);
		listeners.forEach(Runnable::run);
	}

	public void addListener(Runnable listener) {
		listeners.add(listener);
	}

	public Board getBoard() {
		return board;
	}
}
