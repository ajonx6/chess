package org.ajonx.cpu;

import org.ajonx.Moves;
import org.ajonx.Piece;
import org.ajonx.games.GameManager;

import java.util.List;
import java.util.Map;

public abstract class CPU implements Runnable {
	protected Thread thread;
	protected GameManager gameManager;
	protected boolean running = true;
	protected int delayMs = 1;
	protected int color;

	public CPU(GameManager gameManager, int color) {
		this.gameManager = gameManager;
		this.color = color;
		this.thread = new Thread(this);
	}

	public void start() {
		thread.start();
	}

	public void stop() {
		running = false;
	}

	public void run() {
		while (running && !gameManager.state.isGameOver()) {
			// Wait until it's this CPU's turn
			if (!isMyTurn()) {
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
				continue;
			}

			if (!running) return;

			try {
				Thread.sleep(delayMs); // optional thinking delay
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}

			if (!running) return;

			// Get legal moves for this CPU's color
			Map<Integer, List<Moves.Move>> movesPerSquare = gameManager.getLegalMoves();
			if (movesPerSquare == null) return;
			Moves.Move chosenMove = pickMove(movesPerSquare);

			if (running && chosenMove != null) {
				gameManager.makeMove(chosenMove);
			}
		}
	}

	public abstract Moves.Move pickMove(Map<Integer, List<Moves.Move>> movesPerSquare);

	protected abstract boolean isMyTurn();
}