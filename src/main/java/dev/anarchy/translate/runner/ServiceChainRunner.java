package dev.anarchy.translate.runner;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import dev.anarchy.common.DRouteElementI;
import dev.anarchy.common.DServiceChain;
import dev.anarchy.common.DServiceDefinition;
import dev.anarchy.common.util.RouteHelper;

public abstract class ServiceChainRunner {
	private DServiceChain serviceChain;
	
	private DRouteElementI currentElement;
	
	public ServiceChainRunner(DServiceChain serviceChain) {
		this.serviceChain = serviceChain;
	}
	
	public Map<String, Object> run(Map<String, Object> inputPayload) {
		if ( inputPayload == null )
			inputPayload = new HashMap<>();
		
		currentElement = this.serviceChain;
		
		while(currentElement != null) {

			// Transform w/ template
			Map<String, Object> output;
			try {
				output = currentElement.transform(inputPayload);
				
				// Perhaps another transform is wanted
				output = onCallRouteElement(currentElement, output);
			} catch (Exception e) {
				return new HashMap<String, Object>() {
					private static final long serialVersionUID = 1L;
					{
						this.put("Node", currentElement.getDestination());
						this.put("Error", e.getMessage());
					}
				};
			}
			
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

	protected abstract Map<String, Object> onCallRouteElement(DRouteElementI routeElement, Map<String, Object> input) throws Exception;
}
