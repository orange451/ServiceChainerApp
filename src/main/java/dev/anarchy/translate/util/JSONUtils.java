package dev.anarchy.translate.util;

import java.util.Map;

import org.json.simple.JSONValue;

public class JSONUtils {
	public static String mapToJson(Map<Object, Object> map) {
		return JSONValue.toJSONString(map);
	}
	
	public static String mapToJsonPretty(Map<Object, Object> jsonObject) {
		String str = mapToJson(jsonObject);
		str = str.replace("{", "{\n");
		str = str.replace("[", "[\n");
		str = str.replace("]", "\n]");
		str = str.replace("}", "\n}");
		str = str.replace(",", ",\n");
		
		String[] strs = str.split("\n");
		int tab = 0;
		for (int i = 0; i < strs.length; i++) {
			String s = strs[i];

			if ( s.contains("}") || s.contains("]") )
				tab--;
			
			String prefix = "";
			for (int j = 0; j < tab; j++) {
				prefix += "\t";
			}
			
			strs[i] = prefix + s;
			
			if ( s.contains("{") || s.contains("[") )
				tab++;
		}
		
		StringBuilder finalString = new StringBuilder();
		for (int i = 0; i < strs.length; i++) {
			finalString.append(strs[i]);
			finalString.append("\n");
		}
		
		return finalString.toString();
	}
}
