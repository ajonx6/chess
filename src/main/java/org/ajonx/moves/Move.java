package org.ajonx.moves;

public class Move {
	private final int from, to;

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
}