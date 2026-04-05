package org.ajonx.moves;

import org.ajonx.Board;

import java.util.List;

public interface PieceMoveRule {
	List<Move> generateMoves(Board board, int piece, int square);
}