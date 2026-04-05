package org.ajonx.moves;

import org.ajonx.Board;
import org.ajonx.Constants;
import org.ajonx.pieces.Piece;

import java.util.ArrayList;
import java.util.List;

public class SlidingMoveRule implements PieceMoveRule {
	private final int[] offsets = { 8, -8, -1, 1, 7, -7, 9, -9 };
	private final int[][] numSquaresToEdge = new int[Constants.GRID_SIZE * Constants.GRID_SIZE][8];

	public SlidingMoveRule() {
		calcSquaresToEdges();
	}

	private void calcSquaresToEdges() {
		for (int rank = 0; rank < Constants.GRID_SIZE; rank++) {
			for (int file = 0; file < Constants.GRID_SIZE; file++) {
				int numUp = Constants.GRID_SIZE - 1 - rank;
				int numDown = rank;
				int numLeft = file;
				int numRight = Constants.GRID_SIZE - 1 - file;

				int index = rank * Constants.GRID_SIZE + file;

				numSquaresToEdge[index] = new int[]{ numUp, numDown, numLeft, numRight, Math.min(numUp, numLeft), Math.min(numDown, numRight), Math.min(numUp, numRight), Math.min(numDown, numLeft) };
			}
		}
	}

	@Override
	public List<Move> generateMoves(Board board, int piece, int square) {
		List<Move> moves = new ArrayList<>();

		int pieceColor = Piece.getColor(piece);
		int startDirIndex = Piece.isPieceType(piece, Piece.BISHOP) ? 4 : 0;
		int endDirIndex = Piece.isPieceType(piece, Piece.ROOK) ? 4 : 8;

		for (int dirIndex = startDirIndex; dirIndex < endDirIndex; dirIndex++) {
			for (int n = 0; n < numSquaresToEdge[square][dirIndex]; n++) {
				int targetSquare = square + offsets[dirIndex] * (n + 1);
				if (targetSquare < 0 || targetSquare >= Constants.NUM_CELLS) continue;

				int targetPiece = board.get(targetSquare);

				if (Piece.isColor(targetPiece, pieceColor)) break;
				moves.add(new Move(square, targetSquare));
				if (Piece.isOppositeColor(targetPiece, pieceColor)) break;
			}
		}

		return moves;
	}
}