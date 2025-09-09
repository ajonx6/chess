package org.ajonx.games;

import org.ajonx.moves.Move;
import org.ajonx.pieces.Piece;

import java.util.*;
import java.util.stream.Collectors;

public class MoveTester {
	private int depth;
	private String startFen;

	public MoveTester(int depth, String startFen) {
		this.depth = depth;
		this.startFen = startFen;
	}

	public GameManager setupGame() {
		GameManager game = new GameManager(startFen);
		game.moveGenerator.precomputeData();

		game.headless();
		game.setWhiteCPU(null);
		game.setBlackCPU(null);

		return game;
	}

	public void run() {
		GameManager gameManager = setupGame();
		//		gameManager.moveGenerator.precomputeData();
		//		System.out.println(gameManager.moveGenerator.attackedSquares);
		//		System.out.println(gameManager.moveGenerator.pinnedPieces);
		//		System.out.println(gameManager.moveGenerator.checkers);
		//		gameManager.moveHandler.makeMove(new Move("d2d3"));
		//		gameManager.moveHandler.makeMove(new Move("c7c6"));
		//		gameManager.moveHandler.makeMove(new Move("e1d2"));
		//		gameManager.moveHandler.makeMove(new Move("d8a5"));
		//		List<List<Move>> histories = getHistories(1, gameManager);
		//		printHistoryGroupBy(histories, 1);
		//		System.out.println(histories.size());
		//
		//
		//
		for (int i = 1; i <= depth; i++) {
			gameManager = setupGame();
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
			List<Move> finalMoves = new ArrayList<>();
			for (List<Move> history : count.get(key)) {
				finalMoves.add(history.get(history.size() - 1));
			}
			System.out.println(key + "= " + count.get(key).size() + "   (" + finalMoves + ")");
		}
	}

	public List<List<Move>> getHistories(int depth, GameManager gameManager) {
		if (depth == 0) {
			List<List<Move>> history = new ArrayList<>();
			history.add(new ArrayList<>(gameManager.moveHandler.history));
			return history;
		}

		Map<Integer, List<Move>> moveMap = gameManager.getLegalMoves();
		List<Move> moves = moveMap.values().stream().flatMap(List::stream).toList();
		List<List<Move>> history = new ArrayList<>();

		for (Move move : moves) {
			gameManager.moveHandler.makeMove(move);
			gameManager.moveGenerator.precomputeData();
			history.addAll(getHistories(depth - 1, gameManager));
			gameManager.moveHandler.undoMove();
			gameManager.moveGenerator.precomputeData();
		}

		return history;
	}

	public long run(int depth, GameManager gameManager) {
		if (depth == 0) {
			return 1L;
		}

		gameManager.moveGenerator.precomputeData();
		Map<Integer, List<Move>> moveMap = gameManager.getLegalMoves();
		List<Move> moves = moveMap.values().stream().flatMap(List::stream).toList();
		long numPositions = 0;

		for (Move move : moves) {
			gameManager.moveHandler.makeMove(move);
			gameManager.moveGenerator.precomputeData();
			numPositions += run(depth - 1, gameManager);
			gameManager.moveHandler.undoMove();
			gameManager.moveGenerator.precomputeData();
		}

		return numPositions;
	}
}