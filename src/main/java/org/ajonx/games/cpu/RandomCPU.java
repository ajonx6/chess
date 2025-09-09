package org.ajonx.games.cpu;

import org.ajonx.games.GameManager;
import org.ajonx.moves.Move;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class RandomCPU extends CPU {
	private static final Random RANDOM = new Random();

	public RandomCPU(GameManager gameManager, int color) {
		super(gameManager, color);
	}

	public Move pickMove(Map<Integer, List<Move>> movesPerSquare) {
		List<Move> allMoves = new ArrayList<>();
		for (int square : movesPerSquare.keySet()) {
			allMoves.addAll(movesPerSquare.get(square));
		}
		return allMoves.get(RANDOM.nextInt(allMoves.size()));
	}

	@Override
	public boolean isMyTurn() {
		return color == gameManager.board.colorToMove;
	}

	@Override
	public CPU copy(GameManager gameManager, int color) {
		return new RandomCPU(gameManager, color);
	}
}