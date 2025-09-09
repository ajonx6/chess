package org.ajonx.games;

import org.ajonx.Board;
import org.ajonx.Window;
import org.ajonx.pieces.Piece;
import org.ajonx.games.cpu.CPU;
import org.ajonx.moves.Move;
import org.ajonx.moves.MoveHandler;
import org.ajonx.moves.MoveGenerator;

public class SessionManager {
	public int whiteWin = 0, blackWin = 0;
	public int stalemate = 0, insufficient = 0;

	private final int numGames;
	private final CPU whiteCPUTEmplate;
	private final CPU blackCPUTemplate;

	public SessionManager(CPU whiteCPUTEmplate, CPU blackCPUTemplate, int numGames) {
		this.whiteCPUTEmplate = whiteCPUTEmplate;
		this.blackCPUTemplate = blackCPUTemplate;
		this.numGames = numGames;
	}

	public GameManager setupGame() {
		GameManager game = new GameManager();

		game.headless();
		game.setWhiteCPU(whiteCPUTEmplate.copy(game, Piece.WHITE));
		game.setBlackCPU(blackCPUTemplate.copy(game, Piece.BLACK));

		return game;
	}

	public void run() {
		for (int i = 0; i < numGames; i++) {
			GameManager gameManager = setupGame();
			if ((i + 1) % 10 == 0) System.out.println("Game " + (i + 1) + " / " + numGames);

			while (gameManager.evaluateGameState() == GameState.ONGOING) {
				CPU current = (gameManager.board.colorToMove == Piece.WHITE) ? gameManager.whiteCPU : gameManager.blackCPU;
				Move move = current.pickMove(gameManager.getLegalMoves());
				gameManager.runTurn(move);
			}

			recordResult(gameManager);
		}

		printSummary();
	}

	private void recordResult(GameManager gameManager) {
		GameState finalState = gameManager.evaluateGameState();
		int turn = gameManager.board.colorToMove;

		switch (finalState) {
			case CHECKMATE -> {
				if (turn == Piece.WHITE) blackWin++;
				else whiteWin++;
			}
			case STALEMATE -> stalemate++;
			case DRAW_IMMATERIAL -> insufficient++;
			default -> {}
		}
	}

	private void printSummary() {
		System.out.println("\nSession complete:");
		System.out.println("White wins: " + whiteWin);
		System.out.println("Black wins: " + blackWin);
		System.out.println("Stalemates: " + stalemate);
		System.out.println("Insufficient material draws: " + insufficient);
	}
}