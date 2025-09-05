package org.ajonx;

public class Piece {
	public static final int WHITE = 0b01000;
	public static final int BLACK = 0b10000;

	public static final int INVALID = 0b000;
	public static final int KING = 0b001;
	public static final int QUEEN = 0b010;
	public static final int BISHOP = 0b011;
	public static final int KNIGHT = 0b100;
	public static final int ROOK = 0b101;
	public static final int PAWN = 0b110;

	public static int getType(int piece) {
		return piece & 0b00111;
	}

	public static boolean isType(int piece, int type) {
		return (piece & 0b00111) == type;
	}

	public static int getColor(int piece) {
		return piece & 0b11000;
	}

	public static boolean isColor(int piece, int color) {
		return (piece & 0b11000) == color;
	}

	public static boolean matchColor(int piece1, int piece2) {
		return getColor(piece1) == getColor(piece2);
	}

	public static boolean matchOpposite(int piece1, int piece2) {
		int c1 = getColor(piece1), c2 = getColor(piece2);

		if (c1 == WHITE) return c2 == BLACK;
		else if (c1 == BLACK) return c2 == WHITE;
		else return c2 == INVALID;
	}

	public static boolean isOppositeColor(int piece, int color) {
		int c = getColor(piece);
		if (c == WHITE) return color == BLACK;
		else if (c == BLACK) return color == WHITE;
		else return false;
	}

	public static int inverse(int color) {
		if (color == WHITE) return BLACK;
		else if (color == BLACK) return WHITE;
		else return INVALID;
	}

	public static boolean isSlider(int piece) {
		int type = getType(piece);
		return type == QUEEN || type == BISHOP || type == ROOK;
	}

	public static int getPieceFromChar(char c) {
		if (c == 'k') return KING;
		else if (c == 'q') return QUEEN;
		else if (c == 'b') return BISHOP;
		else if (c == 'n') return KNIGHT;
		else if (c == 'r') return ROOK;
		else if (c == 'p') return PAWN;
		else return INVALID;
	}

	public static String toString(int piece) {
		boolean white = (piece & WHITE) == WHITE;
		int type = piece & 0b111;

		String pieceChar;
		if (type == KING) pieceChar = "k";
		else if (type == QUEEN) pieceChar = "q";
		else if (type == BISHOP) pieceChar = "b";
		else if (type == KNIGHT) pieceChar = "n";
		else if (type == ROOK) pieceChar = "r";
		else if (type == PAWN) pieceChar = "p";
		else pieceChar = ".";

		return white ? pieceChar.toUpperCase() : pieceChar;
	}
}