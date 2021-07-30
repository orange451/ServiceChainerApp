package dev.anarchy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dev.anarchy.event.Event;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ApplicationData {
	public final DCollection UNORGANIZED = new DCollection();

	private ObservableList<DCollection> collections = FXCollections.observableArrayList();
	
	private Event onCollectionAddedEvent = new Event();
	
	private Event onCollectionRemovedEvent = new Event();
	
	public ApplicationData() {
		UNORGANIZED.setDeletable(false);
		UNORGANIZED.setArchivable(false);
		UNORGANIZED.setName("Unorganized");
	}
	
	public void loadData() {
		DServiceChain chain = new DServiceChain();
		chain.setName("VVTP07 - DHL Integration");
		UNORGANIZED.addChild(chain);
		
		this.addCollection(UNORGANIZED);
	}
	
	public DServiceChain newServiceChain(DCollection collection) {
		DServiceChain chain = new DServiceChain();
		String baseName = chain.getName();
		
		int index = 0;
		String checkName = baseName;
		while(collection.getChild(checkName) != null) {
			index += 1;
			checkName = baseName + " (" + index + ")"; 
		}
		
		chain.setName(checkName);
		collection.addChild(chain);
		
		return chain;
	}

	public List<DCollection> getCollectionsUnmodifyable() {
		DCollection[] arr = new DCollection[this.collections.size()];
		for (int i = 0; i < collections.size(); i++) {
			arr[i] = collections.get(i);
		}
		return Arrays.asList(arr);
	}
	
	public void addCollection(DCollection collection) {
		if ( collections.add(collection) ) {
			onCollectionAddedEvent.fire(collection);
		}
	}
	
	public void removeCollection(DCollection collection) {
		if ( collections.remove(collection) ) {
			onCollectionRemovedEvent.fire(collection);
		}
	}
	
	public Event getOnCollectionAddedEvent() {
		return this.onCollectionAddedEvent;
	}
	
	public Event getOnCollectionRemovedEvent() {
		return this.onCollectionRemovedEvent;
	}
}
