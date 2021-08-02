package dev.anarchy.translate.runner;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import dev.anarchy.common.DRouteElementI;
import dev.anarchy.common.DServiceChain;
import dev.anarchy.common.DServiceDefinition;
import dev.anarchy.common.util.RouteHelper;
import dev.anarchy.translate.util.JSONUtils;

public abstract class ServiceChainRunner {
	private DServiceChain serviceChain;
	
	private DRouteElementI currentElement;
	
	public ServiceChainRunner(DServiceChain serviceChain) {
		this.serviceChain = serviceChain;
	}
	
	public Map<String, Object> run(Map<String, Object> inputPayload) {
		currentElement = this.serviceChain;
		
		while(currentElement != null) {

			// Transform w/ template
			Map<String, Object> output = currentElement.transform(inputPayload);
			
			// Perhaps another transform is wanted
			output = onCallRouteElement(currentElement, output);
			
			// Augment maybe
			if ( currentElement instanceof DServiceDefinition && !StringUtils.isEmpty(((DServiceDefinition)currentElement).getAugmentPayload()) ) {
				inputPayload.put(((DServiceDefinition)currentElement).getAugmentPayload(), output);
			} else {
				inputPayload = output;
			}
			
			currentElement = RouteHelper.getLinkedTo(serviceChain.getRoutesUnmodifyable(), currentElement);
		}
		
		return inputPayload;
	}

	protected abstract Map<String, Object> onCallRouteElement(DRouteElementI routeElement, Map<String, Object> input);
}
