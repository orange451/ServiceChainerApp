package dev.anarchy.common;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DServiceDefinition extends DRouteElement {
	
	@JsonProperty("ExtensionhandlerRouteId")
	private String routeId;
	
	@JsonProperty("TransformationType")
	private String transformationType;
	
	@JsonProperty("TemplateContent")
	private String templateContent;
	
	@JsonProperty("DestinationParams")
	private List<DDesinationParams> destinationParams;
	
	@JsonProperty("AugmentPayload")
	private String augmentPayload;
	
	@JsonProperty("Condition")
	private String condition;
	
	public DServiceDefinition() {
		super();
		this.setDesination("ServiceDefinition");
	}
	
	public void setExtensionHandlerRouteId(String routeId) {
		this.routeId = routeId;
		this.setName(routeId);
	}

	@JsonProperty("ExtensionhandlerRouteId")
	public String getExtensionHandlerRouteId() {
		return this.routeId;
	}

	public String getTransformationType() {
		return transformationType;
	}

	public void setTransformationType(String transformationType) {
		this.transformationType = transformationType;
	}

	public String getTemplateContent() {
		return templateContent;
	}

	public void setTemplateContent(String templateContent) {
		this.templateContent = templateContent;
	}

	public List<DDesinationParams> getDestinationParams() {
		return destinationParams;
	}

	public void setDestinationParams(List<DDesinationParams> destinationParams) {
		this.destinationParams = destinationParams;
	}

	public String getAugmentPayload() {
		return augmentPayload;
	}

	public void setAugmentPayload(String augmentPayload) {
		this.augmentPayload = augmentPayload;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}
}
