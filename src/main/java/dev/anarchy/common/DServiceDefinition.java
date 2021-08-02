package dev.anarchy.common;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.anarchy.common.util.RouteHelper;
import dev.anarchy.translate.util.TranslateMapService;
import dev.anarchy.translate.util.TranslateType;

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
	
	public DServiceDefinition() {
		super();
		this.setDesination("ServiceDefinition");
		this.setMockResponse("{}");
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

	public String getMockResponse() {
		return mockResponse;
	}

	public void setMockResponse(String mockResponse) {
		this.mockResponse = mockResponse;
	}

	@Override
	public Map<String, Object> transform(Map<String, Object> inputPayload) {
		// Try to transform the data
		if ( !StringUtils.isEmpty(this.getTemplateContent()) && !StringUtils.isEmpty(this.getTransformationType()) ) {
			return RouteHelper.transform(this, inputPayload);
		} else {
			return inputPayload;
		}
	}
}
