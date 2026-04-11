package org.ajonx;

import org.ajonx.moves.Move;
import org.ajonx.pieces.Piece;

import java.util.ArrayList;
import java.util.List;

public class Board {
	public static final int CASTLE_WHITE_KING = 0b0001;
	public static final int CASTLE_WHITE_QUEEN = 0b0010;
	public static final int CASTLE_BLACK_KING = 0b0100;
	public static final int CASTLE_BLACK_QUEEN = 0b1000;

	public int width;
	public int height;
	public int[] board;

	public List<Integer> pieces = new ArrayList<>();
	private int enpassantSquare = -1;
	private int castlingRights = 0;

	public Board(int width, int height) {
		this.width = width;
		this.height = height;
		this.board = new int[width * height];
	}

	public int loadDefaultGame() {
		return loadGame("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
	}

	public int loadGame(String fen) {
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
					int piece = pieceColor | pieceType;

					set(file, height - 1 - rank, piece);
					pieces.add(piece);
					file++;
				}
			}
		}

		int colorToMove;
		// Section 1 - turn
		if (sections[1].equals("w")) colorToMove = Piece.WHITE;
		else colorToMove = Piece.BLACK;

		// Section 2 - castling rights
		if (sections[2].contains("K")) {
			castlingRights |= CASTLE_WHITE_KING;
		}
		if (sections[2].contains("Q")) {
			castlingRights |= CASTLE_WHITE_QUEEN;
		}
		if (sections[2].contains("k")) {
			castlingRights |= CASTLE_BLACK_KING;
		}
		if (sections[2].contains("q")) {
			castlingRights |= CASTLE_BLACK_QUEEN;
		}

		System.out.println(Integer.toBinaryString(castlingRights));

		return colorToMove;
	}

	public void makeMove(Move move) {
		int piece = get(move.getFrom());
		if (piece == Piece.INVALID) return;

		set(move.getTo(), piece);
		set(move.getFrom(), Piece.INVALID);
	}

	public void promote(Move move) {
		set(move.getTo(), move.getPromotionPiece());
	}

	public void loseCastlingRight(int flag) {
		castlingRights &= ~flag;
	}

	public boolean hasCastlingRight(int flag) {
		return (castlingRights & flag) != 0;
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

	public int index(int file, int rank) {
		return rank * width + file;
	}

	public int get(int file, int rank) {
		return get(index(file, rank));
	}

	public int get(int index) {
		return board[index];
	}

	public void set(int file, int rank, int piece) {
		set(index(file, rank), piece);
	}

	public void set(int index, int piece) {
		board[index] = piece;
	}

	public int getEnpassantSquare() {
		return enpassantSquare;
	}

	public void setEnpassantSquare(int enpassantSquare) {
		this.enpassantSquare = enpassantSquare;
	}

	public int getCastlingRights() {
		return castlingRights;
	}
}