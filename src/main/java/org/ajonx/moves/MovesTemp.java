package org.ajonx.moves;

import org.ajonx.Board;
import org.ajonx.Window;
import org.ajonx.Piece;

import java.util.*;
import java.util.stream.Collectors;

public class MovesTemp {
	public static int[] directionOffsets = new int[] { 8, -8, -1, 1, 7, -7, 9, -9 };
	public static int[] knightOffsets = new int[] { 6, -6, 10, -10, 15, -15, 17, -17 };
	public static int[][] numSquaresToEdge = new int[Window.GRID_SIZE * Window.GRID_SIZE][8];

	private static Board board;
	private static MoveHandler moveHandler;

	public static void init(Board board, MoveHandler moveHandler) {
		MovesTemp.board = board;
		MovesTemp.moveHandler = moveHandler;
		createDistanceArray();
	}

	public static void createDistanceArray() {
		for (int rank = 0; rank < 8; rank++) {
			for (int file = 0; file < 8; file++) {
				int numUp = Window.GRID_SIZE - 1 - rank;
				int numDown = rank;
				int numLeft = file;
				int numRight = Window.GRID_SIZE - 1 - file;

				int index = board.index(file, rank);

				numSquaresToEdge[index] = new int[] { numUp, numDown, numLeft, numRight, Math.min(numUp, numLeft), Math.min(numDown, numRight), Math.min(numUp, numRight), Math.min(numDown, numLeft) };
			}
		}
	}

	public static Map<Integer, List<Move>> generateLegalMoveMap() {
		Set<Move> movesRemDups = new HashSet<>(generateLegalMoves());
		List<Move> moves = new ArrayList<>(movesRemDups);
		return moves.stream().collect(Collectors.groupingBy(m -> board.index(m.sfile, m.srank)));
	}

	// public static List<Move> generateLegalMoves() {
	// 	List<Move> pseudoLegalMoves = generateMoves();
	// 	List<Move> legalMoves = new ArrayList<>();
	// 	int turn = board.colorToMove;
	//
	// 	for (Move move : pseudoLegalMoves) {
	// 		MoveState prevState = moveHandler.makeTemporaryMove(move);
	// 		int indexOfKing = board.indexOfKing(turn);
	// 		List<Move> responses = generateMoves();
	// 		if (responses.stream().noneMatch(m -> board.index(m.efile, m.erank) == indexOfKing)) {
	// 			legalMoves.add(move);
	// 		}
	// 		moveHandler.undoMove(prevState);
	// 	}
	//
	// 	return legalMoves;
	// }

	public static List<Move> generateLegalMoves() {
		List<Move> pseudoLegalMoves = generateMoves(); // all moves ignoring check
		List<Move> legalMoves = new ArrayList<>();

		for (Move move : pseudoLegalMoves) {
			if (!wouldLeaveKingInCheck(move)) {
				legalMoves.add(move);
			}
		}

		return legalMoves;
	}

	public static boolean wouldLeaveKingInCheck(Move move) {
		int turn = Piece.isColor(board.get(move.sfile, move.srank), Piece.WHITE) ? Piece.WHITE : Piece.BLACK;

		int kingSquare = board.indexOfKing(turn);
		int startSquare = board.index(move.sfile, move.srank);
		int endSquare = board.index(move.efile, move.erank);

		// If the move is moving the king, the new king position is endSquare
		if (board.get(startSquare) == (Piece.KING | turn)) {
			kingSquare = endSquare;
		}

		// Check if the kingSquare is attacked after this move
		return isSquareAttacked(kingSquare, Piece.inverse(turn));
	}

	public static boolean isSquareAttacked(int squareIndex, int attackerColor) {
		for (int pieceType : board.pieceMap.keySet()) {
			List<List<Integer>> squares = board.pieceMap.get(pieceType);
			int color = Piece.indexFromColor(attackerColor);
			List<Integer> pieceSquares = squares.get(color);

			for (int square : pieceSquares) {
				List<Integer> attackedSquares = generateAttacks(square, pieceType | attackerColor);
				if (attackedSquares.contains(squareIndex)) return true;
			}
		}

		return false;
	}

	public static boolean isKingInCheck() {
		List<Move> opponentMoves = generateOpponentMoves();
		int kindex = board.pieceMap.get(Piece.KING).get(Piece.indexFromColor(board.colorToMove)).get(0);
		int kfile = kindex % 8;
		int krank = kindex / 8;

		for (Move move : opponentMoves) {
			if (move.efile == kfile && move.erank == krank) return true;
		}

		return false;
	}

	public static List<Move> generateMoves() {
		List<Move> moves = new ArrayList<>();

		for (int pieceType : board.pieceMap.keySet()) {
			List<List<Integer>> indiciesColors = board.pieceMap.get(pieceType);
			int color = Piece.indexFromColor(board.colorToMove);
			List<Integer> indicies = indiciesColors.get(color);
			if (indicies.isEmpty()) continue;

			int piece = pieceType | (color == 0 ? Piece.WHITE : Piece.BLACK);
			for (int index : indicies) {
				int file = index % board.width;
				int rank = index / board.width;
				moves.addAll(generateMoves(file, rank, piece));
			}
		}

		return moves;
	}

	public static List<Move> generateOpponentMoves() {
		List<Move> moves = new ArrayList<>();

		for (int pieceType : board.pieceMap.keySet()) {
			List<List<Integer>> indiciesColors = board.pieceMap.get(pieceType);
			int color = Piece.indexFromColor(Piece.inverse(board.colorToMove));
			List<Integer> indicies = indiciesColors.get(color);
			if (indicies.isEmpty()) continue;
			int piece = pieceType | (color == 0 ? Piece.WHITE : Piece.BLACK);
			for (int index : indicies) {
				int file = index % board.width;
				int rank = index / board.width;
				moves.addAll(generateMoves(file, rank, piece));

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

	public static List<Integer> generateAttacks(int square, int piece) {
		if (Piece.isSlider(piece))
			return generateSliderMoves(square, piece).stream().map(m -> board.index(m.efile, m.erank)).toList();
		else if (Piece.isType(piece, Piece.KING))
			return generateKingMoves(square, piece).stream().map(m -> board.index(m.efile, m.erank)).toList();
		else if (Piece.isType(piece, Piece.KNIGHT))
			return generateKnightMoves(square, piece).stream().map(m -> board.index(m.efile, m.erank)).toList();
		else if (Piece.isType(piece, Piece.PAWN))
			return generatePawnAttacks(square, piece).stream().map(m -> board.index(m.efile, m.erank)).toList();
		else return new ArrayList<>();
	}

	public static List<Move> generateSliderMoves(int startSquare, int movingPiece) {
		List<Move> moves = new ArrayList<>();
		int startDirIndex = Piece.isType(movingPiece, Piece.BISHOP) ? 4 : 0;
		int endDirIndex = Piece.isType(movingPiece, Piece.ROOK) ? 4 : 8;

		for (int dirIndex = startDirIndex; dirIndex < endDirIndex; dirIndex++) {
			for (int n = 0; n < numSquaresToEdge[startSquare][dirIndex]; n++) {
				int targetSquare = startSquare + directionOffsets[dirIndex] * (n + 1);
				int pieceOnTarget = board.get(targetSquare);

				if (Piece.matchColor(pieceOnTarget, movingPiece)) break;
				moves.add(new Move(startSquare, targetSquare));
				if (Piece.matchOppositeColor(pieceOnTarget, movingPiece)) break;
			}
		}

		return moves;
	}

	public static List<Move> generateKingMoves(int startSquare, int movingPiece) {
		List<Move> moves = new ArrayList<>();
		int file = startSquare % 8;

		for (int dirIndex = 0; dirIndex < 8; dirIndex++) {
			int targetSquare = startSquare + directionOffsets[dirIndex];
			if (targetSquare < 0 || targetSquare >= 64) continue;
			int targetFile = targetSquare % 8;
			if (Math.abs(targetFile - file) > 1) continue;

			int pieceOnTarget = board.get(targetSquare);

			if (!Piece.matchColor(pieceOnTarget, movingPiece)) {
				moves.add(new Move(startSquare, targetSquare));
			}
		}

		if (Piece.isColor(movingPiece, Piece.WHITE)) {
			int rank = 0;

			if (!board.whiteKingMove && !board.whiteRKMove) {
				if (board.get(5, rank) == Piece.INVALID &&
					board.get(6, rank) == Piece.INVALID) {
					moves.add(new Move(startSquare, file + 2 + rank * 8));
				}
			}

			if (!board.whiteKingMove && !board.whiteRQMove) {
				if (board.get(1, rank) == Piece.INVALID &&
					board.get(2, rank) == Piece.INVALID &&
					board.get(3, rank) == Piece.INVALID) {
					moves.add(new Move(startSquare, file - 2 + rank * 8));
				}
			}
		} else {
			int rank = 7;

			if (!board.blackKingMove && !board.blackRKMove) {
				if (board.get(5, rank) == Piece.INVALID &&
					board.get(6, rank) == Piece.INVALID) {
					moves.add(new Move(startSquare, file + 2 + rank * 8));
				}
			}

			if (!board.blackKingMove && !board.blackRQMove) {
				if (board.get(1, rank) == Piece.INVALID &&
					board.get(2, rank) == Piece.INVALID &&
					board.get(3, rank) == Piece.INVALID) {
					moves.add(new Move(startSquare, file - 2 + rank * 8));
				}
			}
		}

		return moves;
	}

	public static List<Move> generateKnightMoves(int startSquare, int movingPiece) {
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

			if (!Piece.matchColor(pieceOnTarget, movingPiece)) {
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

		if (nextRank >= Window.GRID_SIZE || nextRank < 0) return moves;

		if (board.get(file, nextRank) == Piece.INVALID) {
			moves.add(new Move(file, rank, file, nextRank));
			if (rank == baseRank && board.get(file, rank + direction * 2) == Piece.INVALID) {
				moves.add(new Move(file, rank, file, rank + direction * 2));
			}
		}

		int diag1 = file - 1;
		int diag2 = file + 1;
		int targetRank = rank + direction;

		if (diag1 >= 0 && Piece.matchOppositeColor(board.get(diag1, targetRank), piece)) {
			moves.add(new Move(file, rank, diag1, targetRank));
		}
		if (diag2 < Window.GRID_SIZE && Piece.matchOppositeColor(board.get(diag2, targetRank), piece)) {
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

	public static List<Move> generatePawnAttacks(int startSquare, int piece) {
		List<Move> moves = new ArrayList<>();
		int file = startSquare % 8;
		int rank = startSquare / 8;

		int direction = Piece.isColor(piece, Piece.WHITE) ? 1 : -1;
		int targetRank = rank + direction;

		if (targetRank < 0 || targetRank >= Window.GRID_SIZE) return moves;

		int diagLeft = file - 1;
		int diagRight = file + 1;

		if (diagLeft >= 0) {
			moves.add(new Move(file, rank, diagLeft, targetRank)); // attacks left
		}
		if (diagRight < Window.GRID_SIZE) {
			moves.add(new Move(file, rank, diagRight, targetRank)); // attacks right
		}

		return moves;
	}

}