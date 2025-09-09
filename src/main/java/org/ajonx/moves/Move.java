package org.ajonx.moves;

import java.util.Objects;

public class Move implements Comparable<Move> {
	public int startFile, startRank;
	public int endFile, endRank;
	public int promotionPiece;

	public Move(int startFile, int startRank, int endFile, int endRank) {
		this.startFile = startFile;
		this.startRank = startRank;
		this.endFile = endFile;
		this.endRank = endRank;
	}

	public Move(int startIndex, int endIndex) {
		this.startFile = startIndex % 8;
		this.startRank = startIndex / 8;
		this.endFile = endIndex % 8;
		this.endRank = endIndex / 8;
	}

	public Move(String square) {
		this.startFile = Character.toLowerCase(square.charAt(0)) - 'a';
		this.startRank = Character.toLowerCase(square.charAt(1)) - '1';
		this.endFile = Character.toLowerCase(square.charAt(2)) - 'a';
		this.endRank = Character.toLowerCase(square.charAt(3)) - '1';
	}

	public Move(Move move, int promotionPiece) {
		this(move.startFile, move.startRank, move.endRank, move.endRank);
		this.promotionPiece = promotionPiece;
	}

	public String toString() {
		String start = "" + (char) ('a' + startFile) + (startRank + 1);
		String end = "" + (char) ('a' + endFile) + (endRank + 1);
		return start + end;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Move move = (Move) o;
		return startFile == move.startFile && startRank == move.startRank && endFile == move.endFile && endRank == move.endRank;
	}

	@Override
	public int hashCode() {
		return Objects.hash(startFile, startRank, endFile, endRank);
	}

	@Override
	public int compareTo(Move o) {
		if (startFile != o.startFile) return Integer.compare(startFile, o.startFile);
		if (startRank != o.startRank) return Integer.compare(startRank, o.startRank);
		if (endFile != o.endFile) return Integer.compare(endFile, o.endFile);
		if (endRank != o.endRank) return Integer.compare(endRank, o.endRank);
		return 0;
	}
}
