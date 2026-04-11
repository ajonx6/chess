package org.ajonx.moves;

import org.ajonx.Board;
import org.ajonx.Constants;
import org.ajonx.pieces.Piece;

import java.util.ArrayList;
import java.util.List;

public class KingMoveRule implements PieceMoveRule {
	private final int[] offsets = { 8, -8, -1, 1, 7, -7, 9, -9 };

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
			if (Math.abs(targetFile - file) > 1 || Math.abs(targetRank - rank) > 1) continue;

			int targetPiece = board.get(targetSquare);
			if (!Piece.isColor(targetPiece, pieceColor)) {
				moves.add(new Move(square, targetSquare));
			}
		}

		if (pieceColor == Piece.WHITE && square == Constants.E1) {
			if (board.hasCastlingRight(Board.CASTLE_WHITE_KING)) {
				int f1 = Constants.F1;
				int g1 = Constants.G1;
				if (board.get(f1) == Piece.INVALID && board.get(g1) == Piece.INVALID) moves.add(new Move(square, g1));
			}

			if (board.hasCastlingRight(Board.CASTLE_WHITE_QUEEN)) {
				int d1 = Constants.D1;
				int c1 = Constants.C1;
				int b1 = Constants.B1;
				if (board.get(d1) == Piece.INVALID && board.get(c1) == Piece.INVALID && board.get(b1) == Piece.INVALID) moves.add(new Move(square, c1));
			}
		}

		if (pieceColor == Piece.BLACK && square == Constants.E8) {
			if (board.hasCastlingRight(Board.CASTLE_BLACK_KING)) {
				int f8 = Constants.F8;
				int g8 = Constants.G8;
				if (board.get(f8) == Piece.INVALID && board.get(g8) == Piece.INVALID) moves.add(new Move(square, g8));
			}

			if (board.hasCastlingRight(Board.CASTLE_BLACK_QUEEN)) {
				int d8 = Constants.D8;
				int c8 = Constants.C8;
				int b8 = Constants.B8;
				if (board.get(d8) == Piece.INVALID && board.get(c8) == Piece.INVALID && board.get(b8) == Piece.INVALID) moves.add(new Move(square, c8));
			}
		}

		return moves;
	}
}