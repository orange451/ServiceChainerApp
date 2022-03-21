package dev.anarchy.common;

import java.io.IOException;
import java.util.Map;

import org.apache.velocity.runtime.parser.ParseException;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.anarchy.common.condition.ConditionMeta;
import freemarker.template.TemplateException;

public class DConditionElement extends DRouteElement {
	
	@JsonProperty("_ConditionMeta")
	private ConditionMeta conditionMeta;
	
	public DConditionElement() {
		this.conditionMeta = new ConditionMeta();
	}

	@Override
	public Map<String, Object> transform(Map<String, Object> inputPayload)
			throws ParseException, IOException, TemplateException {
		return null;
	}

	@JsonIgnore()
	public ConditionMeta getConditionMeta() {
		return conditionMeta;
	}

	@JsonIgnore()
	public void setConditionMeta(ConditionMeta conditionMeta) {
		this.conditionMeta = conditionMeta;
		this.onChangedEvent.fire();
	}
}
