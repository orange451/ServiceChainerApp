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
		try {
			return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
		} catch (JsonProcessingException e) {
			return null;
		}
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
