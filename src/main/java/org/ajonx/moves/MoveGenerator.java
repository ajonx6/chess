package org.ajonx.moves;

import org.ajonx.Constants;
import org.ajonx.GameManager;
import org.ajonx.pieces.Piece;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MoveGenerator {
	public static Map<Integer, List<Move>> generateMoves(GameManager manager) {
		Map<Integer, List<Move>> moves = new HashMap<>();

		for (int square = 0; square < Constants.NUM_CELLS; square++) {
			int piece = manager.getBoard().get(square);
			if (Piece.isColor(piece, manager.getColorToMove())) {
				moves.put(square, Piece.getMoveGenerator(piece).generateMoves(manager.getBoard(), piece, square));
			}
		}

		return moves;
	}
}
