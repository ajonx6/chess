package org.ajonx.pieces.behaviours;

import org.ajonx.Board;
import org.ajonx.moves.Move;
import org.ajonx.pieces.Piece;

import java.util.ArrayList;
import java.util.List;

public class SlidingBehaviour implements PieceBehaviour {
	public static int[] directionOffsets = new int[]{ 8, -8, -1, 1, 7, -7, 9, -9 };

	public static List<Move> getMovement(Board board, int file, int rank, int movingPiece, int[][] numSquaresToEdge) {
		List<Move> moves = new ArrayList<>();
		int startSquare = file + rank * board.width;
		int startDirIndex = Piece.isType(movingPiece, Piece.BISHOP) ? 4 : 0;
		int endDirIndex = Piece.isType(movingPiece, Piece.ROOK) ? 4 : 8;

		for (int dirIndex = startDirIndex; dirIndex < endDirIndex; dirIndex++) {
			for (int n = 0; n < numSquaresToEdge[startSquare][dirIndex]; n++) {
				int targetSquare = startSquare + directionOffsets[dirIndex] * (n + 1);
				int pieceOnTarget = board.get(targetSquare);

				if (Piece.matchColor(pieceOnTarget, movingPiece)) break;
				moves.add(new Move(startSquare, targetSquare));
				if (Piece.matchOppositeColor(pieceOnTarget, movingPiece)) break;
			}
		}

		return moves;
	}

	public static List<Move> getAttacks(Board board, int file, int rank, int movingPiece, int[][] numSquaresToEdge) {
		List<Move> moves = new ArrayList<>();
		int startSquare = file + rank * board.width;
		int startDirIndex = Piece.isType(movingPiece, Piece.BISHOP) ? 4 : 0;
		int endDirIndex = Piece.isType(movingPiece, Piece.ROOK) ? 4 : 8;
		int sliderColor = Piece.getColor(movingPiece);

		for (int dirIndex = startDirIndex; dirIndex < endDirIndex; dirIndex++) {
			for (int n = 0; n < numSquaresToEdge[startSquare][dirIndex]; n++) {
				int targetSquare = startSquare + directionOffsets[dirIndex] * (n + 1);
				int pieceOnTarget = board.get(targetSquare);

				moves.add(new Move(startSquare, targetSquare));
				if (pieceOnTarget != Piece.INVALID) {
					if (Piece.isColor(pieceOnTarget, sliderColor)) break;
					if (Piece.isType(pieceOnTarget, Piece.KING)) continue;
					break;
				}
			}
		}

		return moves;
	}
}