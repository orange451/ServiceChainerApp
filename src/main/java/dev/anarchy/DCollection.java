package dev.anarchy;

import java.util.Arrays;
import java.util.List;

import dev.anarchy.event.Event;
import dev.anarchy.event.NameChangeEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DCollection {
	private String name;
	
	private ObservableList<DServiceChain> serviceChains = FXCollections.observableArrayList();
	
	private NameChangeEvent onNameChangeEvent = new NameChangeEvent();
	
	private Event onChildAddedEvent = new Event();
	
	private Event onChildRemovedEvent = new Event();
	
	public DCollection() {
		
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
		this.onNameChangeEvent.fire(name);
	}
	
	public void addChild(DServiceChain chain) {
		if ( this.serviceChains.add(chain) ) {
			this.onChildAddedEvent.fire(chain);
		}
	}
	
	public void removeChild(DServiceChain chain) {
		if ( this.serviceChains.remove(chain) ) {
			this.onChildRemovedEvent.fire(chain);
		}
	}
	
	public List<DServiceChain> getChildrenUnmodifyable() {
		DServiceChain[] arr = new DServiceChain[this.serviceChains.size()];
		for (int i = 0; i < serviceChains.size(); i++) {
			arr[i] = serviceChains.get(i);
		}
		return Arrays.asList(arr);
	}
	
	public Event getOnChildAddedEvent() {
		return this.onChildAddedEvent;
	}
	
	public Event getOnChildRemovedEvent() {
		return this.onChildRemovedEvent;
	}
	
	public NameChangeEvent getOnNameChangeEvent() {
		return this.onNameChangeEvent;
	}
}
