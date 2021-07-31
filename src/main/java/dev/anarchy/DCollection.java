package dev.anarchy;

import com.fasterxml.jackson.annotation.JsonProperty;

import dev.anarchy.ui.AnarchyApp;

public class DCollection extends DFolder {
	@JsonProperty("_IsCollection")
	private final boolean isCollection = true;
	
	public DCollection() {
		super();
	}

	public void delete() {
		super.delete();
		
		AnarchyApp.get().getData().removeCollection(this);
	}
}
