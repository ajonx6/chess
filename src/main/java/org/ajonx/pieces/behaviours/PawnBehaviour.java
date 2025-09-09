package org.ajonx.pieces.behaviours;

import org.ajonx.Board;
import org.ajonx.Window;
import org.ajonx.games.GameManager;
import org.ajonx.moves.Move;
import org.ajonx.pieces.Piece;

import java.util.ArrayList;
import java.util.List;

public class PawnBehaviour implements PieceBehaviour {
	public static List<Move> getMovement(Board board, int file, int rank, int movingPiece) {
		List<Move> moves = new ArrayList<>();

		int baseRank = Piece.isColor(movingPiece, Piece.WHITE) ? 1 : 6;
		int direction = Piece.isColor(movingPiece, Piece.WHITE) ? 1 : -1;
		int nextRank = rank + direction;

		if (nextRank >= GameManager.GRID_SIZE || nextRank < 0) return moves;

		// Forward moves
		if (board.get(file, nextRank) == Piece.INVALID) {
			moves.add(new Move(file, rank, file, nextRank));
			if (rank == baseRank && board.get(file, rank + direction * 2) == Piece.INVALID) {
				moves.add(new Move(file, rank, file, rank + direction * 2));
			}
		}

		// Diagonal captures
		int diag1 = file - 1;
		int diag2 = file + 1;

		if (diag1 >= 0) {
			if (Piece.matchOppositeColor(board.get(diag1, nextRank), movingPiece)) {
				moves.add(new Move(file, rank, diag1, nextRank));
			}
			if (board.index(diag1, nextRank) == board.enPassantTarget) {
				moves.add(new Move(file, rank, diag1, nextRank));
			}
		}

		if (diag2 < GameManager.GRID_SIZE) { // ✅ check upper bound
			if (Piece.matchOppositeColor(board.get(diag2, nextRank), movingPiece)) {
				moves.add(new Move(file, rank, diag2, nextRank));
			}
			if (board.index(diag2, nextRank) == board.enPassantTarget) {
				moves.add(new Move(file, rank, diag2, nextRank));
			}
		}

		return moves;
	}


	public static List<Move> getAttacks(Board board, int file, int rank, int movingPiece) {
		List<Move> moves = new ArrayList<>();

		int direction = Piece.isColor(movingPiece, Piece.WHITE) ? 1 : -1;
		int targetRank = rank + direction;

		if (targetRank < 0 || targetRank >= GameManager.GRID_SIZE) return moves;

		int diagLeft = file - 1;
		int diagRight = file + 1;

		if (diagLeft >= 0) {
			moves.add(new Move(file, rank, diagLeft, targetRank)); // attacks left
		}
		if (diagRight < GameManager.GRID_SIZE) {
			moves.add(new Move(file, rank, diagRight, targetRank)); // attacks right
		}

		return moves;
	}
}