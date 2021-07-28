package dev.anarchy.translate.util;

import dev.anarchy.translate.type.freemarker.FreemarkerTranslateService;
import dev.anarchy.translate.type.velocity.VelocityTranslateService;

public class TranslateMapService {
    private VelocityTranslateService velocityTranslateService;
    private FreemarkerTranslateService freemarkerTranslateService;

	public String translate(TranslateType type, String template, String dataModel) {
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
