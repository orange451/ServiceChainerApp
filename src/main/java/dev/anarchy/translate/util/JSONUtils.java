package dev.anarchy.translate.util;

import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class JSONUtils {
	
	/** Custom Pretty Printer Utilizing Tabs **/
	private static ObjectWriter prettyWriter;
	
	/** Default Object writer to serialize objects **/
	private static ObjectWriter objectWriter;
	
	/** Default Object Mapper **/
	private static ObjectMapper objectMapper;
	
	/** Initialize **/
	static {
		DefaultPrettyPrinter prettyPrinter = new CustomPrettyPrinter();

		objectMapper = new ObjectMapper();
		prettyWriter = objectMapper.writer(prettyPrinter);
		objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
	}
	
	/**
	 * Convenient method to marshal a json string in to a java map.
	 * @param jsonStr
	 * @param classType
	 * @return A java map that represents the user-supplied json.
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jsonToMap(String json) throws JsonProcessingException {
		return convertToObject(json, Map.class);
	}
	
    /**
     * Convenient method to turn a java Map into a JSON string.
     * @param o The map to convert to a JSON string
     * @return The JSON string or null if the conversion failed
     */
	public static String mapToJson(Map<String, Object> map) {
		try {
			return convertToJson(map);
		} catch (JsonProcessingException e) {
			return null;
		}
	}
	
    /**
     * Convenient method to turn a java Map into a JSON string. Applies pretty printing.
     * @param o The map to convert to a JSON string
     * @return The JSON string or null if the conversion failed
     */
	public static String mapToJsonPretty(Map<String, Object> jsonObject) {
		try {
			return convertToJsonPretty(jsonObject);
		} catch (JsonProcessingException e) {
			return null;
		}
	}
	
	/**
	 * Convenient method to marshal a json string in to a specified class type.
	 * @param jsonStr
	 * @param classType
	 * @return The object of the class type the json is marshaled in to.
	 */
    public static <T> T convertToObject(String jsonStr, Class<T> classType) throws JsonMappingException, JsonProcessingException {
        return objectMapper.readValue(jsonStr, classType);
    }

    /**
     * Convenient method to turn an object into a JSON string.
     */
    public static String convertToJson(Object object) throws JsonProcessingException {
        return objectWriter.writeValueAsString(object);
    }

    /**
     * Convenient method to turn an object into a JSON string.
     */
    public static String convertToJsonPretty(Object object) throws JsonProcessingException {
        return prettyWriter.writeValueAsString(object);
    }

    /**
     * Convenient method to turn an object into a JSON string.
     * @param o The object to convert to a JSON string
     * @return The JSON string or null if the conversion failed
     */
    public static String objectToJSON(Object o) {
        String jsonStr = "";
        if (o != null) {
            try {
                jsonStr = convertToJson(o);
            } catch (Exception e) {
            	e.printStackTrace();
            }

        }
        return jsonStr;
    }
}
