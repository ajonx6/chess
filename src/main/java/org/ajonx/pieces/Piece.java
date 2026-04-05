package org.ajonx.pieces;

import org.ajonx.moves.*;

import java.util.Map;

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

	private static Map<Integer, PieceMoveRule> moveMap;

	public static int getPieceType(int piece) {
		return piece & 0b111;
	}

	public static int getColor(int piece) {
		return piece & 0b11000;
	}

	public static int getOppositeColor(int piece) {
		int pieceColor = getColor(piece);

		if (pieceColor == WHITE) return BLACK;
		else if (pieceColor == BLACK) return WHITE;
		else return INVALID;
	}

	public static boolean isPieceType(int piece, int type) {
		int pieceType = getPieceType(piece);
		return pieceType == type;
	}

	public static boolean isColor(int piece, int color) {
		int pieceColor = getColor(piece);
		return pieceColor == color;
	}

	public static boolean isOppositeColor(int piece, int color) {
		int pieceColor = getColor(piece);

		if (pieceColor == WHITE) return color == BLACK;
		else if (pieceColor == BLACK) return color == WHITE;
		else return false;
	}

	public static int getPieceFromChar(char c) {
		c = Character.toLowerCase(c);
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

	public static PieceMoveRule getMoveGenerator(int piece) {
		int pieceType = getPieceType(piece);
		if (pieceType == INVALID) return null;
		return moveMap.get(pieceType);
	}

	public static void initMoveGenerators() {
		moveMap = Map.of(
				Piece.PAWN, new PawnMoveRule(),
				Piece.KNIGHT, new KnightMoveRule(),
				Piece.BISHOP, new SlidingMoveRule(),
				Piece.ROOK, new SlidingMoveRule(),
				Piece.QUEEN, new SlidingMoveRule(),
				Piece.KING, new KingMoveRule()
		);
	}
}