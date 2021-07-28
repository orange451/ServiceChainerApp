package dev.anarchy.translate.util;

import java.util.Map;

import org.json.simple.JSONValue;

public class JSONUtils {
	public static String mapToJson(Map<Object, Object> map) {
		return JSONValue.toJSONString(map);
	}
}
