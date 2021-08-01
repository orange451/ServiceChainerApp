package dev.anarchy.ui.control;

import dev.anarchy.common.DRouteElement;

public class TemplateEditor {
	private TemplateEditorType type;
	
	private DRouteElement routeElement;
	
	public TemplateEditor(TemplateEditorType type, DRouteElement routeElement) {
		
	}
}

enum TemplateEditorType {
	INPUT,
	OUTPUT;
}
