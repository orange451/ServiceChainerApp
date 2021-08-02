package dev.anarchy.ui.control;

import dev.anarchy.common.DServiceDefinition;
import dev.anarchy.ui.codemirror.CodeSyntax;
import dev.anarchy.ui.codemirror.control.CodeEditor;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class TemplateEditor extends ModalWindow {
	
	private TemplateEditorType type;
	
	private DServiceDefinition serviceDefinition;
	
	private Stage stage;
	
	private BorderPane layout;
	
	public TemplateEditor(TemplateEditorType type, DServiceDefinition serviceDefinition) {
		this.type = type;
		this.serviceDefinition = serviceDefinition;
		
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
		
		layout.setCenter(code);
		layout.setTop(topLayout);
		
		stage.setOnCloseRequest((event)->{
        	serviceDefinition.setTemplateContent(code.getText());
        	
        	if ( "none".equalsIgnoreCase(comboBox.getValue()) )
        		serviceDefinition.setTransformationType(null);
        	else
        		serviceDefinition.setTransformationType(comboBox.getValue().toLowerCase());
        });
	}

	@Override
	protected void start(Stage stage) {
		this.stage = stage;
		
		layout = new BorderPane();
        Scene toolScene = new Scene(layout, 640, 480);
        stage.setScene(toolScene);
	}
}

enum TemplateEditorType {
	INPUT,
}
