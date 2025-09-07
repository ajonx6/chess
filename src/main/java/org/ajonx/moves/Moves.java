package org.ajonx.moves;

import org.ajonx.Board;
import org.ajonx.Window;
import org.ajonx.pieces.Piece;
import org.ajonx.pieces.behaviours.KingBehaviour;
import org.ajonx.pieces.behaviours.KnightBehaviour;
import org.ajonx.pieces.behaviours.PawnBehaviour;
import org.ajonx.pieces.behaviours.SlidingBehaviour;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Moves {
	public static int[][] numSquaresToEdge = new int[Window.GRID_SIZE * Window.GRID_SIZE][8];

	private static Board board;
	private static MoveHandler moveHandler;

	public static void init(Board board, MoveHandler moveHandler) {
		Moves.board = board;
		Moves.moveHandler = moveHandler;
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
		return generateLegalMoves().stream().collect(Collectors.groupingBy(m -> board.index(m.sfile, m.srank)));
	}

	public static List<Move> generateLegalMoves() {
		List<Move> pseudoLegalMoves = generateMoves();
		List<Move> legalMoves = new ArrayList<>();
		int movingColor = board.colorToMove;

		for (Move move : pseudoLegalMoves) {
			MoveState prevState = moveHandler.makeTemporaryMove(move);
			int indexOfKing = board.indexOfKing(movingColor);
			List<Move> responses = generateMoves();
			if (responses.stream().noneMatch(m -> board.index(m.efile, m.erank) == indexOfKing)) {
				legalMoves.add(move);
			}
			moveHandler.undoMove(prevState);
		}

		return legalMoves;
	}

	public static List<Move> generateMoves() {
		List<Move> moves = new ArrayList<>();

		for (int rank = 0; rank < 8; rank++) {
			for (int file = 0; file < 8; file++) {
				int piece = board.get(file, rank);
				if (Piece.isColor(piece, board.colorToMove)) {
					moves.addAll(generateMovesAt(file, rank, piece));
				}
			}
		}

		return moves;
	}

	public static List<Move> generateOpponentMoves() {
		List<Move> moves = new ArrayList<>();

		for (int rank = 0; rank < 8; rank++) {
			for (int file = 0; file < 8; file++) {
				int piece = board.get(file, rank);
				if (Piece.isOppositeColor(piece, board.colorToMove)) {
					moves.addAll(generateMovesAt(file, rank, piece));
				}
			}
		}

		return moves;
	}

	public static List<Move> generateMovesAt(int file, int rank, int piece) {
		if (Piece.isSlider(piece)) return SlidingBehaviour.getMovement(board, file, rank, piece, numSquaresToEdge);
		else if (Piece.isType(piece, Piece.KING)) return KingBehaviour.getMovement(board, file, rank, piece);
		else if (Piece.isType(piece, Piece.KNIGHT)) return KnightBehaviour.getMovement(board, file, rank, piece);
		else if (Piece.isType(piece, Piece.PAWN)) return PawnBehaviour.getMovement(board, file, rank, piece);
		else return new ArrayList<>();
	}

	public static List<Move> generateAttacks() {
		List<Move> moves = new ArrayList<>();

		for (int rank = 0; rank < 8; rank++) {
			for (int file = 0; file < 8; file++) {
				int piece = board.get(file, rank);
				if (Piece.isOppositeColor(piece, board.colorToMove)) {
					moves.addAll(generateAttacksAt(file, rank, piece));
				}
			}
		}

		return moves;
	}

	public static List<Move> generateAttacksAt(int file, int rank, int piece) {
		if (Piece.isSlider(piece)) return SlidingBehaviour.getAttacks(board, file, rank, piece, numSquaresToEdge);
		else if (Piece.isType(piece, Piece.KING)) return KingBehaviour.getAttacks(board, file, rank, piece);
		else if (Piece.isType(piece, Piece.KNIGHT)) return KnightBehaviour.getAttacks(board, file, rank, piece);
		else if (Piece.isType(piece, Piece.PAWN)) return PawnBehaviour.getAttacks(board, file, rank, piece);
		else return new ArrayList<>();
	}

	public static boolean isKingInCheck(int kingIndex) {
		List<Move> attackedSquares = generateAttacks();
		List<Integer> squaresAttacked = attackedSquares.stream().map(m -> board.index(m.efile, m.erank)).toList();

		for (int attackedSquare : squaresAttacked) {
			if (attackedSquare == kingIndex) return true;
		}

		return false;
	}
}