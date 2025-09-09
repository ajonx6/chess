package org.ajonx.pieces.behaviours;

import org.ajonx.Board;
import org.ajonx.moves.Move;
import org.ajonx.pieces.Piece;

import java.util.ArrayList;
import java.util.List;

public class KingBehaviour implements PieceBehaviour {
	public static int[] directionOffsets = new int[]{ 8, -8, -1, 1, 7, -7, 9, -9 };

	public static List<Move> getMovement(Board board, int file, int rank, int movingPiece) {
		List<Move> moves = new ArrayList<>();
		int startSquare = file + rank * board.width;

		for (int dirIndex = 0; dirIndex < 8; dirIndex++) {
			int targetSquare = startSquare + directionOffsets[dirIndex];
			if (targetSquare < 0 || targetSquare >= 64) continue;
			int targetFile = targetSquare % 8;
			if (Math.abs(targetFile - file) > 1) continue;

			int pieceOnTarget = board.get(targetSquare);

			if (!Piece.matchColor(pieceOnTarget, movingPiece)) {
				moves.add(new Move(startSquare, targetSquare));
			}
		}

		if (Piece.isColor(movingPiece, Piece.WHITE)) {
			int crank = 0;

			if (board.hasCastleRight(Board.CASTLE_WHITE_KING)) {
				if (board.get(5, crank) == Piece.INVALID &&
						board.get(6, crank) == Piece.INVALID) {
					moves.add(new Move(startSquare, file + 2 + crank * 8));
				}
			}

			if (board.hasCastleRight(Board.CASTLE_WHITE_QUEEN)) {
				if (board.get(1, crank) == Piece.INVALID &&
						board.get(2, crank) == Piece.INVALID &&
						board.get(3, crank) == Piece.INVALID) {
					moves.add(new Move(startSquare, file - 2 + crank * 8));
				}
			}
		} else {
			int crank = 7;

			if (board.hasCastleRight(Board.CASTLE_BLACK_KING)) {
				if (board.get(5, crank) == Piece.INVALID &&
						board.get(6, crank) == Piece.INVALID) {
					moves.add(new Move(startSquare, file + 2 + crank * 8));
				}
			}

			if (board.hasCastleRight(Board.CASTLE_BLACK_QUEEN)) {
				if (board.get(1, crank) == Piece.INVALID &&
						board.get(2, crank) == Piece.INVALID &&
						board.get(3, crank) == Piece.INVALID) {
					moves.add(new Move(startSquare, file - 2 + crank * 8));
				}
			}
		}

		return moves;
	}

	public static List<Move> getAttacks(Board board, int file, int rank, int movingPiece) {
		List<Move> moves = new ArrayList<>();
		int startSquare = file + rank * board.width;

		for (int dirIndex = 0; dirIndex < 8; dirIndex++) {
			int targetSquare = startSquare + directionOffsets[dirIndex];
			if (targetSquare < 0 || targetSquare >= 64) continue;
			int targetFile = targetSquare % 8;
			if (Math.abs(targetFile - file) > 1) continue;
			moves.add(new Move(startSquare, targetSquare));
		}

		return moves;
	}
}