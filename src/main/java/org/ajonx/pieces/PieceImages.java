package org.ajonx.pieces;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PieceImages {
	public static BufferedImage[] images = null;

	public static void init(String imageName, int numPieceX, int numPieceY, List<Integer> mapKeys, int maxPiece) {
		try (InputStream is = PieceImages.class.getResourceAsStream("/" + imageName)) {
			if (is == null) throw new IOException("Resource not found: " + imageName);
			BufferedImage piecesImage = ImageIO.read(is);

			int pieceWidth = (piecesImage.getWidth(null) / numPieceX);
			int pieceHeight = (piecesImage.getHeight(null) / numPieceY);

			images = new BufferedImage[maxPiece + 1];

			int index = 0;
			for (int y = 0; y < numPieceY; y++) {
				for (int x = 0; x < numPieceX; x++) {
					BufferedImage subImage = piecesImage.getSubimage(x * pieceWidth, y * pieceHeight, pieceWidth, pieceHeight);
					images[mapKeys.get(index++)] = subImage;
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static BufferedImage getImage(int i) {
		return images[i];
	}

	public static List<Integer> getDefaultPieceList() {
		List<Integer> pieceTypes = new ArrayList<>();
		pieceTypes.add(Piece.WHITE | Piece.KING);
		pieceTypes.add(Piece.WHITE | Piece.QUEEN);
		pieceTypes.add(Piece.WHITE | Piece.BISHOP);
		pieceTypes.add(Piece.WHITE | Piece.KNIGHT);
		pieceTypes.add(Piece.WHITE | Piece.ROOK);
		pieceTypes.add(Piece.WHITE | Piece.PAWN);
		pieceTypes.add(Piece.BLACK | Piece.KING);
		pieceTypes.add(Piece.BLACK | Piece.QUEEN);
		pieceTypes.add(Piece.BLACK | Piece.BISHOP);
		pieceTypes.add(Piece.BLACK | Piece.KNIGHT);
		pieceTypes.add(Piece.BLACK | Piece.ROOK);
		pieceTypes.add(Piece.BLACK | Piece.PAWN);
		return pieceTypes;
	}
}