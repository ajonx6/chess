package org.ajonx;


import java.awt.*;

public class Colors {
	public static final Color[] DEFAULT_COLORS = new Color[]{ new Color(0xDFD7EF), new Color(0x9578B2) };
	public static final Color[] PREV_FROM_COLORS = new Color[]{ new Color(0xDFF2B3), new Color(0xA3BF6E) };
	public static final Color[] PREV_TO_COLORS = new Color[]{ new Color(0xFFE79C), new Color(0xE6C75F) };
	public static final Color[] MOVE_COLORS = new Color[]{ new Color(0xFF6666), new Color(0xB84040) };

	public static Color getColor(Color[] colors, boolean isLight) {
		return colors[isLight ? 0 : 1];
	}
}
