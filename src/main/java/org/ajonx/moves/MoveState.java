package org.ajonx.moves;

public class MoveState {
	public Move move;             // the move made
	public int capturedPiece;     // piece taken (if any)
	public int enPassantBefore;   // previous en passant target (-1 if none)
	public int castlingRights;    // 4-bit mask: WK WQ BK BQ
	public int colorToMoveBefore; // 0=white, 1=black
	public int specialFlags;      // bit flags: see below
}
