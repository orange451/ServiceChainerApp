package dev.anarchy.common;

import java.io.IOException;
import java.util.Map;

import org.apache.velocity.runtime.parser.ParseException;

import dev.anarchy.event.Event;
import freemarker.template.TemplateException;

public interface DRouteElementI {
	public void setSize(double width, double height);

	public void setPosition(double x, double y);
	
	public double getWidth();
	
	public double getHeight();
	
	public double getX();
	
	public double getY();

	public void setColor(String hexString);
	
	public String getColor();
	
	public String getName();
	
	public void setName(String name);
	
	public Event getOnChangedEvent();
	
	public String getSource();
	
	public String getSourceId();
	
	public String getDestination();
	
	public String getDestinationId();

	public Map<String, Object> transform(Map<String, Object> inputPayload) throws ParseException, IOException, TemplateException;
}
