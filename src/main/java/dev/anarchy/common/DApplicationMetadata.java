package dev.anarchy.common;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DApplicationMetadata {
	
	@JsonProperty("Collections")
	private List<String> collections = new ArrayList<>();
	
	@JsonIgnore
	public List<String> getCollections() {
		return this.collections;
	}
	
	@JsonIgnore
	public void setCollections(List<String> collections) {
		this.collections = collections;
	}
}
