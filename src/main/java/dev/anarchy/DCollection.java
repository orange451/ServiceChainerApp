package dev.anarchy;

import java.util.Arrays;
import java.util.List;

import dev.anarchy.event.Event;
import dev.anarchy.event.NameChangeEvent;
import dev.anarchy.ui.AnarchyApp;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DCollection {
	private String name;
	
	private boolean deletable = true;
	
	private boolean archivable = true;
	
	private ObservableList<DServiceChain> serviceChains = FXCollections.observableArrayList();
	
	private NameChangeEvent onNameChangeEvent = new NameChangeEvent();
	
	private Event onChildAddedEvent = new Event();
	
	private Event onChildRemovedEvent = new Event();
	
	public DCollection() {
		this.onChildAddedEvent.connect((args)->{
			((DServiceChain)args[0]).setParent(this);
		});
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

	public DServiceChain getChild(String name) {
		for (DServiceChain chain : this.serviceChains)
			if ( chain.getName().equals(name) )
				return chain;
		
		return null;
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
	
	public boolean isArchivable() {
		return this.archivable;
	}
	
	public void setArchivable(boolean archivable) {
		this.archivable = archivable;
	}
	
	public boolean isDeletable() {
		return this.deletable;
	}
	
	public void setDeletable(boolean deletable) {
		this.deletable = deletable;
	}

	public void delete() {
		if ( !this.isDeletable() )
			return;
		
		for (DServiceChain chain : this.serviceChains) {
			chain.setParent(null);
		}
		
		AnarchyApp.get().getData().removeCollection(this);
	}
}
