package dev.anarchy.translate.runner;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import dev.anarchy.common.DRouteElementI;
import dev.anarchy.common.DServiceChain;
import dev.anarchy.common.DServiceDefinition;
import dev.anarchy.translate.util.JSONUtils;

public class BasicServiceChainRunner extends ServiceChainRunner{

	public BasicServiceChainRunner(DServiceChain serviceChain) {
		super(serviceChain);
	}

	@Override
	protected Map<String, Object> onCallRouteElement(DRouteElementI routeElement, Map<String, Object> input) throws Exception {
		if ( routeElement instanceof DServiceDefinition ) {
			DServiceDefinition serviceDef = (DServiceDefinition)routeElement;
			if ( !StringUtils.isEmpty(serviceDef.getMockResponse()) ) {
				Map<String, Object> output = JSONUtils.jsonToMap(serviceDef.getMockResponse());
				if ( output != null )
					return output;
			}
		}
		
		return input;
	}

}
