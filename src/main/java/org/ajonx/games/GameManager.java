package org.ajonx.games;

import org.ajonx.pieces.Piece;
import org.ajonx.SoundPlayer;
import org.ajonx.games.cpu.CPU;
import org.ajonx.moves.Move;
import org.ajonx.moves.Moves;

import java.util.*;

public class GameManager {
	public CPU whiteCPU;
	public CPU blackCPU;
	public GameInstances instances;
	public boolean isGameOver;

	private boolean headless = false;

	public GameManager() {}

	public void setInstances(GameInstances instances) {
		this.instances = instances;
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
	}

	public void endGame(GameState state) {
		isGameOver = true;
		if (headless) return;

		String opponentString = instances.board.colorToMove == Piece.WHITE ? "Black" : "White";

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

	public void runTurn(Move move, int piece) {
		boolean isTake = instances.board.get(move.efile, move.erank) != Piece.INVALID;
		boolean isCastle = instances.moveHandler.isCastle(move.sfile, move.efile, piece);

		// if (!headless) {
		// 	if (instances.board.colorToMove == Piece.WHITE) System.out.print(move + " ");
		// 	else System.out.println(move);
		// }

		instances.moveHandler.makeMove(move.sfile, move.srank, move.efile, move.erank, piece);

		if (!headless) {
			instances.window.resetHeldPiece();
			instances.window.chessPanel.repaint();
		}

		int kingIndex = instances.board.indexOfKing(instances.board.colorToMove);
		boolean inCheck = Moves.isKingInCheck(kingIndex);

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
		Map<Integer, List<Move>> legalMoves = Moves.generateLegalMoveMap();

		int kingIndex = instances.board.indexOfKing(instances.board.colorToMove);
		boolean inCheck = Moves.isKingInCheck(kingIndex);

		if (legalMoves.isEmpty()) {
			if (inCheck) return GameState.CHECKMATE;
			else return GameState.STALEMATE;
		}

		if (instances.board.isImmaterial()) return GameState.DRAW_IMMATERIAL;

		return GameState.ONGOING;
	}

	public Map<Integer, List<Move>> getLegalMoves() {
		Map<Integer, List<Move>> moves = new HashMap<>();
		Map<Integer, List<Move>> moveMap = Moves.generateLegalMoveMap();

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