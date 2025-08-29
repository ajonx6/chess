package org.ajonx;


import java.awt.*;

public class Colors {
//	public static final Color LIGHT_COLOR = new Color(0xDFD7EF);
	//	public static final Color DARK_COLOR = new Color(0x9578B2);
	//
	//	public static final Color LIGHT_PREV_START_COLOR = new Color(0xDFF2DF);
	//	public static final Color DARK_PREV_START_COLOR = new Color(0x8BBF8B);
	//
	//	public static final Color LIGHT_PREV_END_COLOR = new Color(0xFFF4C2);
	//	public static final Color DARK_PREV_END_COLOR = new Color(0xE6D58C);
	//
	//	public static final Color LIGHT_MOVE_COLOR = new Color(0xFDDDDD);
	//	public static final Color DARK_MOVE_COLOR = new Color(0xD68B8B);

	public static final Color[] DEFAULT_COLORS = new Color[]{ new Color(0xDFD7EF), new Color(0x9578B2) };
	public static final Color[] PREV_START_COLORS = new Color[]{ new Color(0xDFF2B3), new Color(0xA3BF6E) };
	public static final Color[] PREV_END_COLORS = new Color[]{ new Color(0xFFE79C), new Color(0xE6C75F) };
	public static final Color[] MOVE_COLORS = new Color[]{ new Color(0xFF6666), new Color(0xB84040) };

	public static Color getColor(Color[] colors, boolean isLight) {
		return colors[isLight ? 0 : 1];
	}
}
