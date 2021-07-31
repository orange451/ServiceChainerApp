package dev.anarchy.ui.util;

import javafx.scene.paint.Color;

public class ColorHelper {
	private static String format(double val) {
		String in = Integer.toHexString((int) Math.round(val * 255));
		return in.length() == 1 ? "0" + in : in;
	}

	public static String toHexString(Color value) {
		return "#" + (format(value.getRed()) + format(value.getGreen()) + format(value.getBlue())
				+ format(value.getOpacity())).toUpperCase();
	}
	
	public static Color fromHexString(String value) {
		return Color.web(value);
	}
}
