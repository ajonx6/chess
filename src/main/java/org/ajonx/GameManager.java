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
