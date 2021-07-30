package dev.anarchy;

import dev.anarchy.event.Event;
import dev.anarchy.event.NameChangeEvent;

public class DServiceChain {
	private String name;
	
	private NameChangeEvent onNameChangeEvent;
	
	private Event onParentChangeEvent;
	
	private DCollection parent;
	
	public DServiceChain() {
		this.setName("New Service Chain");
		this.onNameChangeEvent = new NameChangeEvent();
		this.onParentChangeEvent = new Event();
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
		
		if ( onNameChangeEvent != null )
			onNameChangeEvent.fire(name);
	}
	
	public NameChangeEvent getOnNameChangeEvent() {
		return this.onNameChangeEvent;
	}
	
	public Event getOnParentChangeEvent() {
		return this.onParentChangeEvent;
	}
	
	public void setParent(DCollection parent) {
		DCollection oldParent = this.parent;
		this.parent = parent;
		
		if ( onParentChangeEvent != null )
			onParentChangeEvent.fire(parent, oldParent);
	}
	
	public DCollection getParent() {
		return this.parent;
	}
}
