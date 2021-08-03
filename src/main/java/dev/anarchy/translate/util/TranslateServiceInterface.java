package dev.anarchy.translate.util;

import java.io.IOException;

import org.apache.velocity.runtime.parser.ParseException;

import freemarker.template.TemplateException;

public interface TranslateServiceInterface {
	public String translate(String template, String dataModel) throws ParseException, IOException, TemplateException;
}
