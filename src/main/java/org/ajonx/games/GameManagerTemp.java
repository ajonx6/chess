package org.ajonx.games;

import org.ajonx.Piece;
import org.ajonx.SoundPlayer;
import org.ajonx.games.cpu.CPU;
import org.ajonx.moves.Move;
import org.ajonx.moves.Moves;

import java.util.List;
import java.util.Map;

public class GameManagerTemp {
	public CPU whiteCPU;
	public CPU blackCPU;
	public GameInstances instances;
	public boolean isGameOver;

	private boolean headless = false;

	public GameManagerTemp() {}

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
		int movingPiece = instances.board.get(move.sfile, move.srank);
		int takenPiece = instances.board.get(move.efile, move.erank);
		boolean isCastle = instances.moveHandler.isCastle(move.sfile, move.efile, piece);

		if (!headless) {
			if (instances.board.colorToMove == Piece.WHITE) System.out.print(move + " ");
			else System.out.println(move);
		}

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
			else if (takenPiece != Piece.INVALID) SoundPlayer.take();
			else SoundPlayer.move();
		}
	}

	// public void updatePieceMap(Move move, int movingPiece, int takenPiece) {
	// 	int type = Piece.getType(movingPiece);
	// 	int colorIndex = Piece.isColor(movingPiece, Piece.WHITE) ? 0 : 1;
	//
	// 	int startIndex = instances.board.index(move.sfile, move.srank);
	// 	int endIndex = instances.board.index(move.efile, move.erank);
	//
	// 	instances.board.pieceMap.get(type).get(colorIndex).remove((Integer) startIndex);
	//
	// 	if (takenPiece != Piece.INVALID) {
	// 		int capturedType = Piece.getType(takenPiece);
	// 		int capturedColor = Piece.isColor(takenPiece, Piece.WHITE) ? 0 : 1;
	// 		instances.board.pieceMap.get(capturedType).get(capturedColor).remove((Integer) endIndex);
	// 	}
	//
	// 	instances.board.pieceMap.get(type).get(colorIndex).add(endIndex);
	// }

	public GameState evaluateGameState() {
		Map<Integer, List<Move>> legalMoves = Moves.generateLegalMoveMap();

		int kingIndex = instances.board.indexOfKing(instances.board.colorToMove);
		boolean inCheck = Moves.isKingInCheck(kingIndex);
		// System.out.println(legalMoves + ", " + inCheck);

		if (legalMoves.isEmpty()) {
			if (inCheck) return GameState.CHECKMATE;
			else return GameState.STALEMATE;
		}

		if (instances.board.isImmaterial()) return GameState.DRAW_IMMATERIAL;

		return GameState.ONGOING;
	}

	public Map<Integer, List<Move>> getLegalMoves() {
		return Moves.generateLegalMoveMap();
	}

	public void headless() {
		headless = true;
	}

	public boolean isHeadless() {
		return headless;
	}
}