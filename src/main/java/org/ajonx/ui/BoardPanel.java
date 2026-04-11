package org.ajonx.ui;

import org.ajonx.*;
import org.ajonx.moves.Move;
import org.ajonx.pieces.Piece;
import org.ajonx.pieces.PieceImages;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class BoardPanel extends JPanel {
	private GameManager manager;

	private int mouseX, mouseY;
	private int selectedSquare = -1;
	private int selectedPiece = Piece.INVALID;
	private List<Move> movesForPiece = new ArrayList<>();

	public BoardPanel(GameManager manager) {
		this.manager = manager;
		this.manager.addListener(this::repaint);

		BoardPanelMouseListener mouseListener = new BoardPanelMouseListener();
		addMouseListener(mouseListener);
		addMouseMotionListener(mouseListener);
		addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {

			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) System.exit(0);
			}

			@Override
			public void keyReleased(KeyEvent e) {

			}
		});

		setFocusable(true);
		requestFocus();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);

		Board board = manager.getBoard();

		drawSquares(g2d);
		drawPieces(g2d, board);
		drawPickedUp(g2d);
	}

	private void drawSquares(Graphics2D g2d) {
		for (int rank = 0; rank < Constants.GRID_SIZE; rank++) {
			for (int file = 0; file < Constants.GRID_SIZE; file++) {
				int uiX = toXCoord(file), uiY = toYCoord(rank);

				Color color = getSquareColor(file, rank);
				g2d.setColor(color);
				g2d.fillRect(uiX, uiY, Constants.CELL_SIZE, Constants.CELL_SIZE);
			}
		}
	}

	private Color getSquareColor(int file, int rank) {
		boolean isLight = (file + rank) % 2 == 1;
		Color[] theme = Colors.DEFAULT_COLORS;
		int square = Util.toIndex(file, rank);

		if (selectedPiece != Piece.INVALID && !movesForPiece.isEmpty() && movesForPiece.stream().anyMatch(m -> m.getTo() == square)) {
			theme = Colors.MOVE_COLORS;
		} else if (selectedPiece != Piece.INVALID && square == selectedSquare) {
			theme = Colors.CURRENT_COLORS;
		} else if (manager.getPreviousMove().getFrom() == square) {
			theme = Colors.PREV_FROM_COLORS;
		} else if (manager.getPreviousMove().getTo() == square) {
			theme = Colors.PREV_TO_COLORS;
		}

		return Colors.getColor(theme, isLight);
	}

	private void drawPieces(Graphics2D g2d, Board board) {
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		for (int rank = 0; rank < Constants.GRID_SIZE; rank++) {
			for (int file = 0; file < Constants.GRID_SIZE; file++) {
				int uiX = toXCoord(file), uiY = toYCoord(rank);
				if (selectedSquare == Util.toIndex(file, rank)) continue;

				int piece = board.get(board.index(file, rank));
				if (piece != Piece.INVALID) {
					BufferedImage pieceImage = PieceImages.getImage(piece);
					g2d.drawImage(pieceImage, uiX, uiY, Constants.CELL_SIZE, Constants.CELL_SIZE, null);
				}
			}
		}

		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
	}

	private void drawPickedUp(Graphics2D g2d) {
		if (selectedPiece == Piece.INVALID) return;

		BufferedImage pieceImage = PieceImages.getImage(selectedPiece);
		g2d.drawImage(pieceImage, mouseX - Constants.CELL_SIZE / 2, mouseY - Constants.CELL_SIZE / 2, Constants.CELL_SIZE, Constants.CELL_SIZE, null);
	}

	public static int toFileFromCoord(int x) {
		return x / Constants.CELL_SIZE;
	}

	public static int toXCoord(int file) {
		return file * Constants.CELL_SIZE;
	}

	public static int toRankFromCoord(int y) {
		return Constants.GRID_SIZE - 1 - y / Constants.CELL_SIZE;
	}

	public static int toYCoord(int rank) {
		return (Constants.GRID_SIZE - 1 - rank) * Constants.CELL_SIZE;
	}

	private class BoardPanelMouseListener implements MouseListener, MouseMotionListener {
		public void mousePressed(MouseEvent e) {
			if (selectedPiece != Piece.INVALID) return;

			int file = toFileFromCoord(e.getX());
			int rank = toRankFromCoord(e.getY());
			selectedSquare = Util.toIndex(file, rank);
			selectedPiece = manager.getBoard().get(selectedSquare);
			movesForPiece = manager.getAllMovesThisTurn().containsKey(selectedSquare) ? manager.getAllMovesThisTurn().get(selectedSquare) : new ArrayList<>();
		}

		public void mouseReleased(MouseEvent e) {
			if (selectedPiece == Piece.INVALID) return;

			if (!movesForPiece.isEmpty()) {
				int file = toFileFromCoord(e.getX());
				int rank = toRankFromCoord(e.getY());
				int endSquare = Util.toIndex(file, rank);
				Move move = movesForPiece.stream().filter(m -> m.getTo() == endSquare).findFirst().orElse(null);
				if (move != null) manager.makeMove(move);
			}

			selectedSquare = -1;
			selectedPiece = Piece.INVALID;
			repaint();
		}

		public void mouseDragged(MouseEvent e) {
			mouseX = e.getX();
			mouseY = e.getY();
			repaint();
		}

		public void mouseClicked(MouseEvent e) {}

		public void mouseEntered(MouseEvent e) {}

		public void mouseExited(MouseEvent e) {}

		public void mouseMoved(MouseEvent e) {

		}
	}
}