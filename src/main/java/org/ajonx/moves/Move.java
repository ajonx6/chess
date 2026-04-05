package org.ajonx.moves;

import org.ajonx.pieces.Piece;

import java.util.Objects;

public class Move {
	private final int from, to;
	private final int promotionPiece;

	public Move(int from, int to) {
		this(from, to, Piece.INVALID);
	}

	public Move(int from, int to, int promotionPiece) {
		this.from = from;
		this.to = to;
		this.promotionPiece = promotionPiece;
	}

	public int getFrom() {
		return from;
	}

	public int getTo() {
		return to;
	}

	public int getPromotionPiece() {
		return promotionPiece;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		Move move = (Move) o;
		return from == move.from && to == move.to;
	}

	@Override
	public int hashCode() {
		return Objects.hash(from, to);
	}

	@Override
	public String toString() {
		return "Move{" +
				"from=" + from +
				", to=" + to +
				", promotionPiece=" + promotionPiece +
				'}';
	}
}