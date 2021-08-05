package dev.anarchy.common;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.anarchy.common.util.RouteHelper;

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
	
	@JsonProperty("_MockResponse")
	private String mockResponse;

	@JsonProperty("_LastInput")
	private String lastInput;
	
	public DServiceDefinition() {
		super();
		this.setDesination("ServiceDefinition");
	}
	
	public void setExtensionHandlerRouteId(String routeId) {
		this.routeId = routeId;
		this.onChangedEvent.fire();
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
		this.onChangedEvent.fire();
	}

	public String getTemplateContent() {
		return templateContent;
	}

	public void setTemplateContent(String templateContent) {
		this.templateContent = templateContent;
		this.onChangedEvent.fire();
	}

	public List<DDesinationParams> getDestinationParams() {
		return destinationParams;
	}

	public void setDestinationParams(List<DDesinationParams> destinationParams) {
		this.destinationParams = destinationParams;
		this.onChangedEvent.fire();
	}

	public String getAugmentPayload() {
		return augmentPayload;
	}

	public void setAugmentPayload(String augmentPayload) {
		this.augmentPayload = augmentPayload;
		this.onChangedEvent.fire();
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
		this.onChangedEvent.fire();
	}

	@JsonIgnore()
	public String getMockResponse() {
		return mockResponse;
	}

	@JsonIgnore()
	public void setMockResponse(String mockResponse) {
		this.mockResponse = mockResponse;
		this.onChangedEvent.fire();
	}

	@JsonIgnore()
	public String getLastInput() {
		return lastInput;
	}

	@JsonIgnore()
	public void setLastInput(String lastInput) {
		this.lastInput = lastInput;
	}

	@Override
	public Map<String, Object> transform(Map<String, Object> inputPayload) throws Exception {
		// Try to transform the data
		if ( !StringUtils.isEmpty(this.getTemplateContent()) && !StringUtils.isEmpty(this.getTransformationType()) ) {
			return RouteHelper.transform(this, inputPayload);
		} else {
			return inputPayload;
		}
	}
}
