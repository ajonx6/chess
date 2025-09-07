package org.ajonx;

import org.ajonx.games.GameInstances;
import org.ajonx.games.GameManager;
import org.ajonx.games.MoveTester;
import org.ajonx.games.SessionManager;
import org.ajonx.games.cpu.CPU;
import org.ajonx.games.cpu.RandomCPU;
import org.ajonx.pieces.Piece;

public class Main {
	public static void main(String[] args) {
		// Window chessApp = new Window();
		//
		// GameManager gameManager = new GameManager();
		// GameInstances gameInstances = new GameInstances(chessApp, chessApp.getBoard(), chessApp.getMoveHandler());
		// gameManager.setInstances(gameInstances);
		// chessApp.setGameManager(gameManager);
		//
		// // CPU white = null;
		// CPU white = new RandomCPU(gameManager, Piece.WHITE);
		// // CPU black = null;
		// CPU black = new RandomCPU(gameManager, Piece.BLACK);
		// gameManager.setWhiteCPU(white);
		// gameManager.setBlackCPU(black);
		//
		// gameManager.startGame();

		// SessionManager manager = new SessionManager(new RandomCPU(null, Piece.WHITE), new RandomCPU(null, Piece.BLACK), 500);
		// manager.run();

		MoveTester moveTester = new MoveTester(6, "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
		moveTester.run();
	}
}