package org.ajonx.games;

import org.ajonx.Moves;
import org.ajonx.Piece;
import org.ajonx.cpu.CPU;

import java.util.List;
import java.util.Map;

public class GameManager {
	public CPU whiteCPU;
	public CPU blackCPU;

	public GameState state;

	public GameManager(GameState state) {
		this.state = state;
	}

	public void setWhiteCPU(CPU cpu) {
		this.whiteCPU = cpu;
	}

	public void setBlackCPU(CPU cpu) {
		this.blackCPU = cpu;
	}

	public void startGame() {
		if (whiteCPU != null) whiteCPU.start();
		if (blackCPU != null) blackCPU.start();
	}

	public void makeMove(Moves.Move move) {
		int piece = state.board.get(move.sfile, move.srank);

		state.moveHandler.makeMove(move.sfile, move.srank, move.efile, move.erank, piece);
		state.chessApp.resetHeldPiece();
		state.chessApp.chessPanel.repaint();
	}

	public Map<Integer, List<Moves.Move>> getLegalMoves() {
		Map<Integer, List<Moves.Move>> legalMoves = Moves.generateLegalMoveMap();
		if (legalMoves.isEmpty()) {
			if (whiteCPU != null) whiteCPU.stop();
			if (blackCPU != null) blackCPU.stop();
			System.out.println(state.board.colorToMove == Piece.WHITE ? "Black wins!" : "White wins!");
			return null;
		} else return legalMoves;
	}
}