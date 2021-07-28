package dev.anarchy.translate.type.velocity;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.RuntimeSingleton;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import dev.anarchy.common.dto.Document;
import dev.anarchy.translate.util.JSONUtils;
import dev.anarchy.translate.util.TranslateServiceInterface;

public class VelocityTranslateService implements TranslateServiceInterface {

	private static final String DOCUMENT = "document";
	
	private static final String JSONUTILS = "JSONUtils";

	@SuppressWarnings("unchecked")
	public String translate(String templateContent, String dataModel) {
    	try {
    		Properties properties = new Properties();
    		properties.put("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.NullLogChute");
    		
	    	VelocityEngine ve = new VelocityEngine();
	    	ve.init(properties);
	        
	        // Convert datamodel into json
	        JSONObject jsonModel = (JSONObject) JSONValue.parse(dataModel);
	        
	        // Convert DataModel into VelocityContext
	        VelocityContext context = new VelocityContext();
	        context.put(JSONUTILS, new JSONUtils());
	        context.put(DOCUMENT, new Document(jsonModel));

	        // Process template
	        StringWriter w = new StringWriter();
	        RuntimeServices runtimeServices = RuntimeSingleton.getRuntimeServices();
	        StringReader reader = new StringReader(templateContent);
	        SimpleNode node = runtimeServices.parse(reader, "StringTemplate");
	        Template template = new Template();
	        template.setRuntimeServices(runtimeServices);
	        template.setData(node);
	        template.initDocument();
	        template.merge(context, w);
	        
	        return w.toString();
    	} catch(Exception e) {
    		e.printStackTrace();
    		return null;
    	}
	}

}
