package dev.anarchy.ui.control.servicechain;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import dev.anarchy.ace.Modes;
import dev.anarchy.common.DServiceDefinition;
import dev.anarchy.translate.runner.BasicServiceChainRunner;
import dev.anarchy.translate.util.JSONUtils;
import dev.anarchy.ui.control.RunnerWindowBase;

public class ServiceDefinitionRunner extends RunnerWindowBase {
	
	private DServiceDefinition serviceDefinition;
	
	public ServiceDefinitionRunner(DServiceDefinition serviceDefinition) {
		this.serviceDefinition = serviceDefinition;
		
		code.setText(serviceDefinition.getLastInput());
		code.setMode(Modes.JSON);
		
		getStage().setTitle("Template Runner - " + serviceDefinition.getName());
	}

	@Override
	protected void onTest() {
		try {
			String template = code.getText();
			if ( StringUtils.isEmpty(template) )
				template = "{}";
			
			serviceDefinition.setLastInput(template);
			Map<String, Object> inputPayload = JSONUtils.jsonToMap(template);
			Map<String, Object> result = new BasicServiceChainRunner(null).transformSingle(serviceDefinition, inputPayload, false);
			String prettyResult = JSONUtils.mapToJsonPretty(result);
			addResult("Test Result [ok]", prettyResult);
		} catch (Exception e) {
			e.printStackTrace();
			addResult("Test Result [err]", e.toString());
		}
	}
}
