package org.ajonx.moves;

import org.ajonx.Board;
import org.ajonx.Window;
import org.ajonx.games.GameManager;
import org.ajonx.pieces.Piece;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class MoveHandler {
	// Flags for special moves
	public static final int FLAG_CASTLE = 0b001;
	public static final int FLAG_ENPASSANT = 0b010;
	public static final int FLAG_PROMOTION = 0b100;

	// Castling rights bitmask
	public static final int CASTLE_WHITE_KING = 0b0001;
	public static final int CASTLE_WHITE_QUEEN = 0b0010;
	public static final int CASTLE_BLACK_KING = 0b0100;
	public static final int CASTLE_BLACK_QUEEN = 0b1000;

	public Stack<Move> history = new Stack<>(); // optional, for debugging
	public Stack<MoveState> stateStack = new Stack<>();

	private Board board;
	private MoveGenerator moveGenerator;

	public MoveHandler(Board board, MoveGenerator moveGenerator) {
		this.board = board;
		this.moveGenerator = moveGenerator;
	}

	// --- Core make/undo ---
	public MoveState makeMove(Move move) {
		int startPiece = board.get(move.startFile, move.startRank);
		int capturedPiece = board.get(move.endFile, move.endRank);

		MoveState state = new MoveState();
		state.move = move;
		state.capturedPiece = capturedPiece;
		state.enPassantBefore = board.enPassantTarget;
		state.castlingRights = board.castlingRights;
		state.colorToMoveBefore = board.colorToMove;
		state.specialFlags = 0;

		// Castling
		if (isCastle(move, startPiece)) {
			state.specialFlags |= FLAG_CASTLE;
			handleCastleMove(move, startPiece);
		}
		// En passant
		else if (isEnPassant(move, startPiece)) {
			state.specialFlags |= FLAG_ENPASSANT;
			int epRank = (board.colorToMove == Piece.WHITE) ? move.endRank - 1 : move.endRank + 1;
			capturedPiece = board.get(move.endFile, epRank);
			state.capturedPiece = capturedPiece;
			board.set(move.endFile, epRank, Piece.INVALID);
			board.set(move.endFile, move.endRank, startPiece);
			board.set(move.startFile, move.startRank, Piece.INVALID);
		}
		// Promotion
		else if (isPromotion(move, startPiece)) {
			state.specialFlags |= FLAG_PROMOTION;
			board.set(move.endFile, move.endRank, Piece.QUEEN | Piece.getColor(startPiece));
			board.set(move.startFile, move.startRank, Piece.INVALID);
		}
		// else if (isPmove.promotedPiece != Piece.INVALID) {
		//     state.specialFlags |= FLAG_PROMOTION;
		//	   board.set(move.endFile, move.endRank, move.promotedPiece | Piece.getColor(startPiece));
		//	   board.set(move.startFile, move.startRank, Piece.INVALID);
		// }
		// Normal move
		else {
			board.set(move.endFile, move.endRank, startPiece);
			board.set(move.startFile, move.startRank, Piece.INVALID);
		}

		// Update board state
		board.enPassantTarget = calcNewEnPassant(move, startPiece);
		board.castlingRights = updateCastlingRights(move, startPiece, board.castlingRights);
		board.colorToMove = Piece.inverse(board.colorToMove); // switch sides

		stateStack.push(state);
		history.push(move);
		return state;
	}

	public void undoMove() {
		MoveState state = stateStack.pop();
		Move move = state.move;
		int startPiece = board.get(move.endFile, move.endRank);

		if ((state.specialFlags & FLAG_CASTLE) != 0) {
			undoCastleMove(move, startPiece);
		} else if ((state.specialFlags & FLAG_ENPASSANT) != 0) {
			board.set(move.startFile, move.startRank, startPiece);
			board.set(move.endFile, move.endRank, Piece.INVALID);
			int epRank = (state.colorToMoveBefore == Piece.WHITE) ? move.endRank - 1 : move.endRank + 1;
			board.set(move.endFile, epRank, state.capturedPiece);
		} else if ((state.specialFlags & FLAG_PROMOTION) != 0) {
			int pawn = Piece.PAWN | Piece.getColor(startPiece);
			board.set(move.startFile, move.startRank, pawn);
			board.set(move.endFile, move.endRank, state.capturedPiece);
		} else {
			board.set(move.startFile, move.startRank, startPiece);
			board.set(move.endFile, move.endRank, state.capturedPiece);
		}

		board.enPassantTarget = state.enPassantBefore;
		board.castlingRights = state.castlingRights;
		board.colorToMove = state.colorToMoveBefore;

		history.pop();
	}

	// --- Helpers ---

	public boolean isCastle(Move move, int piece) {
		return Piece.isType(piece, Piece.KING) && Math.abs(move.startFile - move.endFile) == 2;
	}

	public boolean isEnPassant(Move move, int piece) {
		return Piece.isType(piece, Piece.PAWN) && board.index(move.endFile, move.endRank) == board.enPassantTarget;
	}

	public boolean isPromotion(Move move, int piece) {
		// Must be a pawn
		if (!Piece.isType(piece, Piece.PAWN)) return false;

		// Check last rank depending on color
		if (Piece.isColor(piece, Piece.WHITE) && move.endRank == GameManager.GRID_SIZE - 1) return true;
		else if (Piece.isColor(piece, Piece.BLACK) && move.endRank == 0) return true;
		else return false;
	}

	public void handleCastleMove(Move move, int king) {
		board.set(move.endFile, move.endRank, king);
		board.set(move.startFile, move.startRank, Piece.INVALID);

		int rank = move.startRank;
		if (move.endFile == 6) { // kingside
			board.set(5, rank, board.get(7, rank));
			board.set(7, rank, Piece.INVALID);
		} else { // queenside
			board.set(3, rank, board.get(0, rank));
			board.set(0, rank, Piece.INVALID);
		}
	}

	public void undoCastleMove(Move move, int king) {
		board.set(move.startFile, move.startRank, king);
		board.set(move.endFile, move.endRank, Piece.INVALID);

		int rank = move.startRank;
		if (move.endFile == 6) { // kingside
			board.set(7, rank, board.get(5, rank));
			board.set(5, rank, Piece.INVALID);
		} else { // queenside
			board.set(0, rank, board.get(3, rank));
			board.set(3, rank, Piece.INVALID);
		}
	}

	public int calcNewEnPassant(Move move, int piece) {
		if (Piece.isType(piece, Piece.PAWN) && Math.abs(move.endRank - move.startRank) == 2) {
			return board.index(move.startFile, (move.startRank + move.endRank) / 2);
		}
		return -1;
	}

	public int updateCastlingRights(Move move, int piece, int rights) {
		int type = Piece.getType(piece);
		int color = Piece.getColor(piece);

		// King moves → remove both rights
		if (type == Piece.KING) {
			if (color == Piece.WHITE) rights &= ~(CASTLE_WHITE_KING | CASTLE_WHITE_QUEEN);
			else rights &= ~(CASTLE_BLACK_KING | CASTLE_BLACK_QUEEN);
		}

		// Rook moves → remove that side's right
		if (type == Piece.ROOK) {
			if (color == Piece.WHITE) {
				if (move.startFile == 0 && move.startRank == 0) rights &= ~CASTLE_WHITE_QUEEN;
				else if (move.startFile == 7 && move.startRank == 0) rights &= ~CASTLE_WHITE_KING;
			} else {
				if (move.startFile == 0 && move.startRank == 7) rights &= ~CASTLE_BLACK_QUEEN;
				else if (move.startFile == 7 && move.startRank == 7) rights &= ~CASTLE_BLACK_KING;
			}
		}

		return rights;
	}
}