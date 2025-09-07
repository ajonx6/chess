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
	public static Map<Integer, BufferedImage> images = new HashMap<>();

	public static void init(String imageName, int numPieceX, int numPieceY, List<Integer> mapKeys) {
		try (InputStream is = PieceImages.class.getResourceAsStream("/" + imageName)) {
			if (is == null) throw new IOException("Resource not found: " + imageName);
			BufferedImage piecesImage = ImageIO.read(is);

			int pieceWidth = (piecesImage.getWidth(null) / numPieceX);
			int pieceHeight = (piecesImage.getHeight(null) / numPieceY);

			int index = 0;
			for (int y = 0; y < numPieceY; y++) {
				for (int x = 0; x < numPieceX; x++) {
					BufferedImage subImage = piecesImage.getSubimage(x * pieceWidth, y * pieceHeight, pieceWidth, pieceHeight);
					images.put(mapKeys.get(index++), subImage);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
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