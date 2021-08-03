package dev.anarchy.translate.util;

import java.io.IOException;

import org.apache.velocity.runtime.parser.ParseException;

import dev.anarchy.translate.type.freemarker.FreemarkerTranslateService;
import dev.anarchy.translate.type.velocity.VelocityTranslateService;
import freemarker.template.TemplateException;

public class TranslateMapService {
    private VelocityTranslateService velocityTranslateService;
    private FreemarkerTranslateService freemarkerTranslateService;

	public String translate(TranslateType type, String template, String dataModel) throws ParseException, IOException, TemplateException {
		if ( velocityTranslateService == null )
			velocityTranslateService = new VelocityTranslateService();
		
		if ( freemarkerTranslateService == null )
			freemarkerTranslateService = new FreemarkerTranslateService();
		
		switch(type) {
			case VELOCITY: {
				return velocityTranslateService.translate(template, dataModel);
			}
			case FREEMARKER: {
				return freemarkerTranslateService.translate(template, dataModel);
			}
			default: {
				return null;
			}
		}
	}

}
