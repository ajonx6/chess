package org.ajonx;

import java.util.ArrayList;
import java.util.List;

public class Moves {
	public static int[] directionOffsets = new int[]{ 8, -8, -1, 1, 7, -7, 9, -9 };
	public static int[] knightOffsets = new int[]{ 6, -6, 10, -10, 15, -15, 17, -17 };
	public static int[][] numSquaresToEdge = new int[ChessApplication.GRID_SIZE * ChessApplication.GRID_SIZE][8];

	private static Board board;

	public static void init(Board board) {
		Moves.board = board;
		createDistanceArray();
	}

	public static void createDistanceArray() {
		for (int rank = 0; rank < 8; rank++) {
			for (int file = 0; file < 8; file++) {
				int numUp = ChessApplication.GRID_SIZE - 1 - rank;
				int numDown = rank;
				int numLeft = file;
				int numRight = ChessApplication.GRID_SIZE - 1 - file;

				int index = board.index(file, rank);

				numSquaresToEdge[index] = new int[]{ numUp, numDown, numLeft, numRight, Math.min(numUp, numLeft), Math.min(numDown, numRight), Math.min(numUp, numRight), Math.min(numDown, numLeft) };
			}
		}
	}

	public static List<Move> generateMoves() {
		List<Move> moves = new ArrayList<>();

		for (int rank = 0; rank < 8; rank++) {
			for (int file = 0; file < 8; file++) {
				int piece = board.get(file, rank);
				if (Piece.isColor(piece, board.colorToMove)) {
					if (Piece.isSlider(piece)) {
						moves.addAll(generateSliderMoves(board.index(file, rank), piece));
					} else if (Piece.isType(piece, Piece.KING)) {
						moves.addAll(generateKingMoves(board.index(file, rank), piece));
					}
				}
			}
		}

		return moves;
	}

	public static List<Move> generateMoves(int file, int rank, int piece) {
		if (Piece.isSlider(piece)) return generateSliderMoves(board.index(file, rank), piece);
		else if (Piece.isType(piece, Piece.KING)) return generateKingMoves(board.index(file, rank), piece);
		else if (Piece.isType(piece, Piece.KNIGHT)) return generateKnightMoves(board.index(file, rank), piece);
		else if (Piece.isType(piece, Piece.PAWN)) return generatePawnMoves(board.index(file, rank), piece);
		else return new ArrayList<>();
	}

	public static List<Move> generateSliderMoves(int startSquare, int piece) {
		List<Move> moves = new ArrayList<>();
		int startDirIndex = Piece.isType(piece, Piece.BISHOP) ? 4 : 0;
		int endDirIndex = Piece.isType(piece, Piece.ROOK) ? 4 : 8;

		for (int dirIndex = startDirIndex; dirIndex < endDirIndex; dirIndex++) {
			for (int n = 0; n < numSquaresToEdge[startSquare][dirIndex]; n++) {
				int targetSquare = startSquare + directionOffsets[dirIndex] * (n + 1);
				int pieceOnTarget = board.get(targetSquare);

				if (Piece.isColor(pieceOnTarget, Piece.getColor(piece))) break;
				moves.add(new Move(startSquare, targetSquare));
				if (Piece.isOppositeColor(pieceOnTarget, Piece.getColor(piece))) break;
			}
		}

		return moves;
	}

	public static List<Move> generateKingMoves(int startSquare, int piece) {
		List<Move> moves = new ArrayList<>();
		int file = startSquare % 8;

		for (int dirIndex = 0; dirIndex < 8; dirIndex++) {
			int targetSquare = startSquare + directionOffsets[dirIndex];
			if (targetSquare < 0 || targetSquare >= 64) continue;
			int targetFile = targetSquare % 8;
			if (Math.abs(targetFile - file) > 1) continue;

			int pieceOnTarget = board.get(targetSquare);

			if (!Piece.isColor(pieceOnTarget, Piece.getColor(piece))) {
				moves.add(new Move(startSquare, targetSquare));
			}
		}

		return moves;
	}

	public static List<Move> generateKnightMoves(int startSquare, int piece) {
		List<Move> moves = new ArrayList<>();
		int file = startSquare % 8;
		int rank = startSquare / 8;

		for (int dirIndex = 0; dirIndex < 8; dirIndex++) {
			int targetSquare = startSquare + knightOffsets[dirIndex];
			if (targetSquare < 0 || targetSquare >= 64) continue;
			int targetFile = targetSquare % 8;
			int targetRank = targetSquare / 8;
			int df = Math.abs(targetFile - file);
			int dr = Math.abs(targetRank - rank);
			if (!((df == 2 && dr == 1) || (df == 1 && dr == 2))) continue;

			int pieceOnTarget = board.get(targetSquare);

			if (!Piece.isColor(pieceOnTarget, Piece.getColor(piece))) {
				moves.add(new Move(startSquare, targetSquare));
			}
		}

		return moves;
	}

	public static List<Move> generatePawnMoves(int startSquare, int piece) {
		List<Move> moves = new ArrayList<>();
		int file = startSquare % 8;
		int rank = startSquare / 8;

		int baseRank = Piece.isColor(piece, Piece.WHITE) ? 1 : 6;
		int direction = Piece.isColor(piece, Piece.WHITE) ? 1 : -1;
		int nextRank = rank + direction;

		if (nextRank >= ChessApplication.GRID_SIZE || nextRank < 0) return moves;

		if (board.get(file, nextRank) == Piece.INVALID) {
			moves.add(new Move(file, rank, file, nextRank));
			if (rank == baseRank && board.get(file, rank + direction * 2) == Piece.INVALID) {
				moves.add(new Move(file, rank, file, rank + direction * 2));
			}
		}

		int diag1 = file - 1;
		int diag2 = file + 1;
		int targetRank = rank + direction;

		if (diag1 >= 0 && Piece.isOppositeColor(board.get(diag1, targetRank), Piece.getColor(piece))) {
			moves.add(new Move(file, rank, diag1, targetRank));
		}
		if (diag2 < ChessApplication.GRID_SIZE && Piece.isOppositeColor(board.get(diag2, targetRank), Piece.getColor(piece))) {
			moves.add(new Move(file, rank, diag2, targetRank));
		}

		if (diag1 >= 0 && board.index(diag1, targetRank) == board.enPassantTarget) {
			moves.add(new Move(file, rank, diag1, targetRank));
		}
		if (diag2 >= 0 && board.index(diag2, targetRank) == board.enPassantTarget) {
			moves.add(new Move(file, rank, diag2, targetRank));
		}

		return moves;
	}


	public static class Move {
		public int sfile, srank;
		public int efile, erank;

		public Move(int sfile, int srank, int efile, int erank) {
			this.sfile = sfile;
			this.srank = srank;
			this.efile = efile;
			this.erank = erank;
		}

		public Move(int sindex, int eindex) {
			this.sfile = sindex % 8;
			this.srank = sindex / 8;
			this.efile = eindex % 8;
			this.erank = eindex / 8;
		}
	}
}