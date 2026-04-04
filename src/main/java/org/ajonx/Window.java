package org.ajonx;

import org.ajonx.pieces.Piece;
import org.ajonx.pieces.PieceImages;
import org.ajonx.ui.BoardPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

public class Window {
	public static final int WIDTH = Constants.CELL_SIZE * Constants.GRID_SIZE;
	public static final int HEIGHT = Constants.CELL_SIZE * Constants.GRID_SIZE;

	private final JFrame frame;
	private BoardPanel boardPanel;
	private GameManager manager;

	public Window() {
		PieceImages.init("pieces.png", 6, 2, PieceImages.getDefaultPieceList(), Piece.BLACK | Piece.PAWN);

		Board board = new Board(Constants.GRID_SIZE, Constants.GRID_SIZE);
		board.loadDefaultGame();
		manager = new GameManager(board);

		frame = new JFrame("Chess");
		frame.setUndecorated(true);
		frame.setSize(WIDTH, HEIGHT);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		createChessPanel();

		frame.setVisible(true);
	}

	public void createChessPanel() {
		boardPanel = new BoardPanel(manager);
		boardPanel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		frame.add(boardPanel);
	}

	public void repaint() {
		boardPanel.repaint();
	}
}