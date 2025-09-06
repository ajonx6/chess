package org.ajonx.moves;

public class MoveState {
	public Move move;
	public int startPiece;
	public int capturedPiece;
	public int enPassantBefore;
	public boolean wk, wrq, wrk;
	public boolean bk, brq, brk;
	public int colorToMoveBefore;

	public boolean wasCastle;
	public int rookStartFile, rookStartRank;
	public int rookEndFile, rookEndRank;
	public int rookStartPieceBefore, rookEndPieceBefore;

	public MoveState(Move move, int startPiece, int capturedPiece, int enPassantBefore, boolean wk, boolean wrq, boolean wrk, boolean bk, boolean brq, boolean brk, int colorToMoveBefore) {
		this.move = move;
		this.startPiece = startPiece;
		this.capturedPiece = capturedPiece;
		this.enPassantBefore = enPassantBefore;
		this.wk = wk;
		this.wrq = wrq;
		this.wrk = wrk;
		this.bk = bk;
		this.brq = brq;
		this.brk = brk;
		this.colorToMoveBefore = colorToMoveBefore;
	}

	public MoveState(Move move, int startPiece, int capturedPiece, int enPassantBefore, boolean wk, boolean wrq, boolean wrk, boolean bk, boolean brq, boolean brk, int colorToMoveBefore, int rookStartFile, int rookStartRank, int rookEndFile, int rookEndRank, int rookStartPieceBefore, int rookEndPieceBefore) {
		this.move = move;
		this.startPiece = startPiece;
		this.capturedPiece = capturedPiece;
		this.enPassantBefore = enPassantBefore;
		this.wk = wk;
		this.wrq = wrq;
		this.wrk = wrk;
		this.bk = bk;
		this.brq = brq;
		this.brk = brk;
		this.colorToMoveBefore = colorToMoveBefore;

		this.wasCastle = true;
		this.rookStartFile = rookStartFile;
		this.rookStartRank = rookStartRank;
		this.rookEndFile = rookEndFile;
		this.rookEndRank = rookEndRank;
		this.rookStartPieceBefore = rookStartPieceBefore;
		this.rookEndPieceBefore = rookEndPieceBefore;
	}
}
