package org.ajonx;

import org.ajonx.games.GameManager;
import org.ajonx.moves.Move;
import org.ajonx.moves.MoveHandler;
import org.ajonx.moves.MoveGenerator;
import org.ajonx.pieces.Piece;
import org.ajonx.pieces.PieceImages;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Window {
	public static final int WIDTH = GameManager.CELL_SIZE * GameManager.GRID_SIZE;
	public static final int HEIGHT = GameManager.CELL_SIZE * GameManager.GRID_SIZE;

	private JFrame frame;
	private JPanel chessPanel;
	private UIState uiState;
	private GameManager gameManager;

	private int mouseX = -1, mouseY = -1;
	private boolean mouseWasPressed = false;
	private List<Move> movesForPiece = new ArrayList<>();

	public Window() {
		PieceImages.init("pieces.png", 6, 2, PieceImages.getDefaultPieceList());
		uiState = new UIState();

		frame = new JFrame("Chess");
		frame.setUndecorated(true);
		frame.setSize(WIDTH, HEIGHT);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		createChessPanel();

		frame.setVisible(true);
	}

	public void setGameManager(GameManager gameManager) {
		this.gameManager = gameManager;
	}

	public void createChessPanel() {
		chessPanel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D) g;
				g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
				g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
				drawBoard(g, g2);
			}
		};
		chessPanel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		chessPanel.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) System.exit(0);
			}
		});
		chessPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (mouseWasPressed) return;
				mouseWasPressed = true;
				handleMousePressed(e);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				mouseWasPressed = false;
				handleMouseReleased(e);
			}
		});
		chessPanel.addMouseMotionListener(new MouseAdapter() {
			public void mouseMoved(MouseEvent e) {
				mouseX = e.getX();
				mouseY = e.getY();
				if (uiState.heldPiece != Piece.INVALID) {
					chessPanel.repaint();
				}
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				mouseX = e.getX();
				mouseY = e.getY();
				if (uiState.heldPiece != Piece.INVALID) {
					chessPanel.repaint();
				}
			}
		});
		chessPanel.setFocusable(true);
		chessPanel.requestFocus();

		frame.add(chessPanel);
	}

	public void drawBoard(Graphics g, Graphics2D g2) {
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		for (int rank = 0; rank < GameManager.GRID_SIZE; rank++) {
			for (int file = 0; file < GameManager.GRID_SIZE; file++) {
				int uiY = (GameManager.GRID_SIZE - 1 - rank) * GameManager.CELL_SIZE;
				int uiX = file * GameManager.CELL_SIZE;

				Color color = getSquareColor(file, rank);
				g.setColor(color);
				g.fillRect(uiX, uiY, GameManager.CELL_SIZE, GameManager.CELL_SIZE);

				int piece = gameManager.board.get(gameManager.board.index(file, rank));
				if (piece != Piece.INVALID && !(file == uiState.startFile && rank == uiState.startRank && uiState.heldPiece != Piece.INVALID)) {
					BufferedImage pieceImage = PieceImages.images.get(piece);
					g.drawImage(pieceImage, uiX, uiY, GameManager.CELL_SIZE, GameManager.CELL_SIZE, null);
				}
			}
		}

		if (uiState.heldPiece != Piece.INVALID) {
			BufferedImage pieceImage = PieceImages.images.get(uiState.heldPiece);
			int imgX = mouseX - GameManager.CELL_SIZE / 2;
			int imgY = mouseY - GameManager.CELL_SIZE / 2;
			g.drawImage(pieceImage, imgX, imgY, GameManager.CELL_SIZE, GameManager.CELL_SIZE, null);
		}

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
	}

	private Color getSquareColor(int file, int rank) {
		boolean isLight = (file + rank) % 2 == 1;

		for (Move move : movesForPiece) {
			if (file == move.endFile && rank == move.endRank) return Colors.getColor(Colors.MOVE_COLORS, isLight);
		}

		if (file == uiState.startFile && rank == uiState.startRank)
			return Colors.getColor(Colors.PREV_START_COLORS, isLight);
		// if (file == lastStartFile && rank == lastStartRank) return Colors.getColor(Colors.PREV_START_COLORS, isLight);
		// if (file == lastEndFile && rank == lastEndRank) return Colors.getColor(Colors.PREV_END_COLORS, isLight);

		return Colors.getColor(Colors.DEFAULT_COLORS, isLight);
	}

	private void handleMousePressed(MouseEvent e) {
		if (uiState.heldPiece != Piece.INVALID) return;

		int file = e.getX() / GameManager.CELL_SIZE;
		int rankUI = e.getY() / GameManager.CELL_SIZE;
		int rank = GameManager.GRID_SIZE - 1 - rankUI;

		int pieceToHold = gameManager.board.get(gameManager.board.index(file, rank));
		if (pieceToHold == Piece.INVALID || Piece.isOppositeColor(pieceToHold, gameManager.board.colorToMove)) return;

		List<Move> moves = gameManager.getLegalMoves().get(gameManager.board.index(file, rank));
		resetHeldPiece();
		if (moves != null) movesForPiece.addAll(moves);

		uiState.heldPiece = pieceToHold;
		uiState.startFile = file;
		uiState.startRank = rank;
		mouseX = e.getX();
		mouseY = e.getY();

		chessPanel.repaint();
	}

	private void handleMouseReleased(MouseEvent e) {
		if (!isValidClick(e) || uiState.heldPiece == Piece.INVALID) {
			resetHeldPiece();
			return;
		}

		int file = e.getX() / GameManager.CELL_SIZE;
		int rankUI = e.getY() / GameManager.CELL_SIZE;
		int rank = GameManager.GRID_SIZE - 1 - rankUI;

		if (!isMoveAllowed(file, rank)) {
			resetHeldPiece();
			chessPanel.repaint();
			return;
		}

		Move move = new Move(uiState.startFile, uiState.startRank, file, rank);
		gameManager.runTurn(move);
		resetHeldPiece();
	}

	public boolean isValidClick(MouseEvent e) {
		return uiState.heldPiece != Piece.INVALID && e.getX() >= 0 && e.getY() >= 0 && e.getX() < WIDTH && e.getY() < HEIGHT;
	}

	public boolean isMoveAllowed(int file, int rank) {
		return movesForPiece.stream().anyMatch(move -> move.endFile == file && move.endRank == rank);
	}

	public void resetHeldPiece() {
		uiState.resetHeldPiece();
		movesForPiece.clear();
		mouseX = -1;
		mouseY = -1;
	}

	public UIState getUiState() {
		return uiState;
	}

	public void repaint() {
		chessPanel.repaint();
	}
}