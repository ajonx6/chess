package org.ajonx.games.cpu;

import org.ajonx.games.GameManager;
import org.ajonx.moves.Move;

import java.util.List;
import java.util.Map;

public abstract class CPU implements Runnable {
	protected Thread thread;
	protected GameManager gameManager;
	protected boolean running = true;
	protected int delayMs = 10;
	protected int color;

	public CPU(GameManager gameManager, int color) {
		this.gameManager = gameManager;
		this.color = color;
		this.thread = new Thread(this);
	}

	public void start() {
		if (gameManager.isHeadless()) return;
		thread.start();
	}

	public void stop() {
		if (gameManager.isHeadless()) return;
		running = false;
	}

	public void run() {
		while (running && !gameManager.isGameOver) {
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
				Thread.sleep(delayMs); // Simulate thinking
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}

			if (!running) return;

			// Get legal moves for this CPU's color
			Map<Integer, List<Move>> movesPerSquare = gameManager.getLegalMoves();
			if (movesPerSquare == null) return;

			Move chosenMove = pickMove(movesPerSquare);
			if (running && chosenMove != null) {
				int piece = gameManager.instances.board.get(chosenMove.sfile, chosenMove.srank);
				gameManager.runTurn(chosenMove, piece);
			}
		}
	}

	public abstract Move pickMove(Map<Integer, List<Move>> movesPerSquare);

	public abstract boolean isMyTurn();

	public abstract CPU copy(GameManager gameManager, int color);
}