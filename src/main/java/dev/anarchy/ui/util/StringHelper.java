package dev.anarchy.ui.util;

public class StringHelper {
	public static String insert(String source, int index, String insert) {
		StringBuilder builder = new StringBuilder();
		
		builder.append(source.substring(0, index));
		builder.append(insert);
		builder.append(source.substring(index));
		
		return builder.toString();
	}
}
