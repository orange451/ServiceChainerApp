package dev.anarchy.common.util;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.velocity.runtime.parser.ParseException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.anarchy.common.DCollection;
import dev.anarchy.common.DFolder;
import dev.anarchy.common.DFolderElement;
import dev.anarchy.common.DRouteElement;
import dev.anarchy.common.DRouteElementI;
import dev.anarchy.common.DServiceChain;
import dev.anarchy.common.DServiceDefinition;
import dev.anarchy.translate.util.JSONUtils;
import dev.anarchy.translate.util.TranslateMapService;
import dev.anarchy.translate.util.TranslateType;
import dev.anarchy.ui.AnarchyApp;
import dev.anarchy.ui.control.Folder;
import freemarker.template.TemplateException;
import javafx.stage.FileChooser;

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
	 * @throws TemplateException 
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public static Map<String, Object> transform(DServiceDefinition serviceDefinition, Map<String, Object> inputPayload) throws Exception {
		TranslateType tType = TranslateType.match(serviceDefinition.getTransformationType());
		String json = JSONUtils.mapToJson(inputPayload);
		String output = new TranslateMapService().translate(tType, serviceDefinition.getTemplateContent(), json);
		return JSONUtils.jsonToMap(output);
	}
	
	public static void export(List<DServiceChain> serviceChains) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Json files (*.json)", "*.json");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showSaveDialog(AnarchyApp.get().getStage());
        
        DCollection collection = new DCollection();
        for (DServiceChain chain : serviceChains)
        	collection.addChild(chain);

        if (file != null) {
            try {
        		ObjectMapper objectMapper = new ObjectMapper();
        		String content = objectMapper.writeValueAsString(collection);
        		String trimmed = export(content);
                PrintWriter writer;
                writer = new PrintWriter(file);
                writer.println(trimmed);
                writer.close();
            } catch (IOException ex) {
            	ex.printStackTrace();
            }
        }
	}

	private static String export(String json) {
		Map<String, Object> data = null;
		try {
			data = JSONUtils.jsonToMap(json);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		data = removeInternalData(data);
		return JSONUtils.mapToJson(data);
	}

	@SuppressWarnings("unchecked")
	private static Map<String, Object> removeInternalData(Map<String, Object> data) {
		Map<String, Object> ret = new HashMap<>();
		
		for (Entry<String, Object> entry : data.entrySet()) {
			if ( entry.getKey().startsWith("_") ) 
				continue;
			
			if ( entry.getValue() != null && entry.getValue() instanceof Map ) {
				ret.put(entry.getKey(), removeInternalData((Map<String, Object>) entry.getValue()));
			} else if ( entry.getValue() != null && entry.getValue() instanceof List ) {
				List<Object> newList = new ArrayList<>();
				for (Object o : (List<Object>)entry.getValue()) {
					if ( o instanceof Map )
						newList.add(removeInternalData((Map)o));
						else
					newList.add(o);
				}
				ret.put(entry.getKey(), newList);
			} else {
				ret.put(entry.getKey(), entry.getValue());
			}
		}
		
		return ret;
	}

	public static void export(DFolderElement element) {
		List<DServiceChain> chains = getServiceChains(element);
		export(chains);
	}
	
	private static List<DServiceChain> getServiceChains(DFolderElement root) {
		List<DServiceChain> list = new ArrayList<>();
		
		if ( root instanceof DFolder ) {
			for (DFolderElement o : ((DFolder)root).getChildrenUnmodifyable()) {
				List<DServiceChain> newChains = getServiceChains(o);
				for (DServiceChain chain : newChains)
					list.add(chain);
			}
		} else if ( root instanceof DServiceChain ) {
			list.add((DServiceChain) root);
		}
		
		return list;
	}

}
