package dev.anarchy.common.util;

import java.util.List;

import dev.anarchy.common.DRouteElement;
import dev.anarchy.common.DRouteElementI;

public class RouteHelper {

	public static void linkRoutes(List<DRouteElement> allRoutes, DRouteElementI source, DRouteElement destination) {
		// Unlink anything source is connected to
		for (DRouteElement element : allRoutes) {
			if ( element.getSourceId() != null && element.getSourceId().equals(source.getDestinationId()) ) {
				element.setSource(null);
				element.setSourceId(null);
			}
			
			// Special case for entry point
			if ( "ON_EVENT".equals(element.getSourceId()) && "ON_EVENT".equals(source.getDestinationId())) {
				element.setSource(null);
				element.setSourceId(null);
			}
		}
		
		// Link new destination
		if ( destination != null ) {
			destination.setSource(source.getDestination());
			destination.setSourceId(source.getDestinationId());
		}
	}

	public static DRouteElement getLinkedTo(List<DRouteElement> allRoutes, DRouteElementI source) {
		for (DRouteElement element : allRoutes) {
			if ( element.getSourceId() != null && element.getSourceId().equals(source.getDestinationId()) ) {
				return element;
			}
		}
		
		return null;
	}

}
