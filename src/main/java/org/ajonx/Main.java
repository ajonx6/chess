package org.ajonx;

import org.ajonx.games.SessionManager;
import org.ajonx.games.cpu.RandomCPU;

public class Main {
	public static void main(String[] args) {
		// ChessApp chessApp = new ChessApp();

		// GameManager gameManager = new GameManager();
		// GameInstances gameInstances = new GameInstances(chessApp, chessApp.getBoard(), chessApp.getMoveHandler());
		// gameManager.setInstances(gameInstances);
		// chessApp.setGameManager(gameManager);
		//
		// // CPU white = null;
		// CPU white = new RandomCPU(gameManager, Piece.WHITE);
		// CPU black = new RandomCPU(gameManager, Piece.BLACK);
		// gameManager.setWhiteCPU(white);
		// gameManager.setBlackCPU(black);
		//
		// gameManager.startGame();

		SessionManager manager = new SessionManager(new RandomCPU(null, Piece.WHITE), new RandomCPU(null, Piece.BLACK), 500);
		manager.run();
	}
}