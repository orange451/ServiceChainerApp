package dev.anarchy.translate.runner;

import java.util.Map;

import dev.anarchy.common.DRouteElementI;
import dev.anarchy.common.DServiceChain;

public class BasicServiceChainRunner extends ServiceChainRunner {

	public BasicServiceChainRunner(DServiceChain serviceChain) {
		super(serviceChain);
		this.setCanUseMocks(true);
	}

	@Override
	protected Map<String, Object> onInvokeRouteElement(DRouteElementI routeElement, Map<String, Object> input) {
		return input;
	}

}
