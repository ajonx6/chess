package org.ajonx.moves;

import org.ajonx.Board;
import org.ajonx.Constants;
import org.ajonx.pieces.Piece;

import java.util.ArrayList;
import java.util.List;

public class PawnMoveRule implements PieceMoveRule {
	@Override
	public List<Move> generateMoves(Board board, int piece, int square) {
		List<Move> moves = new ArrayList<>();
		int color = Piece.getColor(piece);
		int file = square % 8;
		int rank = square / 8;

		int direction = (color == Piece.WHITE) ? 1 : -1;
		int startRank = (color == Piece.WHITE) ? 1 : 6;
		int promotionRank = (color == Piece.WHITE) ? 7 : 0;
		int enPassantSquare = board.getEnpassantSquare();

		int forwardSquare = square + direction * 8;
		if (forwardSquare >= 0 && forwardSquare < Constants.NUM_CELLS && board.get(forwardSquare) == Piece.INVALID) {
			Move move = new Move(square, forwardSquare);
			if (forwardSquare / 8 == promotionRank) {
				move.setPromotionPiece(Piece.QUEEN | color);
			}
			moves.add(move);

			if (rank == startRank) {
				int doubleForward = square + direction * 16;
				if (board.get(doubleForward) == Piece.INVALID) {
					moves.add(new Move(square, doubleForward));
				}
			}
		}

		int[] diagOffsets = { direction * 8 - 1, direction * 8 + 1 };
		for (int offset : diagOffsets) {
			int targetSquare = square + offset;
			if (targetSquare < 0 || targetSquare >= Constants.NUM_CELLS) continue;

			int targetFile = targetSquare % 8;
			int targetRank = targetSquare / 8;

			if (Math.abs(targetFile - file) != 1 || targetRank - rank != direction) continue;

			int targetPiece = board.get(targetSquare);
			if (Piece.isOppositeColor(targetPiece, color)) {
				Move move = new Move(square, targetSquare);
				if (targetRank == promotionRank) {
					move.setPromotionPiece(Piece.QUEEN | color);
				}
				moves.add(move);
			}

			if (targetSquare == enPassantSquare) {
				Move epMove = new Move(square, targetSquare);
				epMove.setEnpassant(true);
				moves.add(epMove);
			}
		}

		return moves;
	}
}