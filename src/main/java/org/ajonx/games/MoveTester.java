package org.ajonx.games;

import org.ajonx.Board;
import org.ajonx.Window;
import org.ajonx.games.cpu.CPU;
import org.ajonx.moves.Move;
import org.ajonx.moves.MoveHandler;
import org.ajonx.moves.MoveState;
import org.ajonx.moves.Moves;
import org.ajonx.pieces.Piece;

import java.util.*;

public class MoveTester {
	private int depth;
	private String startFen;

	public MoveTester(int depth, String startFen) {
		this.depth = depth;
		this.startFen = startFen;
	}

	public GameManager setupGame() {
		GameManager game = new GameManager();

		Board board = new Board(Window.GRID_SIZE, Window.GRID_SIZE);
		board.loadFromFEN(startFen);
		MoveHandler moveHandler = new MoveHandler(board);
		Moves.init(board, moveHandler);

		game.headless();
		game.setInstances(new GameInstances(null, board, moveHandler));

		game.setWhiteCPU(null);
		game.setBlackCPU(null);

		return game;
	}

	public void run() {
		// GameManager gameManager = setupGame();

		// int piece1 = gameManager.instances.board.get(7, 1);
		// gameManager.instances.moveHandler.makeMove(7, 1, 7, 3, piece1);
		// int piece2 = gameManager.instances.board.get(0, 6);
		// gameManager.instances.moveHandler.makeMove(0, 6, 0, 4, piece2);
		// List<List<Move>> histories = getHistories(4, gameManager);
		// System.out.println("Number of moves " + histories.size());
		// printHistoryGroupBy(histories, 1);

		for (int i = 1; i <= depth; i++) {
			GameManager gameManager = setupGame();
			long numMoves = run(i, gameManager);
			System.out.println("Depth " + i + " = " + numMoves);
		}
	}

	public void printHistoryGroupBy(List<List<Move>> histories, int groupSize) {
		if (histories.isEmpty() || histories.get(0).size() < groupSize) return;

		Map<String, List<List<Move>>> count = new TreeMap<>();
		for (List<Move> history : histories) {
			String prefix = "";
			int i = 0;
			for (; i < groupSize; i++) {
				prefix += history.get(i) + " ";
			}
			if (!count.containsKey(prefix)) count.put(prefix, new ArrayList<>());
			count.get(prefix).add(history);
		}

		for (String key : count.keySet()) {
			System.out.println(key + "= " + count.get(key).size());
		}
	}

	public List<List<Move>> getHistories(int depth, GameManager gameManager) {
		if (depth == 0) {
			List<List<Move>> history = new ArrayList<>();
			history.add(new ArrayList<>(gameManager.instances.moveHandler.history));
			return history;
		}

		Map<Integer, List<Move>> moveMap = gameManager.getLegalMoves();
		List<Move> moves = moveMap.values().stream().flatMap(List::stream).toList();
		List<List<Move>> history = new ArrayList<>();

		for (Move move : moves) {
			MoveState moveState = gameManager.instances.moveHandler.makeTemporaryMove(move);
			history.addAll(getHistories(depth - 1, gameManager));
			gameManager.instances.moveHandler.undoMove(moveState);
		}

		return history;
	}

	public long run(int depth, GameManager gameManager) {
		if (depth == 0) {
			return 1L;
		}

		Map<Integer, List<Move>> moveMap = gameManager.getLegalMoves();
		List<Move> moves = moveMap.values().stream().flatMap(List::stream).toList();
		int numPositions = 0;

		for (Move move : moves) {
			MoveState moveState = gameManager.instances.moveHandler.makeTemporaryMove(move);
			numPositions += run(depth - 1, gameManager);
			gameManager.instances.moveHandler.undoMove(moveState);
		}

		return numPositions;
	}
}