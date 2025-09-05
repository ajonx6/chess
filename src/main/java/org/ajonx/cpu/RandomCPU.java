package org.ajonx.cpu;

import org.ajonx.Moves;
import org.ajonx.games.GameManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class RandomCPU extends CPU {
	private static final Random RANDOM = new Random();

	public RandomCPU(GameManager gameManager, int color) {
		super(gameManager, color);
	}

	public Moves.Move pickMove(Map<Integer, List<Moves.Move>> movesPerSquare) {
		List<Moves.Move> allMoves = new ArrayList<>();
		for (int square : movesPerSquare.keySet()) {
			allMoves.addAll(movesPerSquare.get(square));
		}
		return allMoves.get(RANDOM.nextInt(allMoves.size()));
	}

	@Override
	protected boolean isMyTurn() {
		return color == gameManager.state.board.colorToMove;
	}
}