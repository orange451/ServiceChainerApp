package dev.anarchy.common.util;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import dev.anarchy.common.DRouteElement;
import dev.anarchy.common.DRouteElementI;
import dev.anarchy.common.DServiceDefinition;
import dev.anarchy.translate.util.TranslateMapService;
import dev.anarchy.translate.util.TranslateType;

public class RouteHelper {

	public static void linkRoutes(List<DRouteElementI> allRoutes, DRouteElementI source, DRouteElement destination) {
		// Unlink anything source is connected to
		for (DRouteElementI element : allRoutes) {
			if ( !(element instanceof DRouteElement) )
				continue;
			
			if ( element.getSourceId() != null && element.getSourceId().equals(source.getDestinationId()) ) {
				((DRouteElement)element).setSource(null);
				((DRouteElement)element).setSourceId(null);
			}
		}
		
		// Link new destination
		if ( destination != null ) {
			destination.setSource(source.getDestination());
			destination.setSourceId(source.getDestinationId());
		}
	}

	/**
	 * Return a route element that is linked TO this source element. Two elements are linked when the sources destination matches the destinations source.
	 */
	public static DRouteElementI getLinkedTo(List<DRouteElementI> allRoutes, DRouteElementI source) {
		for (DRouteElementI element : allRoutes) {
			if ( element == source )
				continue;
			
			if ( element.getSourceId() != null && element.getSourceId().equals(source.getDestinationId()) ) {
				return element;
			}
		}
		
		return null;
	}

	/**
	 * Transform the input payload based on a service definitions configuration
	 */
	public static Map<String, Object> transform(DServiceDefinition serviceDefinition, Map<String, Object> inputPayload) {
		TranslateType tType = TranslateType.match(serviceDefinition.getTransformationType());
		String json = null; try { json = new ObjectMapper().writeValueAsString(inputPayload); } catch (Exception e) { e.printStackTrace(); }
		String output = new TranslateMapService().translate(tType, serviceDefinition.getTemplateContent(), json);
		Map<String, Object> map = null; try { map = new ObjectMapper().readValue(output, Map.class); } catch (Exception e) { e.printStackTrace(); }
		return map;
	}

}
