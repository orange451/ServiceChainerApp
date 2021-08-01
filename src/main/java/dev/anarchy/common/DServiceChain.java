package dev.anarchy.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.anarchy.event.Event;
import dev.anarchy.event.NameChangeEvent;
import dev.anarchy.ui.util.ColorHelper;
import javafx.scene.paint.Color;

public class DServiceChain implements DFolderElement,DRouteElementI {
	
	@JsonProperty("ExtensionhandlerId")
	private String handlerId;

	@JsonProperty("ExtensionhandlerRoute")
	private List<DRouteElement> routes = new ArrayList<>();

	@JsonProperty("RegisteredExtensionPoints")
	private List<DExtensionPoint> extensionPoints = new ArrayList<>();

	@JsonProperty("Produces")
	private String produces = "JSON";
	
	@JsonProperty("_Name")
	private String name;
	
	@JsonProperty("_X")
	private double x;
	
	@JsonProperty("_Y")
	private double y;

	@JsonProperty("_Width")
	private double width;

	@JsonProperty("_Height")
	private double height;

	@JsonProperty("_Color")
	private String color = ColorHelper.toHexString(Color.color(0.196, 0.6274, 0.235));
	
	@JsonIgnore()
	private Event onChangedEvent = new Event();
	
	@JsonIgnore
	private Event onNameChangeEvent = new NameChangeEvent();
	
	@JsonIgnore
	private Event onHandlerIdChangeEvent = new NameChangeEvent();
	
	@JsonIgnore
	private Event onParentChangeEvent = new Event();
	
	@JsonIgnore
	private Event onRouteAddedEvent = new Event();
	
	@JsonIgnore
	private Event onRouteRemovedEvent = new Event();
	
	@JsonIgnore
	private DFolder parent;
	
	public DServiceChain() {
		this.setName("New Service Chain");
		this.setHandlerId("Entry Point");
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
		
		if ( onNameChangeEvent != null )
			onNameChangeEvent.fire(name);
	}
	
	public Event getOnNameChangeEvent() {
		return this.onNameChangeEvent;
	}
	
	public Event getOnHandlerIdChangeEvent() {
		return this.onHandlerIdChangeEvent;
	}
	
	public Event getOnParentChangeEvent() {
		return this.onParentChangeEvent;
	}
	
	public Event getOnRouteAddedEvent() {
		return this.onRouteAddedEvent;
	}
	
	public Event getOnRouteRemovedEvent() {
		return this.onRouteRemovedEvent;
	}
	
	public void setParent(DFolder parent) {
		DFolder oldParent = this.parent;
		this.parent = parent;
		
		if ( onParentChangeEvent != null )
			onParentChangeEvent.fire(parent, oldParent);
	}
	
	public void addRoute(DRouteElement chain) {
		if ( this.routes.add(chain) ) {
			this.onRouteAddedEvent.fire(chain);
		}
	}
	
	public void removeRoute(DRouteElement chain) {
		if ( this.routes.remove(chain) ) {
			this.onRouteRemovedEvent.fire(chain);
		}
	}
	
	public String getHandlerId() {
		return this.handlerId;
	}
	
	public void setHandlerId(String handlerId) {
		this.handlerId = handlerId;
		
		if ( onHandlerIdChangeEvent != null )
			onHandlerIdChangeEvent.fire(handlerId);
	}

	@JsonIgnore
	public List<DRouteElement> getRoutesUnmodifyable() {
		DRouteElement[] arr = new DRouteElement[this.routes.size()];
		for (int i = 0; i < routes.size(); i++) {
			arr[i] = routes.get(i);
		}
		return Arrays.asList(arr);
	}

	@JsonIgnore
	public DFolder getParent() {
		return this.parent;
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

	@Override
	public String getSource() {
		return "ON_EVENT";
	}

	@Override
	public String getSourceId() {
		return "ON_EVENT";
	}

	@Override
	public String getDestination() {
		return "ON_EVENT";
	}

	@Override
	public String getDestinationId() {
		return "ON_EVENT";
	}
}
