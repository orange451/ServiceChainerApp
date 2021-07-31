package dev.anarchy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import dev.anarchy.event.Event;

@JsonTypeInfo(use=Id.DEDUCTION)
@JsonSubTypes({@Type(DServiceDefinition.class)})
public class DRouteElement implements DRouteElementBase {
	
	@JsonProperty("Source")
	private String source;
	
	@JsonProperty("SourceId")
	private String sourceId;
	
	@JsonProperty("Destination")
	private String destination;
	
	@JsonProperty("DestinationId")
	private String destinationId;
	
	@JsonProperty("IsSync")
	private String isSync;
	
	@JsonProperty("_X")
	private double x;
	
	@JsonProperty("_Y")
	private double y;

	@JsonProperty("_Width")
	private double width;

	@JsonProperty("_Height")
	private double height;

	@JsonProperty("_Name")
	private String name;

	@JsonProperty("_Color")
	private String color;
	
	@JsonIgnore()
	private Event onChangedEvent = new Event();
	
	public void setName(String name) {
		this.name = name;
		this.onChangedEvent.fire();
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setSize(double width, double height) {
		this.width = width;
		this.height = height;
		this.onChangedEvent.fire();
	}

	public void setPosition(double x, double y) {
		this.x = x;
		this.y = y;
		this.onChangedEvent.fire();
	}
	
	public double getWidth() {
		return this.width;
	}
	
	public double getHeight() {
		return this.height;
	}
	
	public double getX() {
		return this.x;
	}
	
	public double getY() {
		return this.y;
	}

	public void setColor(String hexString) {
		this.color = hexString;
		this.onChangedEvent.fire();
	}
	
	public String getColor() {
		return this.color;
	}

	public Event getOnChangedEvent() {
		return this.onChangedEvent;
	}
}
