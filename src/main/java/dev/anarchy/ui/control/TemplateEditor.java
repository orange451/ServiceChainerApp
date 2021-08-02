package dev.anarchy.ui.control;

import dev.anarchy.common.DServiceDefinition;
import dev.anarchy.ui.AnarchyApp;
import dev.anarchy.ui.codemirror.CodeSyntax;
import dev.anarchy.ui.codemirror.control.CodeEditor;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class TemplateEditor {
	private TemplateEditorType type;
	
	private DServiceDefinition serviceDefinition;
	
	private Stage toolStage;
	
	public TemplateEditor(TemplateEditorType type, DServiceDefinition serviceDefinition) {
		HBox topLayout = new HBox();
		topLayout.setAlignment(Pos.CENTER_RIGHT);
		
		ComboBox<String> comboBox = new ComboBox<>();
		comboBox.getItems().addAll(
		    "None",
		    "Velocity",
		    "Freemarker"
		);
		
		comboBox.setValue(comboBox.getItems().get(0));
		for (String item : comboBox.getItems())
			if ( item.equalsIgnoreCase(serviceDefinition.getTransformationType()) )
				comboBox.setValue(item);
		
		CodeEditor code = new CodeEditor(serviceDefinition.getTemplateContent());
		if ( "velocity".equalsIgnoreCase(serviceDefinition.getTransformationType()) ) {
			code.setSyntax(CodeSyntax.VELOCITY);
		} else {
			code.setSyntax(CodeSyntax.JSON);
		}
		
		topLayout.getChildren().add(new Label("Template Type:"));
		topLayout.getChildren().add(comboBox);
		
		BorderPane layout = new BorderPane();
		layout.setCenter(code);
		layout.setTop(topLayout);
		
        toolStage = new Stage();
        toolStage.initOwner(AnarchyApp.get().getStage());
        toolStage.initModality(Modality.APPLICATION_MODAL);
        toolStage.setAlwaysOnTop(true);
        toolStage.setOnCloseRequest((event)->{
        	serviceDefinition.setTemplateContent(code.getText());
        	
        	if ( "none".equalsIgnoreCase(comboBox.getValue()) )
        		serviceDefinition.setTransformationType(null);
        	else
        		serviceDefinition.setTransformationType(comboBox.getValue().toLowerCase());
        });
        
        Scene toolScene = new Scene(layout, 640, 480);
        toolStage.setScene(toolScene);
	}
	
	public void show() {
        toolStage.centerOnScreen();
        toolStage.show();
	}
}

enum TemplateEditorType {
	INPUT,
}
