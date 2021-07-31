package dev.anarchy;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.anarchy.event.Event;

public class ApplicationData {
	@JsonIgnore
	public final DCollection UNORGANIZED = new DCollection();
	
	@JsonProperty("Collections")
	private List<DCollection> collections = new ArrayList<>();

	@JsonIgnore
	private Event onCollectionAddedEvent = new Event();
	
	@JsonIgnore
	private Event onCollectionRemovedEvent = new Event();
	
	public ApplicationData() {
		UNORGANIZED.setDeletable(false);
		UNORGANIZED.setArchivable(false);
		UNORGANIZED.setName("Unorganized");
	}
	
	public void load() {
		if ( this.collections.size() == 0 )
			this.addCollection(UNORGANIZED);
	}
	
	public DServiceChain newServiceChain(DFolder collection) {
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
		
		save();
		
		return chain;
	}

	public DFolder newFolder(DFolder internal) {
		DFolder folder = new DFolder();
		String baseName = folder.getName();
		
		int index = 0;
		String checkName = baseName;
		while(internal.getChild(checkName) != null) {
			index += 1;
			checkName = baseName + " (" + index + ")"; 
		}
		
		folder.setName(checkName);
		internal.addChild(folder);
		
		save();
		
		return folder;
	}

	@JsonIgnore
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
		
		save();
	}
	
	public void removeCollection(DCollection collection) {
		if ( collections.remove(collection) ) {
			onCollectionRemovedEvent.fire(collection);
		}
		
		save();
	}
	
	public Event getOnCollectionAddedEvent() {
		return this.onCollectionAddedEvent;
	}
	
	public Event getOnCollectionRemovedEvent() {
		return this.onCollectionRemovedEvent;
	}
	
	public void save() {
		String json = this.serializeJSON();
		System.out.println(json);
		
		try {
		    BufferedWriter writer = new BufferedWriter(new FileWriter("APPDATA.json"));
		    writer.write(json);
		    writer.close();
		} catch(Exception e) {
			//
		}
	}
	
	private String serializeJSON() {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		return "{}";
	}
}
