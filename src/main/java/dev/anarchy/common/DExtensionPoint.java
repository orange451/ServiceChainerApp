package dev.anarchy.common;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DExtensionPoint {
	@JsonProperty("ExtensionPointId")
	private String extensionPointId;

	@JsonProperty("EntryCondition")
	private String entryCondition;

	public String getExtensionPointId() {
		return extensionPointId;
	}

	public void setExtensionPointId(String extensionPointId) {
		this.extensionPointId = extensionPointId;
	}

	public String getEntryCondition() {
		return entryCondition;
	}

	public void setEntryCondition(String entryCondition) {
		this.entryCondition = entryCondition;
	}
}
