package org.ajonx.moves;

import org.ajonx.pieces.Piece;

import java.util.Objects;

public class Move {
	private final int from, to;
	private int promotionPiece;
	private boolean enpassant;

	public Move(int from, int to) {
		this.from = from;
		this.to = to;
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

	public void setPromotionPiece(int promotionPiece) {
		this.promotionPiece = promotionPiece;
	}

	public boolean isEnpassant() {
		return enpassant;
	}

	public void setEnpassant(boolean enpassant) {
		this.enpassant = enpassant;
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