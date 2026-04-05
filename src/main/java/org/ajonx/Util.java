package org.ajonx;

public class Util {
	private Util() {}

	public static int toFile(int index) {
		return index % 8;
	}

	public static int toRank(int index) {
		return index / 8;
	}

	public static int toIndex(int file, int rank) {
		return rank * Constants.GRID_SIZE + file;
	}
}
