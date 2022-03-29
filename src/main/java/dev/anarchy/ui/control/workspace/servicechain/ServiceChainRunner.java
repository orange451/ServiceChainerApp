package dev.anarchy.ui.control.workspace.servicechain;

import java.util.List;
import java.util.Map;

import dev.anarchy.common.DServiceChain;
import dev.anarchy.translate.runner.BasicServiceChainRunner;
import dev.anarchy.translate.util.JSONUtils;
import dev.anarchy.ui.control.RunnerWindowBase;
import dev.anarchy.ace.Modes;

public class ServiceChainRunner extends RunnerWindowBase {
	
	private DServiceChain serviceChain;
	
	public ServiceChainRunner(DServiceChain serviceChain) {
		this.serviceChain = serviceChain;
		
		code.setText(serviceChain.getLastInput());
		code.setMode(Modes.JSON);
		
		getStage().setTitle("Service Chain Runner - " + serviceChain.getName());
	}

	@Override
	protected void onTest() {
		try {
			serviceChain.setLastInput(code.getText());
			Map<String, Object> inputPayload = JSONUtils.jsonToMap(code.getText());
			Map<String, Object> result = new BasicServiceChainRunner(serviceChain).run(inputPayload);
			String prettyResult = JSONUtils.mapToJsonPretty(result);
			addResult("Test Result [ok]", prettyResult);
			animateLong("success");
		} catch (Exception e) {
			e.printStackTrace();
			addResult("Test Result [err]", e.toString());
			animateFlash("danger");
		}
	}
}
