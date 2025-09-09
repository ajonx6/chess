package org.ajonx.games;

import org.ajonx.Board;
import org.ajonx.Window;
import org.ajonx.moves.MoveHandler;
import org.ajonx.pieces.Piece;
import org.ajonx.SoundPlayer;
import org.ajonx.games.cpu.CPU;
import org.ajonx.moves.Move;
import org.ajonx.moves.MoveGenerator;

import java.util.*;

public class GameManager {
	public static final int GRID_SIZE = 8;
	public static final int CELL_SIZE = 75;

	public Window window;
	public Board board;
	public MoveGenerator moveGenerator;
	public MoveHandler moveHandler;
	public CPU whiteCPU;
	public CPU blackCPU;
	public boolean isGameOver;

	private boolean headless = false;

	public GameManager(Window window, String startFen) {
		this.window = window;
		this.board = new Board(GRID_SIZE, GRID_SIZE);
		this.moveGenerator = new MoveGenerator(board);
		this.moveHandler = new MoveHandler(board, moveGenerator);

		board.loadGame(startFen);
	}

	public GameManager(String startFen) {
		this(null, startFen);
	}

	public GameManager(Window window) {
		this(window, "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
	}

	public GameManager() {
		this(null, "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
	}

	public void setWhiteCPU(CPU cpu) {
		this.whiteCPU = cpu;
	}

	public void setBlackCPU(CPU cpu) {
		this.blackCPU = cpu;
	}

	public void startGame() {
		if (!headless) {
			if (whiteCPU != null) whiteCPU.start();
			if (blackCPU != null) blackCPU.start();
		}
		moveGenerator.precomputeData();
	}

	public void endGame(GameState state) {
		isGameOver = true;
		if (headless) return;

		String opponentString = board.colorToMove == Piece.WHITE ? "Black" : "White";

		if (state == GameState.CHECKMATE) {
			System.out.println(opponentString + " wins!");
		} else if (state == GameState.STALEMATE) {
			System.out.println("Draw by stalemate");
		} else if (state == GameState.DRAW_IMMATERIAL) {
			System.out.println("Draw by insufficient material");
		}

		if (whiteCPU != null) whiteCPU.stop();
		if (blackCPU != null) blackCPU.stop();
	}

	public void runTurn(Move move) {
		int startPiece = board.get(move.startFile, move.startRank);
		int endPiece = board.get(move.endFile, move.endRank);
		boolean isTake = endPiece != Piece.INVALID;
		boolean isCastle = moveHandler.isCastle(move, startPiece);

		if (!headless) {
			if (board.colorToMove == Piece.WHITE) System.out.print(move + " ");
			else System.out.println(move);
		}

		moveHandler.makeMove(move);

		if (!headless) {
			window.resetHeldPiece();
			window.repaint();
		}

		moveGenerator.precomputeData();
		boolean inCheck = moveGenerator.isKingInCheck(board.colorToMove);

		GameState state = evaluateGameState();
		if (state != GameState.ONGOING) {
			endGame(state);
			if (!headless) SoundPlayer.gameEnd();
		} else if (!headless) {
			if (inCheck) SoundPlayer.check();
			else if (isCastle) SoundPlayer.castle();
			else if (isTake) SoundPlayer.take();
			else SoundPlayer.move();
		}
	}

	public GameState evaluateGameState() {
		Map<Integer, List<Move>> legalMoves = moveGenerator.generateLegalMoveMap();
		boolean inCheck = moveGenerator.isKingInCheck(board.colorToMove);

		if (legalMoves.isEmpty()) {
			if (inCheck) return GameState.CHECKMATE;
			else return GameState.STALEMATE;
		}

		if (board.isImmaterial()) return GameState.DRAW_IMMATERIAL;

		return GameState.ONGOING;
	}

	public Map<Integer, List<Move>> getLegalMoves() {
		Map<Integer, List<Move>> moves = new HashMap<>();
		Map<Integer, List<Move>> moveMap = moveGenerator.generateLegalMoveMap();

		for (int square : moveMap.keySet()) {
			Set<Move> moveRemDup = new HashSet<>(moveMap.get(square));
			moves.put(square, new ArrayList<>(moveRemDup));
		}

		return moves;
	}

	public void headless() {
		headless = true;
	}

	public boolean isHeadless() {
		return headless;
	}
}