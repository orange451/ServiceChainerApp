package dev.anarchy.common;

import java.util.Map;

import dev.anarchy.event.Event;

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

	public Map<String, Object> translate(Map<String, Object> inputPayload);
}
