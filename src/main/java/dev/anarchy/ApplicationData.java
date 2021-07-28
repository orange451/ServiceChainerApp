package dev.anarchy;

import dev.anarchy.event.Event;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ApplicationData {
	private ObservableList<DCollection> collections = FXCollections.observableArrayList();
	
	private Event onCollectionAddedEvent = new Event();
	
	private Event onCollectionRemovedEvent = new Event();
	
	public ApplicationData() {
		//
	}
	
	public void loadData() {
		DCollection collection = new DCollection();
		collection.setName("Collection1");
		
		DServiceChain chain = new DServiceChain();
		chain.setName("VVTP07 - DHL Integration");
		collection.addChild(chain);
		
		this.addCollection(collection);
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
