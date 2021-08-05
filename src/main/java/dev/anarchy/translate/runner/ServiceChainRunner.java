package dev.anarchy.translate.runner;

import java.util.HashMap;
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
		if ( inputPayload == null )
			inputPayload = new HashMap<>();
		
		currentElement = this.serviceChain;
		
		while(currentElement != null) {

			// Transformations
			Map<String, Object> output = transformSingle(currentElement, inputPayload, true);
			
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

	public Map<String, Object> transformSingle(DRouteElementI currentElement, Map<String, Object> inputPayload, boolean canUseMockResponese) {
		Map<String, Object> output;
		try {
			// Template
			output = currentElement.transform(inputPayload);
			
			// Perhaps another transform is wanted
			output = onInvokeRouteElement(currentElement, output);
			
			// Mock (highest priority)
			if ( canUseMockResponese && currentElement instanceof DServiceDefinition ) {
				DServiceDefinition serviceDef = (DServiceDefinition)currentElement;
				if ( !StringUtils.isEmpty(serviceDef.getMockResponse()) ) {
					Map<String, Object> mockOutput = JSONUtils.jsonToMap(serviceDef.getMockResponse());
					if ( mockOutput != null )
						output = mockOutput;
				}
			}
		} catch (Exception e) {
			return new HashMap<String, Object>() {
				private static final long serialVersionUID = 1L;
				{
					this.put("Node", currentElement.getDestination());
					this.put("Error", e.getMessage());
				}
			};
		}
		
		return output;
	}

	protected abstract Map<String, Object> onInvokeRouteElement(DRouteElementI routeElement, Map<String, Object> input) throws Exception;
}
