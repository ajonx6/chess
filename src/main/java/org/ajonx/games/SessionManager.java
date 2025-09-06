package org.ajonx.games;

import org.ajonx.Board;
import org.ajonx.Window;
import org.ajonx.Piece;
import org.ajonx.games.cpu.CPU;
import org.ajonx.moves.Move;
import org.ajonx.moves.MoveHandler;
import org.ajonx.moves.Moves;

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

		Board board = new Board(Window.GRID_SIZE, Window.GRID_SIZE);
		MoveHandler moveHandler = new MoveHandler(board);
		Moves.init(board, moveHandler);

		game.headless();
		game.setInstances(new GameInstances(null, board, moveHandler));

		game.setWhiteCPU(whiteCPUTEmplate.copy(game, Piece.WHITE));
		game.setBlackCPU(blackCPUTemplate.copy(game, Piece.BLACK));

		return game;
	}

	public void run() {
		for (int i = 0; i < numGames; i++) {
			GameManager game = setupGame();
			if ((i + 1) % 10 == 0) System.out.println("Game " + (i + 1) + " / " + numGames);

			while (game.evaluateGameState() == GameState.ONGOING) {
				CPU current = (game.instances.board.colorToMove == Piece.WHITE) ? game.whiteCPU : game.blackCPU;
				Move move = current.pickMove(game.getLegalMoves());
				int piece = game.instances.board.get(move.sfile, move.srank);
				game.runTurn(move, piece);
			}

			recordResult(game);
		}

		printSummary();
	}

	private void recordResult(GameManager game) {
		GameState finalState = game.evaluateGameState();
		int turn = game.instances.board.colorToMove;

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