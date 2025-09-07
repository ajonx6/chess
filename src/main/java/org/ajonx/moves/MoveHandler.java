package org.ajonx.moves;

import org.ajonx.Board;
import org.ajonx.Window;
import org.ajonx.pieces.Piece;

import java.util.ArrayList;
import java.util.List;

public class MoveHandler {
	public List<Move> history = new ArrayList<>();
	
	private Board board;

	public MoveHandler(Board board) {
		this.board = board;
	}

	public MoveState makeTemporaryMove(Move move) {
		int startPiece = board.get(move.sfile, move.srank);
		int capturedPiece = board.get(move.efile, move.erank);
		int enPassantBefore = board.enPassantTarget;
		boolean wk = board.whiteKingMove, wrq = board.whiteRQMove, wrk = board.whiteRKMove;
		boolean bk = board.blackKingMove, brq = board.blackRQMove, brk = board.blackRKMove;
		int colorToMoveBefore = board.colorToMove;

		history.add(move);
		
		if (isCastle(move.sfile, move.efile, startPiece)) {
			boolean left = move.efile < move.sfile;
			int rookStartFile = left ? 0 : Window.GRID_SIZE - 1;
			int rookEndFile = move.efile + (left ? 1 : -1);
			int rookRank = move.erank;

			int rookStartPieceBefore = board.get(rookStartFile, rookRank);
			int rookEndPieceBefore = board.get(rookEndFile, rookRank);

			makeMove(move.sfile, move.srank, move.efile, move.erank, startPiece);

			return new MoveState(move, startPiece, capturedPiece, enPassantBefore, wk, wrq, wrk, bk, brq, brk, colorToMoveBefore, rookStartFile, rookRank, rookEndFile, rookRank, rookStartPieceBefore, rookEndPieceBefore);
		} else {
			makeMove(move.sfile, move.srank, move.efile, move.erank, startPiece);
			return new MoveState(move, startPiece, capturedPiece, enPassantBefore, wk, wrq, wrk, bk, brq, brk, colorToMoveBefore);
		}
	}

	public void undoMove(MoveState state) {
		board.set(state.move.sfile, state.move.srank, state.startPiece);
		board.set(state.move.efile, state.move.erank, state.capturedPiece);

		board.enPassantTarget = state.enPassantBefore;
		board.whiteKingMove = state.wk;
		board.whiteRQMove = state.wrq;
		board.whiteRKMove = state.wrk;
		board.blackKingMove = state.bk;
		board.blackRQMove = state.brq;
		board.blackRKMove = state.brk;
		board.colorToMove = state.colorToMoveBefore;

		if (state.wasCastle) {
			board.set(state.rookStartFile, state.rookStartRank, state.rookStartPieceBefore);
			board.set(state.rookEndFile, state.rookEndRank, state.rookEndPieceBefore);
		}

		history.remove(history.size() - 1);
	}


	public void makeMove(int startFile, int startRank, int endFile, int endRank, int heldPiece) {
		board.set(board.index(startFile, startRank), Piece.INVALID);

		if (isPromotion(endRank, heldPiece)) {
			board.set(endFile, endRank, Piece.getColor(heldPiece) | Piece.QUEEN);
		} else {
			handleTakingRooks(endFile, endRank);
			board.set(endFile, endRank, heldPiece);
			handlePieceFlags(startFile, startRank, heldPiece);

			if (isEnPassant(endFile, endRank, heldPiece)) {
				int capturedRank = (Piece.isColor(heldPiece, Piece.WHITE)) ? endRank - 1 : endRank + 1;
				board.set(endFile, capturedRank, Piece.INVALID);
			} else if (isCastle(startFile, endFile, heldPiece)) {
				boolean left = endFile < startFile;
				board.set(endFile + (left ? 1 : -1), endRank, board.get(left ? 0 : Window.GRID_SIZE - 1, endRank));
				board.set(left ? 0 : Window.GRID_SIZE - 1, endRank, Piece.INVALID);
				handleCastlePieceFlags(heldPiece);
			}
		}

		updateEnPassantTarget(startFile, startRank, endRank, heldPiece);
		board.colorToMove = Piece.inverse(board.colorToMove);
	}

	public boolean isCastle(int startFile, int endFile, int piece) {
		return Piece.isType(piece, Piece.KING) && Math.abs(startFile - endFile) == 2;
	}

	public boolean isPromotion(int rank, int piece) {
		return Piece.isType(piece, Piece.PAWN) && ((Piece.isColor(piece, Piece.WHITE) && rank == 7) || (Piece.isColor(piece, Piece.BLACK) && rank == 0));
	}

	public boolean isEnPassant(int file, int rank, int piece) {
		return Piece.isType(piece, Piece.PAWN) && board.index(file, rank) == board.enPassantTarget;
	}

	public void updateEnPassantTarget(int file, int startRank, int endRank, int piece) {
		if (Piece.isType(piece, Piece.PAWN) &&
			Math.abs(startRank - endRank) == 2) {
			board.enPassantTarget = file + ((startRank + endRank) / 2) * Window.GRID_SIZE;
		} else {
			board.enPassantTarget = -1;
		}
	}

	public void handleTakingRooks(int file, int rank) {
		int piece = board.get(file, rank);
		if (!Piece.isType(piece, Piece.ROOK)) return;
		if (Piece.isColor(piece, Piece.WHITE)) {
			if (file == 0 && rank == 0) board.whiteRQMove = true;
			else if (file == Window.GRID_SIZE - 1 && rank == 0) board.whiteRKMove = true;
		} else {
			if (file == 0 && rank == Window.GRID_SIZE - 1) board.blackRQMove = true;
			else if (file == Window.GRID_SIZE - 1 && rank == Window.GRID_SIZE - 1) board.blackRKMove = true;
		}
	}

	public void handlePieceFlags(int startFile, int startRank, int piece) {
		if (Piece.isColor(piece, Piece.WHITE)) {
			if (Piece.isType(piece, Piece.KING)) board.whiteKingMove = true;
			else if (Piece.isType(piece, Piece.ROOK)) {
				if (startFile == 0 && startRank == 0) board.whiteRQMove = true;
				else if (startFile == Window.GRID_SIZE - 1 && startRank == 0) board.whiteRKMove = true;
			}
		} else {
			if (Piece.isType(piece, Piece.KING)) board.blackKingMove = true;
			else if (Piece.isType(piece, Piece.ROOK)) {
				if (startFile == 0 && startRank == Window.GRID_SIZE - 1) board.blackRQMove = true;
				else if (startFile == Window.GRID_SIZE - 1 && startRank == Window.GRID_SIZE - 1)
					board.blackRKMove = true;
			}
		}
	}

	public void handleCastlePieceFlags(int piece) {
		if (Piece.isColor(piece, Piece.WHITE)) {
			board.whiteKingMove = true;
			board.whiteRKMove = true;
			board.whiteRQMove = true;
		} else {
			board.blackKingMove = true;
			board.blackRKMove = true;
			board.blackRQMove = true;
		}
	}
}