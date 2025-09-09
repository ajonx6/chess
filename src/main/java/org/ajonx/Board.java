package org.ajonx;

import org.ajonx.pieces.Piece;
import org.ajonx.pieces.PieceTracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Board {
	public static final int CASTLE_WHITE_KING = 0b0001;
	public static final int CASTLE_WHITE_QUEEN = 0b0010;
	public static final int CASTLE_BLACK_KING = 0b0100;
	public static final int CASTLE_BLACK_QUEEN = 0b1000;

	public int width;
	public int height;
	public int[] board;
	public int colorToMove = Piece.WHITE;
	public Map<Integer, PieceTracker> pieceMap = new HashMap<>();

	public int enPassantTarget = -1;
	public int castlingRights = -1;

	public Board(int width, int height) {
		this.width = width;
		this.height = height;
		this.board = new int[width * height];
	}

	public void loadGame(String fen) {
		for (int type = Piece.KING; type <= Piece.PAWN; type++) {
			pieceMap.put(type, new PieceTracker());
		}

		String[] sections = fen.split(" ");

		// Section 0 - board layout
		String[] ranks = sections[0].split("/");
		for (int rank = 0; rank < height; rank++) {
			String rankData = ranks[rank];
			int file = 0;
			for (char c : rankData.toCharArray()) {
				if (Character.isDigit(c)) {
					file += Character.getNumericValue(c);
				} else {
					int pieceColor = Character.isUpperCase(c) ? Piece.WHITE : Piece.BLACK;
					int pieceType = Piece.getPieceFromChar(Character.toLowerCase(c));
					set(file, height - 1 - rank, pieceColor | pieceType);

					if (pieceColor == Piece.WHITE) {
						pieceMap.get(pieceType).whiteSquares.add(index(file, height - 1 - rank));
					} else {
						pieceMap.get(pieceType).blackSquares.add(index(file, height - 1 - rank));
					}
					file++;
				}
			}
		}

		// Section 1 - turn
		if (sections[1].equals("w")) colorToMove = Piece.WHITE;
		else colorToMove = Piece.BLACK;

		// Section 2 - castling rights
		if (!sections[2].equals("-")) {
			if (sections[2].contains("K")) castlingRights |= CASTLE_WHITE_KING;
			if (sections[2].contains("Q")) castlingRights |= CASTLE_WHITE_QUEEN;
			if (sections[2].contains("k")) castlingRights |= CASTLE_BLACK_KING;
			if (sections[2].contains("q")) castlingRights |= CASTLE_BLACK_QUEEN;
		}

		// Others
		enPassantTarget = -1;
	}

	public boolean isImmaterial() {
		List<Pair<Integer, Integer>> pieces = getAllPieces();

		int nonKingCount = 0;
		int whiteBishopSquare = -1, blackBishopSquare = -1;
		int whiteKnightSquare = -1, blackKnightSquare = -1;

		for (Pair<Integer, Integer> pieceData : pieces) {
			int piece = pieceData.first();
			int index = pieceData.second();

			if (piece == Piece.INVALID) continue;
			int type = Piece.getType(piece);
			if (type == Piece.KING) continue;

			nonKingCount++;

			if (type == Piece.PAWN || type == Piece.ROOK || type == Piece.QUEEN) return false;

			if (type == Piece.BISHOP) {
				if (Piece.isColor(piece, Piece.WHITE)) {
					if (whiteBishopSquare >= 0) return false;
					else whiteBishopSquare = index;
				} else {
					if (blackBishopSquare >= 0) return false;
					else blackBishopSquare = index;
				}
			}

			if (type == Piece.KNIGHT) {
				if (Piece.isColor(piece, Piece.WHITE)) {
					if (whiteKnightSquare >= 0) return false;
					else whiteKnightSquare = index;
				} else {
					if (blackKnightSquare >= 0) return false;
					else blackKnightSquare = index;
				}
			}
		}


		// Case 1: King vs King
		if (nonKingCount == 0) return true;
		// Case 2: King + minor vs King
		if (nonKingCount == 1) return true;

		// Case 3: King + Bishop vs King + Bishop
		if (nonKingCount == 2 && whiteBishopSquare >= 0 && blackBishopSquare >= 0) {
			int wbf = whiteBishopSquare % width;
			int wbr = whiteBishopSquare / width;
			int bbf = blackBishopSquare % width;
			int bbr = blackBishopSquare / width;
			return (wbf + wbr) % 2 == (bbf + bbr) % 2;
		}

		// Case 4: King + Knight vs King + Knight
		if (nonKingCount == 2 && whiteKnightSquare >= 0 && blackKnightSquare >= 0) return true;

		// Case 5: others
		return false;
	}

	public List<Pair<Integer, Integer>> getAllPieces() {
		List<Pair<Integer, Integer>> pieces = new ArrayList<>();

		// for (int pieceType : pieceMap.keySet()) {
		// 	List<List<Integer>> indiciesColors = pieceMap.get(pieceType);
		// 	for (int color = 0; color < 2; color++) {
		// 		List<Integer> indicies = indiciesColors.get(color);
		// 		for (int index : indicies) {
		// 			pieces.add(new Pair<>(pieceType | Piece.colorFromIndex(color), index));
		// 		}
		// 	}
		// }

		for (int file = 0; file < width; file++) {
			for (int rank = 0; rank < height; rank++) {
				int piece = get(file, rank);
				if (piece != Piece.INVALID) pieces.add(new Pair<>(piece, index(file, rank)));
			}
		}

		return pieces;
	}

	public void printBoard() {
		for (int rank = height - 1; rank >= 0; rank--) {
			for (int file = 0; file < width; file++) {
				System.out.print(Piece.toString(get(file, rank)));
			}
			System.out.println();
		}
		System.out.println();
	}

	public int indexOfKing(int colorToFind) {
		// return pieceMap.get(Piece.KING).get(Piece.colorIndex(colorToFind)).get(0);
		for (int i = 0; i < board.length; i++) {
			int piece = board[i];
			if (Piece.isType(piece, Piece.KING) && Piece.isColor(piece, colorToFind)) return i;
		}
		return -1;
	}

	public int index(int file, int rank) {
		return rank * width + file;
	}

	public int get(int file, int rank) {
		return board[index(file, rank)];
	}

	public int get(int index) {
		return board[index];
	}

	public void set(int file, int rank, int piece) {
		board[index(file, rank)] = piece;
	}

	public void set(int index, int piece) {
		board[index] = piece;
	}

	public boolean hasCastleRight(int flag) {
		return (castlingRights & flag) > 0;
	}

	public void removeCastleRight(int flag) {
		castlingRights &= ~flag;
	}

	public int size() {
		return width * height;
	}
}