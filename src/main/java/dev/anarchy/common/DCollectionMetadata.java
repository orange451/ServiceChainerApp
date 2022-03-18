package dev.anarchy.common;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DCollectionMetadata {
	
	@JsonProperty("Name")
	private String name;
	
	@JsonProperty("Children")
	private List<DCollectionMetadata> children = new ArrayList<>();
	
	@JsonProperty("ExtensionHandlers")
	private List<String> extensionHandlers = new ArrayList<>();

	@JsonIgnore
	public String getName() {
		return this.name;
	}

	@JsonIgnore
	public void setName(String name) {
		this.name = name;
	}

	@JsonIgnore
	public List<DCollectionMetadata> getChildren() {
		return this.children;
	}
	
	@JsonIgnore
	public void setChildren(List<DCollectionMetadata> children) {
		this.children = children;
	}
	
	@JsonIgnore
	public List<String> getExtensionHandlers() {
		return this.extensionHandlers;
	}
	
	@JsonIgnore
	public void setExtensionHandlers(List<String> extensionHandlers) {
		this.extensionHandlers = extensionHandlers;
	}
}
