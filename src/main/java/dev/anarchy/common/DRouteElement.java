package dev.anarchy.common;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import dev.anarchy.event.Event;

@JsonTypeInfo(use=Id.DEDUCTION)
@JsonSubTypes({@Type(DServiceDefinition.class)})
public abstract class DRouteElement implements DRouteElementI {
	
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
	
	public DRouteElement() {
		this.setDesination("RouteElement");
		this.setDesinationId(UUID.randomUUID().toString());
	}
	
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

	@JsonIgnore()
	public void setSource(String source) {
		this.source = source;
	}

	@JsonIgnore()
	@Override
	public String getSource() {
		return this.source;
	}

	@JsonIgnore()
	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	@JsonIgnore()
	@Override
	public String getSourceId() {
		return this.sourceId;
	}

	@JsonIgnore()
	public void setDesination(String destination) {
		this.destination = destination;
		this.setName(destination);
	}

	@JsonIgnore()
	@Override
	public String getDestination() {
		return this.destination;
	}

	@JsonIgnore()
	private void setDesinationId(String destinationId) {
		this.destinationId = destinationId;
	}

	@JsonIgnore()
	@Override
	public String getDestinationId() {
		return this.destinationId;
	}
}
