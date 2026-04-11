package org.ajonx;

import org.ajonx.moves.Move;
import org.ajonx.moves.MoveGenerator;
import org.ajonx.pieces.Piece;

import java.lang.management.MonitorInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameManager {
	private Board board;
	private List<Runnable> listeners = new ArrayList<>();

	private int colorToMove = Piece.WHITE;
	private Map<Integer, List<Move>> allMovesThisTurn = new HashMap<>();
	private Move previousMove = new Move(-1, -1);

	public GameManager(Board board) {
		this.board = board;
	}

	public void initGame() {
		this.colorToMove = board.loadDefaultGame();
		startTurn();
	}

	public void startTurn() {
		allMovesThisTurn = MoveGenerator.generateMoves(this);
	}

	public void makeMove(Move move) {
		int piece = board.get(move.getFrom());
		int color = Piece.getColor(piece);
		int from = move.getFrom();
		int to = move.getTo();
		int captured = board.get(move.getTo());

		board.setEnpassantSquare(-1);

		if (Piece.isPieceType(piece, Piece.PAWN)) {
			if (Math.abs(to - from) == 2 * Constants.GRID_SIZE) {
				int epSquare = (from + to) / 2;
				board.setEnpassantSquare(epSquare);
			}
		}
		if (Piece.isPieceType(piece, Piece.KING)) {
			if (color == Piece.WHITE) {
				board.loseCastlingRight(Board.CASTLE_WHITE_QUEEN);
				board.loseCastlingRight(Board.CASTLE_WHITE_KING);

				if (from == Constants.E1 && to == Constants.G1) {
					board.set(Constants.F1, board.get(Constants.H1));
					board.set(Constants.H1, Piece.INVALID);
				}
				if (from == Constants.E1 && to == Constants.C1) {
					board.set(Constants.D1, board.get(Constants.A1));
					board.set(Constants.A1, Piece.INVALID);
				}
			}

			else {
				board.loseCastlingRight(Board.CASTLE_BLACK_QUEEN);
				board.loseCastlingRight(Board.CASTLE_BLACK_KING);

				if (from == Constants.E8 && to == Constants.G8) {
					board.set(Constants.F8, board.get(Constants.H8));
					board.set(Constants.H8, Piece.INVALID);
				}
				if (from == Constants.E8 && to == Constants.C8) {
					board.set(Constants.D8, board.get(Constants.A8));
					board.set(Constants.A8, Piece.INVALID);
				}
			}
		}
		if (Piece.isPieceType(piece, Piece.ROOK)) {
			if (from == Constants.A1) board.loseCastlingRight(Board.CASTLE_WHITE_QUEEN);
			if (from == Constants.H1) board.loseCastlingRight(Board.CASTLE_WHITE_KING);
			if (from == Constants.A8) board.loseCastlingRight(Board.CASTLE_BLACK_QUEEN);
			if (from == Constants.H8) board.loseCastlingRight(Board.CASTLE_BLACK_KING);
		}

		if (Piece.isPieceType(captured, Piece.ROOK)) {
			if (to == Constants.A1) board.loseCastlingRight(Board.CASTLE_WHITE_QUEEN);
			if (to == Constants.H1) board.loseCastlingRight(Board.CASTLE_WHITE_KING);
			if (to == Constants.A8) board.loseCastlingRight(Board.CASTLE_BLACK_QUEEN);
			if (to == Constants.H8) board.loseCastlingRight(Board.CASTLE_BLACK_KING);
		}

		if (move.isEnpassant()) {
			int direction = (color == Piece.WHITE) ? -Constants.GRID_SIZE : Constants.GRID_SIZE;
			int capturedSquare = move.getTo() + direction;
			board.set(capturedSquare, Piece.INVALID);
		}

		board.makeMove(move);
		previousMove = move;
		this.colorToMove = Piece.getOppositeColor(colorToMove);
		if (move.getPromotionPiece() != Piece.INVALID) board.promote(move);

		startTurn();
		listeners.forEach(Runnable::run);
	}


	public void addListener(Runnable listener) {
		listeners.add(listener);
	}

	public Board getBoard() {
		return board;
	}

	public int getColorToMove() {
		return colorToMove;
	}

	public Map<Integer, List<Move>> getAllMovesThisTurn() {
		return allMovesThisTurn;
	}

	public Move getPreviousMove() {
		return previousMove;
	}
}
