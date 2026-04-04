package org.ajonx;

import org.ajonx.moves.Move;
import org.ajonx.pieces.Piece;

import java.util.ArrayList;
import java.util.List;

public class Board {
	public int width;
	public int height;
	public int[] board;

	public List<Integer> pieces = new ArrayList<>();
	public int colorToMove = Piece.WHITE;

	public Board(int width, int height) {
		this.width = width;
		this.height = height;
		this.board = new int[width * height];
	}

	public void loadDefaultGame() {
		loadGame("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
	}

	public void loadGame(String fen) {
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

		// Section 1 - turn
		if (sections[1].equals("w")) colorToMove = Piece.WHITE;
		else colorToMove = Piece.BLACK;

		// Section 2 - castling rights
		if (!sections[2].equals("-")) {}
	}

	public void makeMove(Move move) {
		int piece = get(move.getFrom());
		if (piece == Piece.INVALID) return;

		set(move.getTo(), piece);
		set(move.getFrom(), Piece.INVALID);
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
}