package dev.anarchy.translate.util;

import dev.anarchy.translate.type.freemarker.FreemarkerTranslateService;
import dev.anarchy.translate.type.velocity.VelocityTranslateService;

public enum TranslateType {
	VELOCITY(VelocityTranslateService.class),
	FREEMARKER(FreemarkerTranslateService.class);
	
	private Class<?> clazz;

	TranslateType(Class<?> clazz) {
		this.clazz = clazz;
	}
	
	public Class<?> getTranslateClass() {
		return this.clazz;
	}
	
	public static TranslateType match(String name) {
		for (TranslateType type : TranslateType.values()) {
			if ( type.toString().equalsIgnoreCase(name) ) {
				return type;
			}
		}
		
		return null;
	}
}
