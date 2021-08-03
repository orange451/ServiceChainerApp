package dev.anarchy.translate.type.freemarker;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import dev.anarchy.translate.util.TranslateServiceInterface;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class FreemarkerTranslateService implements TranslateServiceInterface {

	private static final String DOCUMENT = "document";
	
	@SuppressWarnings("deprecation")
	public String translate(String templateContent, String dataModel) throws IOException, TemplateException {
        // Convert datamodel into json
        JSONObject jsonModel = (JSONObject) JSONValue.parse(dataModel);
        
        // Convert template content to template
		Template template = new Template("Test", new StringReader(templateContent));
		
		// Add all keys to input map
        Map<String, Object> inputMap = new HashMap<>();
        inputMap.put(DOCUMENT, jsonModel);
        
        // Process Templtae
        StringWriter stringWriter = new StringWriter();
        template.process(inputMap, stringWriter);
    	return stringWriter.toString();
	}
}
