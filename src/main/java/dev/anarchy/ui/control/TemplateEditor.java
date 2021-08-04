package dev.anarchy.ui.control;

import dev.anarchy.ace.AceEditor;
import dev.anarchy.ace.Modes;
import dev.anarchy.common.DServiceDefinition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
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
		topLayout.setStyle("-fx-background-color: rgb(245, 245, 245);");
		topLayout.setPadding(new Insets(8,8,8,8));
		topLayout.setSpacing(8);
		topLayout.setAlignment(Pos.CENTER_RIGHT);
		
		DropShadow dropShadow = new DropShadow();
		dropShadow.setRadius(5.0);
		dropShadow.setOffsetX(0.0);
		dropShadow.setOffsetY(1.0);
		dropShadow.setColor(Color.color(0.4, 0.5, 0.5));
		topLayout.setEffect(dropShadow);
		
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
		
		AceEditor code = new AceEditor(serviceDefinition.getTemplateContent());
		updateMode(code);
		
		// Update transformation type
		comboBox.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
	        	if ( "none".equalsIgnoreCase(comboBox.getValue()) )
	        		serviceDefinition.setTransformationType(null);
	        	else
	        		serviceDefinition.setTransformationType(comboBox.getValue().toLowerCase());
	        	
				updateMode(code);
			}
		});
		
		topLayout.getChildren().add(new Label("Template Type:"));
		topLayout.getChildren().add(comboBox);
		
		layout.setCenter(code);
		layout.setTop(topLayout);
		
		stage.setOnCloseRequest((event)->{
        	serviceDefinition.setTemplateContent(code.getText());
        });
	}

	private void updateMode(AceEditor code) {
		if ( "velocity".equalsIgnoreCase(serviceDefinition.getTransformationType()) ) {
			code.setMode(Modes.Velocity);
		} else {
			code.setMode(Modes.FTL);
		}
	}

	@Override
	protected void start(Stage stage) {
		this.stage = stage;
		
		layout = new BorderPane();
        Scene toolScene = new Scene(layout, 640, 480);
        stage.setScene(toolScene);
        
        stage.setTitle("Template Editor");
	}
}

enum TemplateEditorType {
	INPUT,
}
