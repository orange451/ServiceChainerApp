package dev.anarchy;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DServiceDefinition extends DRouteElement {
	
	@JsonProperty("ExtensionhandlerRouteId")
	private String routeId;
	
	@JsonProperty("TransformationType")
	private String transformationType;
	
	@JsonProperty("TemplateContent")
	private String templateContent;
	
	public void setExtensionHandlerRouteId(String routeId) {
		this.routeId = routeId;
		this.setName(routeId);
	}

	@JsonProperty("ExtensionhandlerRouteId")
	public String getExtensionHandlerRouteId() {
		return this.routeId;
	}
}
