package org.ajonx;

import java.util.ArrayList;
import java.util.List;

public class Board {
	public int width;
	public int height;
	public int[] board;
	public int colorToMove = Piece.WHITE;

	public int enPassantTarget = -1;

	public Board(int width, int height) {
		this.width = width;
		this.height = height;
		this.board = new int[width * height];

		loadFromFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
		//		loadFromFEN("rqbkn3/8/8/8/8/8/8/3NKBQR w KQkq - 0 1");
	}

	public void loadFromFEN(String fen) {
		String[] sections = fen.split(" ");

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
					set(file++, height - 1 - rank, pieceColor | pieceType);
				}
			}
		}
	}

	public void printBoard() {
		for (int rank = 0; rank < height; rank++) {
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
}