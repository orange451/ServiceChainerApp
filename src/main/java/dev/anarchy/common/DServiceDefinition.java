package dev.anarchy.common;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.runtime.parser.ParseException;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.anarchy.common.util.RouteHelper;
import freemarker.template.TemplateException;

public class DServiceDefinition extends DRouteElement {
	
	@JsonProperty("ExtensionhandlerRouteId")
	private String routeId;
	
	@JsonProperty("TransformationType")
	private String transformationType;
	
	@JsonProperty("TemplateContent")
	private String templateContent;
	
	@JsonProperty("DestinationParams")
	private List<DDestinationParams> destinationParams;
	
	@JsonProperty("AugmentPayload")
	private String augmentPayload;
	
	@JsonProperty("Condition")
	private String condition;
	
	/** Metadata :: Last known user-supplied response from service definition **/
	@JsonProperty("_MockResponse")
	private String mockResponse;

	/** Metadata :: Last known user-supplied json to test against **/
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

	public List<DDestinationParams> getDestinationParams() {
		return destinationParams;
	}

	public void setDestinationParams(List<DDestinationParams> destinationParams) {
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
	public Map<String, Object> transform(Map<String, Object> inputPayload) throws ParseException, IOException, TemplateException {
		// Try to transform the data
		if ( !StringUtils.isEmpty(this.getTemplateContent()) && !StringUtils.isEmpty(this.getTransformationType()) ) {
			return RouteHelper.transform(this, inputPayload);
		} else {
			return inputPayload;
		}
	}
	
	@Override
	public DServiceDefinition clone() {
		DServiceDefinition newInstance = (DServiceDefinition) super.clone();
		
		newInstance.routeId = routeId;
		newInstance.transformationType = transformationType;
		newInstance.templateContent = templateContent;
		newInstance.augmentPayload = augmentPayload;
		newInstance.condition = condition;
		newInstance.mockResponse = mockResponse;
		newInstance.lastInput = lastInput;
		
		for (DDestinationParams params : destinationParams)
			newInstance.destinationParams.add(params.clone());
		
		return newInstance;
	}
}
