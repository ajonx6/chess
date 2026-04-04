package org.ajonx.ui;

import org.ajonx.Board;
import org.ajonx.Colors;
import org.ajonx.Constants;
import org.ajonx.GameManager;
import org.ajonx.pieces.Piece;
import org.ajonx.pieces.PieceImages;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class BoardPanel extends JPanel {
	private GameManager manager;

	private int mouseX, mouseY;
	private boolean dragging = false;
	private int selectedSquare = -1;

	public BoardPanel(GameManager manager) {
		this.manager = manager;

		addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {

			}

			@Override
			public void mousePressed(MouseEvent e) {

			}

			@Override
			public void mouseReleased(MouseEvent e) {

			}

			@Override
			public void mouseEntered(MouseEvent e) {

			}

			@Override
			public void mouseExited(MouseEvent e) {

			}
		});
		addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseDragged(MouseEvent e) {

			}

			@Override
			public void mouseMoved(MouseEvent e) {

			}
		});
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
				int uiX = toUIX(file), uiY = toUIY(rank);

				Color color = getSquareColor(file, rank);
				g2d.setColor(color);
				g2d.fillRect(uiX, uiY, Constants.CELL_SIZE, Constants.CELL_SIZE);
			}
		}
	}

	private Color getSquareColor(int file, int rank) {
		boolean isLight = (file + rank) % 2 == 1;
		return Colors.getColor(Colors.DEFAULT_COLORS, isLight);
	}

	private void drawPieces(Graphics2D g2d, Board board) {
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		for (int rank = 0; rank < Constants.GRID_SIZE; rank++) {
			for (int file = 0; file < Constants.GRID_SIZE; file++) {
				int uiX = toUIX(file), uiY = toUIY(rank);

				int piece = board.get(board.index(file, rank));
				if (piece != Piece.INVALID) {
					BufferedImage pieceImage = PieceImages.getImage(piece);
					g2d.drawImage(pieceImage, uiX, uiY, Constants.CELL_SIZE, Constants.CELL_SIZE, null);
				}
			}
		}

		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
	}

	private void drawPickedUp(Graphics g) {

	}

	private int toUIX(int file) {
		return file * Constants.CELL_SIZE;
	}

	private int toUIY(int rank) {
		return (Constants.GRID_SIZE - 1 - rank) * Constants.CELL_SIZE;
	}
}