package dev.anarchy;

import dev.anarchy.ui.AnarchyApp;

public class DCollection extends DFolder {
	
	public DCollection() {
		super();
	}

	public void delete() {
		super.delete();
		
		AnarchyApp.get().getData().removeCollection(this);
	}
}
