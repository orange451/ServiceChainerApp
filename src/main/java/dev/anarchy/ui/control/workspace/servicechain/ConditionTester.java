package dev.anarchy.ui.control.workspace.servicechain;

import org.apache.commons.lang3.StringUtils;

import dev.anarchy.ace.Modes;
import dev.anarchy.common.DConditionElement;
import dev.anarchy.common.condition.Condition;
import dev.anarchy.translate.util.JSONUtils;
import dev.anarchy.ui.control.RunnerWindowBase;

public class ConditionTester extends RunnerWindowBase {
	
	private DConditionElement condition;
	
	public ConditionTester(DConditionElement condition) {
		this.condition = condition;
		
		code.setText(condition.getConditionMeta().getLastConditionPayload());
		code.setMode(Modes.JSON);
		
		getStage().setTitle("Condition Tester");
	}

	@Override
	protected void onTest() {
		try {
			String inputJson = code.getText();
			if ( StringUtils.isEmpty(inputJson) )
				inputJson = "{}";
			
			condition.getConditionMeta().setLastConditionPayload(inputJson);
			boolean result = new Condition(condition.getConditionMeta().getCondition()).evaluate(JSONUtils.jsonToMap(inputJson));
			
			String response = "{\"Status\": \""+(result ? "Passed" : "Failed")+"\"}";
			addResult("Condition Result [ok]", JSONUtils.mapToJsonPretty(JSONUtils.jsonToMap(response)));
		} catch (Exception e) {
			e.printStackTrace();
			addResult("Condition Result [err]", e.toString());
		}
	}
}
