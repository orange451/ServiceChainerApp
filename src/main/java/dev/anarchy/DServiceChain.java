package dev.anarchy;

import dev.anarchy.event.NameChangeEvent;

public class DServiceChain {
	private String name;
	
	private NameChangeEvent onNameChangeEvent;
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
		
		if ( onNameChangeEvent != null ) {
			onNameChangeEvent.fire(name);
		}
	}
	
	public NameChangeEvent getOnNameChangeEvent() {
		return this.onNameChangeEvent;
	}
}
