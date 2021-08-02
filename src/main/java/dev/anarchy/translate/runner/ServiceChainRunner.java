package dev.anarchy.translate.runner;

import java.util.Map;

import dev.anarchy.common.DRouteElementI;
import dev.anarchy.common.DServiceChain;
import dev.anarchy.common.DServiceDefinition;
import dev.anarchy.common.util.RouteHelper;

public class ServiceChainRunner {
	private DServiceChain serviceChain;
	
	private DRouteElementI currentElement;
	
	public ServiceChainRunner(DServiceChain serviceChain) {
		this.serviceChain = serviceChain;
	}
	
	public Map<String, Object> run(Map<String, Object> inputPayload) {
		currentElement = this.serviceChain;
		
		while(currentElement != null) {
			Map<String, Object> output = currentElement.translate(inputPayload);
			if ( currentElement instanceof DServiceDefinition && !isEmpty(((DServiceDefinition)currentElement).getAugmentPayload()) ) {
				inputPayload.put(((DServiceDefinition)currentElement).getAugmentPayload(), output);
			} else {
				inputPayload = output;
			}
			
			currentElement = RouteHelper.getLinkedTo(serviceChain.getRoutesUnmodifyable(), currentElement);
		}
		
		return inputPayload;
	}

	private boolean isEmpty(String string) {
		return string == null || string.length() == 0;
	}
}
