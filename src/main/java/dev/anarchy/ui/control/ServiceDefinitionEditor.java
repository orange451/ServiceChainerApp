package dev.anarchy.ui.control;

import dev.anarchy.common.DServiceDefinition;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

public class ServiceDefinitionEditor extends ModalWindow {

	private DServiceDefinition serviceDefinition;

	private TextField nameField;
	
	public ServiceDefinitionEditor(DServiceDefinition serviceDefinition) {
		this.serviceDefinition = serviceDefinition;
		
		if ( serviceDefinition.getAugmentPayload() != null )
			nameField.setText(serviceDefinition.getAugmentPayload());
	}

	@Override
	protected void start(Stage stage) {
		BorderPane layout = new BorderPane();
		layout.setPrefSize(100, 100);

	    GridPane gridPane = new GridPane();
	    gridPane.setAlignment(Pos.CENTER);
	    gridPane.setPadding(new Insets(16, 16, 16, 16));
	    gridPane.setHgap(10);
	    gridPane.setVgap(10);
	    
	    ColumnConstraints columnOneConstraints = new ColumnConstraints(100, 100, Double.MAX_VALUE);
	    columnOneConstraints.setHalignment(HPos.RIGHT);

	    // columnTwoConstraints will be applied to all the nodes placed in column two.
	    ColumnConstraints columnTwoConstrains = new ColumnConstraints(100, 100, Double.MAX_VALUE);
	    columnTwoConstrains.setHgrow(Priority.ALWAYS);

	    gridPane.getColumnConstraints().addAll(columnOneConstraints, columnTwoConstrains);
		
	    // Add Name Label
	    Label nameLabel = new Label("Augment Payload:");
	    gridPane.add(nameLabel, 0,1);

	    // Add Name Text Field
	    nameField = new TextField();
	    gridPane.add(nameField, 1,1);
	    
	    layout.setCenter(gridPane);
	    
	    // ok button
	    Button b6 = new Button("Save");
	    b6.setMaxWidth(Double.MAX_VALUE);
	    b6.setOnMouseClicked((event)->{
	    	stage.close();
	    	close();
	    });
	    gridPane.add(b6, 0, 2, 2, 1);
		
		stage.setScene(new Scene(layout));
		this.show();
		
		stage.setOnCloseRequest((event)->{
			close();
		});
	}

	private void close() {
		serviceDefinition.setAugmentPayload(nameField.getText());
	}
}
