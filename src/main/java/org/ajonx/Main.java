package org.ajonx;

import org.ajonx.games.GameInstances;
import org.ajonx.games.GameManager;
import org.ajonx.games.MoveTester;
import org.ajonx.games.SessionManager;
import org.ajonx.games.cpu.CPU;
import org.ajonx.games.cpu.RandomCPU;
import org.ajonx.pieces.Piece;

import java.util.TreeSet;

public class Main {
	public static final int NORMAL = 0;
	public static final int STATISTICS = 1;
	public static final int TESTING = 2;

	public static final int STATE = TESTING;

	public static void main(String[] args) {
		if (STATE == NORMAL) {
			Window chessApp = new Window();

			GameManager gameManager = new GameManager(chessApp);
			chessApp.setGameManager(gameManager);

			CPU white = null;
			// CPU white = new RandomCPU(gameManager, Piece.WHITE);
			CPU black = null;
			// CPU black = new RandomCPU(gameManager, Piece.BLACK);
			gameManager.setWhiteCPU(white);
			gameManager.setBlackCPU(black);

			gameManager.startGame();
		} else if (STATE == STATISTICS) {
			SessionManager manager = new SessionManager(new RandomCPU(null, Piece.WHITE), new RandomCPU(null, Piece.BLACK), 500);
			manager.run();
		} else if (STATE == TESTING) {
			MoveTester moveTester = new MoveTester(4, "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
			moveTester.run();
		}
	}
}