package dev.anarchy.translate.type.velocity;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.RuntimeSingleton;
import org.apache.velocity.runtime.parser.ParseException;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import dev.anarchy.common.dto.Document;
import dev.anarchy.translate.util.JSONUtils;
import dev.anarchy.translate.util.TranslateServiceInterface;

public class VelocityTranslateService implements TranslateServiceInterface {

	private static final String DOCUMENT = "document";
	
	private static final String JSONUTILS = "JSONUtils";
	
	private static final Properties VELOCITY_PROPERTIES;
	
	private static final VelocityEngine VELOCITY_ENGINE;
	
	static {
		VELOCITY_PROPERTIES = new Properties();
		VELOCITY_PROPERTIES.put("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.NullLogChute");
		
		VELOCITY_ENGINE = new VelocityEngine();
		VELOCITY_ENGINE.init(VELOCITY_PROPERTIES);
	}

	@SuppressWarnings("unchecked")
	public String translate(String templateContent, String dataModel) throws ParseException {
        
        // Convert datamodel into json
        JSONObject jsonModel = (JSONObject) JSONValue.parse(dataModel);
        
        // Convert DataModel into VelocityContext
        VelocityContext context = new VelocityContext();
        context.put(JSONUTILS, new JSONUtils());
        context.put(DOCUMENT, new Document(jsonModel));
        context.put(Integer.class.getSimpleName(), Integer.class);
        context.put(Double.class.getSimpleName(), Double.class);
        context.put(Boolean.class.getSimpleName(), Boolean.class);
        context.put(Float.class.getSimpleName(), Float.class);
        context.put(Math.class.getSimpleName(), Math.class);

        // Process template
        StringWriter stringWriter = new StringWriter();
        StringReader stringReader = new StringReader(templateContent);
        RuntimeServices runtimeServices = RuntimeSingleton.getRuntimeServices();
        SimpleNode node = runtimeServices.parse(stringReader, "StringTemplate");
        Template template = new Template();
        template.setRuntimeServices(runtimeServices);
        template.setData(node);
        template.initDocument();
        template.merge(context, stringWriter);
        
        return stringWriter.toString();
	}

}
