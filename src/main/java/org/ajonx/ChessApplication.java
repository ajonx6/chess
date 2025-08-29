package org.ajonx;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class ChessApplication {
	public static final int GRID_SIZE = 8;
	public static final int CELL_SIZE = 150;
	public static final int WIDTH = CELL_SIZE * GRID_SIZE;
	public static final int HEIGHT = CELL_SIZE * GRID_SIZE;

	private JFrame frame;
	private JPanel chessPanel;
	private Board board;

	private int mouseX = -1, mouseY = -1;
	private boolean mouseWasPressed = false;

	private int lastStartFile = -1, lastStartRank = -1;
	private int lastEndFile = -1, lastEndRank = -1;
	private int startFile = -1, startRank = -1;
	private int heldPiece = Piece.INVALID;
	private List<Moves.Move> movesForPiece = new ArrayList<>();

	public ChessApplication() {
		PieceImages.init("pieces.png", 6, 2, PieceImages.getDefaultPieceList());
		board = new Board(GRID_SIZE, GRID_SIZE);
		Moves.init(board);

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
				if (heldPiece != Piece.INVALID) {
					chessPanel.repaint();
				}
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				mouseX = e.getX();
				mouseY = e.getY();
				if (heldPiece != Piece.INVALID) {
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

		for (int rank = 0; rank < GRID_SIZE; rank++) {
			for (int file = 0; file < GRID_SIZE; file++) {
				int uiY = (GRID_SIZE - 1 - rank) * CELL_SIZE;
				int uiX = file * CELL_SIZE;

				Color color = getSquareColor(file, rank);
				g.setColor(color);
				g.fillRect(uiX, uiY, ChessApplication.CELL_SIZE, ChessApplication.CELL_SIZE);

				int piece = board.get(board.index(file, rank));
				if (piece != Piece.INVALID) {
					BufferedImage pieceImage = PieceImages.images.get(piece);
					g.drawImage(pieceImage, uiX, uiY, CELL_SIZE, CELL_SIZE, null);
				}
			}
		}

		if (heldPiece != Piece.INVALID) {
			BufferedImage pieceImage = PieceImages.images.get(heldPiece);
			int imgX = mouseX - CELL_SIZE / 2;
			int imgY = mouseY - CELL_SIZE / 2;
			g.drawImage(pieceImage, imgX, imgY, CELL_SIZE, CELL_SIZE, null);
		}

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
	}

	private Color getSquareColor(int file, int rank) {
		boolean isLight = (file + rank) % 2 == 1;

		for (Moves.Move move : movesForPiece) {
			if (file == move.efile && rank == move.erank) return Colors.getColor(Colors.MOVE_COLORS, isLight);
		}

		if (file == startFile && rank == startRank) return Colors.getColor(Colors.PREV_START_COLORS, isLight);
		// if (file == lastStartFile && rank == lastStartRank) return Colors.getColor(Colors.PREV_START_COLORS, isLight);
		// if (file == lastEndFile && rank == lastEndRank) return Colors.getColor(Colors.PREV_END_COLORS, isLight);

		return Colors.getColor(Colors.DEFAULT_COLORS, isLight);
	}

	private void handleMousePressed(MouseEvent e) {
		if (heldPiece != Piece.INVALID) return;

		int file = e.getX() / CELL_SIZE;
		int rankUI = e.getY() / CELL_SIZE;
		int rank = GRID_SIZE - 1 - rankUI;

		int pieceToHold = board.get(board.index(file, rank));
		if (pieceToHold == Piece.INVALID || !Piece.isColor(pieceToHold, board.colorToMove)) return;

		board.set(board.index(file, rank), Piece.INVALID);
		movesForPiece.addAll(Moves.generateMoves(file, rank, pieceToHold));
		heldPiece = pieceToHold;
		startFile = file;
		startRank = rank;
		mouseX = e.getX();
		mouseY = e.getY();

		chessPanel.repaint();
	}

	private void handleMouseReleased(MouseEvent e) {
		if (heldPiece == Piece.INVALID) return;
		if (e.getX() < 0 || e.getY() < 0 || e.getX() >= WIDTH || e.getY() >= HEIGHT) return;

		int file = e.getX() / CELL_SIZE;
		int rankUI = e.getY() / CELL_SIZE;
		int rank = GRID_SIZE - 1 - rankUI;

		boolean moveOk = movesForPiece.stream().anyMatch(move -> move.efile == file && move.erank == rank);

		if (moveOk) {
			board.set(file, rank, heldPiece);
			if (Piece.isType(heldPiece, Piece.PAWN) && file + rank * GRID_SIZE == board.enPassantTarget) board.set(board.enPassantTarget % 8, startRank, Piece.INVALID);

			if (Math.abs(startRank - rank) == 2 && Piece.isType(heldPiece, Piece.PAWN)) {
				board.enPassantTarget = startFile + ((startRank + rank) / 2) * GRID_SIZE;
			} else {
				board.enPassantTarget = -1;
			}

			if (startFile != file || startRank != rank) {
				lastStartFile = startFile;
				lastStartRank = startRank;
				lastEndFile = file;
				lastEndRank = rank;
			}

			board.colorToMove = Piece.inverse(board.colorToMove);
		} else {
			board.set(startFile, startRank, heldPiece);
		}

		heldPiece = Piece.INVALID;
		mouseX = -1;
		mouseY = -1;
		movesForPiece.clear();
		startFile = -1;
		startRank = -1;

		chessPanel.repaint();
	}

	public static void main(String[] args) {
		new ChessApplication();
	}
}