package org.ajonx;

public class UIState {
	public int startFile = -1, startRank = -1;
	public int heldPiece = Piece.INVALID;
	
	public void resetHeldPiece() {
		heldPiece = Piece.INVALID;
		startFile = -1;
		startRank = -1;
	}
}
