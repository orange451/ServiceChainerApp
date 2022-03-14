package dev.anarchy.ui.control.servicechain;

import dev.anarchy.ace.AceEditor;
import dev.anarchy.ace.AceEvents;
import dev.anarchy.ace.Modes;
import dev.anarchy.common.DServiceDefinition;
import dev.anarchy.ui.control.PopupWindow;
import dev.anarchy.ui.util.IconHelper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class TemplateEditor extends PopupWindow {
	
	private TemplateEditorType type;
	
	private DServiceDefinition serviceDefinition;
	
	private Stage stage;
	
	private BorderPane layout;
	
	public TemplateEditor(TemplateEditorType type, DServiceDefinition serviceDefinition) {
		this.type = type;
		this.serviceDefinition = serviceDefinition;
		
        getStage().setTitle("Template Editor - " + serviceDefinition.getName());
		
		BorderPane topLayout = new BorderPane();
		topLayout.setStyle("-fx-background-color: rgb(245, 245, 245);");
		topLayout.setPadding(new Insets(8,8,8,8));
		
		DropShadow dropShadow = new DropShadow();
		dropShadow.setRadius(5.0);
		dropShadow.setOffsetX(0.0);
		dropShadow.setOffsetY(1.0);
		dropShadow.setColor(Color.color(0.4, 0.5, 0.5));
		topLayout.setEffect(dropShadow);
		
		Button test = new Button("", IconHelper.PLAY.create());
		test.setOnAction((event)->{
			new ServiceDefinitionRunner(serviceDefinition).show();
		});
		topLayout.setLeft(test);
		
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
		code.setOption("scrollPastEnd", 0.5d);
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
		
		HBox right = new HBox();
		right.setSpacing(8);
		right.setAlignment(Pos.CENTER_RIGHT);
		right.getChildren().add(new Label("Template Type:"));
		right.getChildren().add(comboBox);
		topLayout.setRight(right);
		
		layout.setCenter(code);
		layout.setTop(topLayout);
		
		stage.setOnCloseRequest((event)->{
        	serviceDefinition.setTemplateContent(code.getText());
        });
		
		code.addEventHandler(AceEvents.onChangeEvent, (event)->{
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
        Scene toolScene = new Scene(layout, 1024, 576);
        stage.setScene(toolScene);
	}
}
