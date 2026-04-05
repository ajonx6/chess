package org.ajonx.moves;

import org.ajonx.Board;
import org.ajonx.Constants;
import org.ajonx.pieces.Piece;

import java.util.ArrayList;
import java.util.List;

public class KnightMoveRule implements PieceMoveRule {
	private final int[] offsets = { 15, 17, 10, -6, -15, -17, -10, 6 };

	@Override
	public List<Move> generateMoves(Board board, int piece, int square) {
		List<Move> moves = new ArrayList<>();

		int pieceColor = Piece.getColor(piece);
		int file = square % 8;
		int rank = square / 8;

		for (int offset : offsets) {
			int targetSquare = square + offset;
			if (targetSquare < 0 || targetSquare >= Constants.NUM_CELLS) continue;

			int targetFile = targetSquare % 8;
			int targetRank = targetSquare / 8;
			int df = Math.abs(targetFile - file);
			int dr = Math.abs(targetRank - rank);
			if (!((df == 1 && dr == 2) || (df == 2 && dr == 1))) continue;

			int targetPiece = board.get(targetSquare);
			if (!Piece.isColor(targetPiece, pieceColor)) {
				moves.add(new Move(square, targetSquare));
			}
		}

		return moves;
	}
}