package dev.anarchy.common;

import com.fasterxml.jackson.annotation.JsonProperty;

import dev.anarchy.ui.ServiceChainerApp;

public class DCollection extends DFolder {
	@JsonProperty("_IsCollection")
	private final boolean isCollection = true;
	
	public DCollection() {
		super();
	}

	public void delete() {
		super.delete();
		
		ServiceChainerApp.get().getData().removeCollection(this);
	}
}
