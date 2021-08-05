package dev.anarchy.ui.control;

import java.util.Map;

import dev.anarchy.ace.Modes;
import dev.anarchy.common.DServiceDefinition;
import dev.anarchy.translate.runner.BasicServiceChainRunner;
import dev.anarchy.translate.util.JSONUtils;

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
			serviceDefinition.setLastInput(code.getText());
			Map<String, Object> inputPayload = JSONUtils.jsonToMap(code.getText());
			Map<String, Object> result = new BasicServiceChainRunner(null).transformSingle(serviceDefinition, inputPayload, false);
			String prettyResult = JSONUtils.mapToJsonPretty(result);
			addResult("Test Result [ok]", prettyResult);
		} catch (Exception e) {
			e.printStackTrace();
			addResult("Test Result [err]", e.toString());
		}
	}
}
