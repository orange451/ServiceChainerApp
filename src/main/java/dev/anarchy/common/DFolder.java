package dev.anarchy.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.anarchy.event.Event;
import dev.anarchy.event.NameChangeEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DFolder implements DFolderElement {
	@JsonProperty("_Name")
	private String name;

	@JsonProperty("ExtensionHandler")
	private List<DFolderElement> children = new ArrayList<>();

	@JsonProperty("_Deletable")
	private boolean deletable = true;

	@JsonIgnore
	private boolean archivable = true;

	@JsonIgnore
	private NameChangeEvent onNameChangeEvent = new NameChangeEvent();

	@JsonIgnore
	private Event onChildAddedEvent = new Event();

	@JsonIgnore
	private Event onChildRemovedEvent = new Event();
	
	@JsonIgnore
	private Event onParentChangeEvent = new Event();
	
	@JsonIgnore
	private DFolder parent;
	
	public DFolder() {
		this.setName("Folder");
		this.onChildAddedEvent.connect((args)->{
			((DFolderElement)args[0]).setParent(this);
		});
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
		this.onNameChangeEvent.fire(name);
	}
	
	public void addChild(DFolderElement chain) {
		if ( this.children.add(chain) ) {
			this.onChildAddedEvent.fire(chain);
		}
	}
	
	public void removeChild(DFolderElement chain) {
		if ( this.children.remove(chain) ) {
			this.onChildRemovedEvent.fire(chain);
		}
	}

	public DFolderElement getChild(String name) {
		for (DFolderElement chain : this.children)
			if ( chain.getName().equals(name) )
				return chain;
		
		return null;
	}

	@JsonIgnore
	public List<DFolderElement> getChildrenUnmodifyable() {
		DFolderElement[] arr = new DFolderElement[this.children.size()];
		for (int i = 0; i < children.size(); i++) {
			arr[i] = children.get(i);
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
		
		for (DFolderElement chain : this.children) {
			chain.setParent(null);
			if ( chain instanceof DFolder )
				((DFolder)chain).delete();
		}
		
		if ( this.getParent() != null )
			this.getParent().removeChild(this);
	}
	
	public void setParent(DFolder parent) {
		DFolder oldParent = this.parent;
		this.parent = parent;
		
		if ( onParentChangeEvent != null )
			onParentChangeEvent.fire(parent, oldParent);
	}
	
	public DFolder getParent() {
		return this.parent;
	}
}
