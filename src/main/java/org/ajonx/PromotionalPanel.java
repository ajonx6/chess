package org.ajonx;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class PromotionalPanel extends JPanel {
	private int[] promotionPieces;
	private BufferedImage[] images;
	private int cellSize;
	private JButton closeButton;

	public PromotionalPanel(int... promotionPieces) {
		this.promotionPieces = promotionPieces;
		this.images = new BufferedImage[promotionPieces.length];
	}
}