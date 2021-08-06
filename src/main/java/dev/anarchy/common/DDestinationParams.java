package dev.anarchy.common;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DDestinationParams {

	@JsonProperty("ParameterName")
	private String parameterName;
	
	@JsonProperty("ParameterValue")
	private String parameterValue;

	public String getParameterName() {
		return parameterName;
	}

	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

	public String getParameterValue() {
		return parameterValue;
	}

	public void setParameterValue(String parameterValue) {
		this.parameterValue = parameterValue;
	}
	
	public DDestinationParams clone() {
		DDestinationParams obj = new DDestinationParams();
		obj.parameterName = parameterName;
		obj.parameterValue = parameterValue;
		
		return obj;
	}
}
