package org.ajonx.moves;

import org.ajonx.Board;
import org.ajonx.games.GameManager;
import org.ajonx.pieces.Piece;
import org.ajonx.pieces.behaviours.KingBehaviour;
import org.ajonx.pieces.behaviours.KnightBehaviour;
import org.ajonx.pieces.behaviours.PawnBehaviour;
import org.ajonx.pieces.behaviours.SlidingBehaviour;

import javax.smartcardio.ATR;
import java.util.*;
import java.util.stream.Collectors;

public class MoveGenerator {
	public int[] directionOffsets = new int[]{ 8, -8, -1, 1, 7, -7, 9, -9 };
	public int[][] numSquaresToEdge = new int[GameManager.GRID_SIZE * GameManager.GRID_SIZE][8];
	public Set<Integer> attackedSquares = new HashSet<>();
	public Map<Integer, Set<Integer>> pieceAttackSquares = new HashMap<>();
	public Set<Integer> pinnedPieces = new HashSet<>();
	public List<Integer> checkers = new ArrayList<>();

	private Board board;

	public MoveGenerator(Board board) {
		this.board = board;
		precalculateDistances();
	}

	public void precalculateDistances() {
		for (int rank = 0; rank < 8; rank++) {
			for (int file = 0; file < 8; file++) {
				int numUp = GameManager.GRID_SIZE - 1 - rank;
				int numDown = rank;
				int numLeft = file;
				int numRight = GameManager.GRID_SIZE - 1 - file;
				int index = board.index(file, rank);
				numSquaresToEdge[index] = new int[]{ numUp, numDown, numLeft, numRight, Math.min(numUp, numLeft), Math.min(numDown, numRight), Math.min(numUp, numRight), Math.min(numDown, numLeft) };
			}
		}
	}

	public void precomputeData() {
		generateAttacks(Piece.inverse(board.colorToMove));
		generatePinnedSquares(board.colorToMove);
		generateCheckers(board.colorToMove);
	}

	public Map<Integer, List<Move>> generateLegalMoveMap() {
		return generateLegalMoves().stream().collect(Collectors.groupingBy(m -> board.index(m.startFile, m.startRank)));
	}

	public List<Move> generateLegalMoves() {
		List<Move> moves = generateMoves(board.colorToMove);
		List<Move> legalMoves = new ArrayList<>();
		int kingIndex = board.indexOfKing(board.colorToMove);

		for (Move move : moves) {
			int startIndex = board.index(move.startFile, move.startRank);
			int endIndex = board.index(move.endFile, move.endRank);
			int movingPiece = board.get(startIndex);

			if (checkers.size() > 1) {
				if (!Piece.isType(movingPiece, Piece.KING)) continue;
				if (attackedSquares.contains(endIndex)) continue;
			} else if (checkers.size() == 1) {
				if (Piece.isType(movingPiece, Piece.KING) && attackedSquares.contains(endIndex)) continue;
				if (!Piece.isType(movingPiece, Piece.KING) && !canBlockOrCapture(move, kingIndex, checkers.getFirst())) continue;
			} else {
				if (Piece.isType(movingPiece, Piece.KING) && attackedSquares.contains(endIndex)) continue;

				if (pinnedPieces.contains(startIndex)) {
					if (!isMoveAlongPinLine(move, kingIndex)) continue;
				}
				if (isCastle(move) && isCastleIllegal(move)) continue;
				if (isEnPassant(move) && isEnPassantIllegal(move, kingIndex)) continue;
				if (isPromotion(move)) {
					Move p1 = new Move(move, Piece.QUEEN);
					Move p2 = new Move(move, Piece.ROOK);
					Move p3 = new Move(move, Piece.BISHOP);
					Move p4 = new Move(move, Piece.KNIGHT);

					legalMoves.add(p1);
					legalMoves.add(p2);
					legalMoves.add(p3);
					legalMoves.add(p4);
					continue;
				}
			}

			legalMoves.add(move);
		}

		return legalMoves;
	}

	public boolean isCastle(Move move) {
		int piece = board.get(move.startFile, move.startRank);
		return Piece.isType(piece, Piece.KING) && Math.abs(move.startFile - move.endFile) == 2;
	}

	public boolean isEnPassant(Move move) {
		int piece = board.get(move.startFile, move.startRank);
		return Piece.isType(piece, Piece.PAWN) && board.index(move.endFile, move.endRank) == board.enPassantTarget;
	}

	public boolean isPromotion(Move move) {
		int piece = board.get(move.startFile, move.startRank);
		if (!Piece.isType(piece, Piece.PAWN)) return false;

		if (Piece.isColor(piece, Piece.WHITE) && move.endRank == GameManager.GRID_SIZE - 1) return true;
		else if (Piece.isColor(piece, Piece.BLACK) && move.endRank == 0) return true;
		else return false;
	}

	public List<Move> generateMoves(int color) {
		List<Move> moves = new ArrayList<>();
		for (int rank = 0; rank < 8; rank++) {
			for (int file = 0; file < 8; file++) {
				int piece = board.get(file, rank);
				if (Piece.isColor(piece, color)) {moves.addAll(generateMovesAt(file, rank, piece));}
			}
		}
		return moves;
	}

	public List<Move> generateOpponentMoves(int color) {
		List<Move> moves = new ArrayList<>();
		for (int rank = 0; rank < 8; rank++) {
			for (int file = 0; file < 8; file++) {
				int piece = board.get(file, rank);
				if (Piece.isOppositeColor(piece, color)) {moves.addAll(generateMovesAt(file, rank, piece));}
			}
		}
		return moves;
	}

	public List<Move> generateMovesAt(int file, int rank, int piece) {
		if (Piece.isSlider(piece)) return SlidingBehaviour.getMovement(board, file, rank, piece, numSquaresToEdge);
		else if (Piece.isType(piece, Piece.KING)) return KingBehaviour.getMovement(board, file, rank, piece);
		else if (Piece.isType(piece, Piece.KNIGHT)) return KnightBehaviour.getMovement(board, file, rank, piece);
		else if (Piece.isType(piece, Piece.PAWN)) return PawnBehaviour.getMovement(board, file, rank, piece);
		else return new ArrayList<>();
	}

	public void generateAttacks(int color) {
		attackedSquares.clear();
		pieceAttackSquares.clear();

		for (int rank = 0; rank < 8; rank++) {
			for (int file = 0; file < 8; file++) {
				int piece = board.get(file, rank);
				if (Piece.isColor(piece, color)) {
					List<Move> attacks = generateAttacksAt(file, rank, piece);
					Set<Integer> squares = attacks.stream().map(m -> board.index(m.endFile, m.endRank)).collect(Collectors.toSet());
					attackedSquares.addAll(squares);
					pieceAttackSquares.put(board.index(file, rank), squares);
				}
			}
		}
	}

	public List<Move> generateAttacksAt(int file, int rank, int piece) {
		if (Piece.isSlider(piece)) return SlidingBehaviour.getAttacks(board, file, rank, piece, numSquaresToEdge);
		else if (Piece.isType(piece, Piece.KING)) return KingBehaviour.getAttacks(board, file, rank, piece);
		else if (Piece.isType(piece, Piece.KNIGHT)) return KnightBehaviour.getAttacks(board, file, rank, piece);
		else if (Piece.isType(piece, Piece.PAWN)) return PawnBehaviour.getAttacks(board, file, rank, piece);
		else return new ArrayList<>();
	}

	public void generatePinnedSquares(int color) {
		pinnedPieces.clear();
		int kingSquare = board.indexOfKing(color);

		for (int d = 0; d < directionOffsets.length; d++) {
			int dirOffset = directionOffsets[d];
			int distToEdge = numSquaresToEdge[kingSquare][d];

			int candidatePin = -1;
			for (int i = 1; i <= distToEdge; i++) {
				int square = kingSquare + dirOffset * i;
				int piece = board.get(square);

				if (piece == Piece.INVALID) continue;

				if (Piece.matchColor(color, piece)) {
					if (candidatePin == -1) candidatePin = square;
					else break;
				} else {
					boolean canPin = false;
					if (Piece.isType(piece, Piece.ROOK) && d < 4) canPin = true;
					else if (Piece.isType(piece, Piece.BISHOP) && d >= 4) canPin = true;
					else if (Piece.isType(piece, Piece.QUEEN)) canPin = true;

					if (canPin && candidatePin != -1) pinnedPieces.add(candidatePin);
					break;
				}
			}
		}
	}

	public void generateCheckers(int color) {
		checkers.clear();

		int kingSquare = board.indexOfKing(color);
		for (int attackPieceIndex : pieceAttackSquares.keySet()) {
			Set<Integer> squares = pieceAttackSquares.get(attackPieceIndex);
			if (squares.contains(kingSquare)) checkers.add(attackPieceIndex);
		}
	}

	public boolean canBlockOrCapture(Move move, int kingIndex, int checkerIndex) {
		int endIndex = board.index(move.endFile, move.endRank);

		if (endIndex == checkerIndex) return true;

		int checkerPiece = board.get(checkerIndex);
		if (!(Piece.isType(checkerPiece, Piece.ROOK) || Piece.isType(checkerPiece, Piece.BISHOP) || Piece.isType(checkerPiece, Piece.QUEEN))) {
			return false;
		}

		int kingFile = kingIndex % 8;
		int kingRank = kingIndex / 8;
		int checkerFile = checkerIndex % 8;
		int checkerRank = checkerIndex / 8;

		int df = Integer.signum(kingFile - checkerFile);
		int dr = Integer.signum(kingRank - checkerRank);

		int file = checkerFile + df;
		int rank = checkerRank + dr;
		while (file != kingFile || rank != kingRank) {
			int square = rank * 8 + file;
			if (square == endIndex) return true;
			file += df;
			rank += dr;
		}

		return false;
	}

	public boolean isMoveAlongPinLine(Move move, int kingIndex) {
		int kingFile = kingIndex % 8;
		int kingRank = kingIndex / 8;

		int dfKing = move.startFile - kingFile;
		int drKing = move.startRank - kingRank;
		int dfMove = move.endFile - move.startFile;
		int drMove = move.endRank - move.startRank;

		if (dfKing == 0) return dfMove == 0;
		if (drKing == 0) return drMove == 0;
		return Math.abs(dfKing) == Math.abs(drKing)
				&& Math.abs(dfMove) == Math.abs(drMove)
				&& Integer.signum(dfKing) == Integer.signum(dfMove)
				&& Integer.signum(drKing) == Integer.signum(drMove);
	}

	public boolean isCastleIllegal(Move move) {
		int startFile = move.startFile;
		int endFile = move.endFile;
		int rank = move.startRank;

		int step = (endFile > startFile) ? 1 : -1; // +1 kingside, -1 queenside

		for (int file = startFile; file != endFile; file += step) {
			if (attackedSquares.contains(board.index(file, rank))) return true;
		}

		return false;
	}

	public boolean isEnPassantIllegal(Move move, int kingIndex) {
		int startIndex = board.index(move.startFile, move.startRank);
		int endIndex = board.index(move.endFile, move.endRank);
		int color = Piece.getColor(board.get(startIndex)); // Assuming color info stored

		// If pawn is not pinned, en passant is legal
		if (!pinnedPieces.contains(startIndex)) return false;

		int kingFile = kingIndex % 8;
		int kingRank = kingIndex / 8;
		int pawnFile = move.startFile;
		int pawnRank = move.startRank;

		// Direction from king to pinned pawn
		int dfKing = pawnFile - kingFile;
		int drKing = pawnRank - kingRank;

		// Direction from king to pawn along pin line
		int pinDirFile = Integer.signum(dfKing);
		int pinDirRank = Integer.signum(drKing);

		// Check if pawn move is along pin line
		int dfMove = move.endFile - move.startFile;
		int drMove = move.endRank - move.startRank;
		if (dfKing == 0 && dfMove != 0) return true;       // vertical pin → can't move sideways
		if (drKing == 0 && drMove != 0) return true;       // horizontal pin → can't leave rank
		if (Math.abs(dfKing) == Math.abs(drKing)) {        // diagonal pin
			if (Math.abs(dfMove) != Math.abs(drMove) || Integer.signum(dfMove) != pinDirFile || Integer.signum(drMove) != pinDirRank)
				return true;
		}

		// Check X-ray: would capturing pawn expose king along the pin line?
		int captureFile = move.endFile;
		int captureRank = move.startRank; // captured pawn is on the same rank
		int dirFile = Integer.signum(captureFile - kingFile);
		int dirRank = Integer.signum(captureRank - kingRank);

		int f = kingFile + dirFile;
		int r = kingRank + dirRank;
		boolean foundCapturedPawn = false;
		while (f >= 0 && f < 8 && r >= 0 && r < 8) {
			int idx = r * 8 + f;
			int piece = board.get(idx);

			if (!foundCapturedPawn) {
				if (idx == board.index(captureFile, captureRank)) {
					foundCapturedPawn = true; // pawn that will be removed
				} else if (piece != Piece.INVALID) {
					break; // blocked before captured pawn
				}
			} else {
				if (piece != Piece.INVALID) {
					if (Piece.isOppositeColor(piece, color) && (Piece.isType(piece, Piece.ROOK) || Piece.isType(piece, Piece.QUEEN) && (dirFile == 0 || dirRank == 0)
							|| Piece.isType(piece, Piece.BISHOP) && (Math.abs(dirFile) == Math.abs(dirRank)))) {
						return true; // exposes check
					} else break; // blocked by other piece
				}
			}

			f += dirFile;
			r += dirRank;
		}

		return false; // legal
	}


	public boolean isKingInCheck(int color) {
		return attackedSquares.contains(board.indexOfKing(color));
	}
}