package dev.anarchy.translate.type.freemarker;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import dev.anarchy.translate.util.JSONUtils;
import dev.anarchy.translate.util.TranslateServiceInterface;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class FreemarkerTranslateService implements TranslateServiceInterface {

	private static final String DOCUMENT = "document";
	
	private static final String JSONUTILS = "JSONUtils";
	
	private static final String TEMPLATE_NAME = "Template";
	
	private static final Map<String, Object> inputMap;
	
	static {
		inputMap = new HashMap<>();
        inputMap.put(JSONUTILS, new JSONUtils());
	}
	
	@SuppressWarnings("deprecation")
	public String translate(String templateContent, String dataModel) throws IOException, TemplateException {
		
        // Convert datamodel into json
        JSONObject jsonModel = (JSONObject) JSONValue.parse(dataModel);
        inputMap.put(DOCUMENT, jsonModel);
        
        // Convert template content to template and process
        StringWriter stringWriter = new StringWriter();
		Template template = new Template(TEMPLATE_NAME, new StringReader(templateContent));
        template.process(inputMap, stringWriter);
        
    	return stringWriter.toString();
	}
}
