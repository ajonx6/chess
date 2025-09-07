package org.ajonx.pieces.behaviours;

import org.ajonx.Board;
import org.ajonx.moves.Move;
import org.ajonx.pieces.Piece;

import java.util.ArrayList;
import java.util.List;

public class KnightBehaviour implements PieceBehaviour {
	public static int[] knightOffsets = new int[] { 6, -6, 10, -10, 15, -15, 17, -17 };

	public static List<Move> getMovement(Board board, int file, int rank, int movingPiece) {
		List<Move> moves = new ArrayList<>();
		int startSquare = file + rank * board.width;

		for (int dirIndex = 0; dirIndex < 8; dirIndex++) {
			int targetSquare = startSquare + knightOffsets[dirIndex];
			if (targetSquare < 0 || targetSquare >= 64) continue;
			int targetFile = targetSquare % 8;
			int targetRank = targetSquare / 8;
			int df = Math.abs(targetFile - file);
			int dr = Math.abs(targetRank - rank);
			if (!((df == 2 && dr == 1) || (df == 1 && dr == 2))) continue;

			int pieceOnTarget = board.get(targetSquare);

			if (!Piece.matchColor(pieceOnTarget, movingPiece)) {
				moves.add(new Move(startSquare, targetSquare));
			}
		}

		return moves;
	}

	public static List<Move> getAttacks(Board board, int file, int rank, int movingPiece) {
		return getMovement(board, file, rank, movingPiece);
	}
}