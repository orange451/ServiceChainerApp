package dev.anarchy.translate.util;

import java.util.Map;

import org.json.simple.JSONValue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONUtils {
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jsonToMap(String json) throws JsonProcessingException {
		return new ObjectMapper().readValue(json, Map.class);
	}
	
	public static String mapToJson(Map<String, Object> map) {
		return JSONValue.toJSONString(map);
	}
	
	public static String mapToJsonPretty(Map<String, Object> jsonObject) {
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
	
	public static String escape(String value) {
		if ( value == null )
			return value;
		
		value = value.replace("\u0000", "\\0")
				.replace("'", "\\'")
				.replace("\\", "\\\\")
				.replace("\"", "\\\"")
				.replace("\n", "\\n")
				.replace("\r", "\\r")
				.replace("\t", "\\t");
		
		return "\"" + value + "\"";
	}
}
