package dev.anarchy.ui.control;

import org.controlsfx.control.textfield.CustomTextField;

import dev.anarchy.common.DServiceDefinition;
import dev.anarchy.ui.util.IconHelper;
import dev.anarchy.ui.util.TooltipHelper;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

public class ServiceDefinitionEditor extends ModalWindow {

	private DServiceDefinition serviceDefinition;

	private TextField augmentField;

	private CustomTextField routeField;
	
	private CustomTextField templateLabel;
	
	public ServiceDefinitionEditor(DServiceDefinition serviceDefinition) {
		this.serviceDefinition = serviceDefinition;
		
		if ( serviceDefinition.getDestination() != null )
			routeField.setText(serviceDefinition.getDestination());
		
		if ( serviceDefinition.getAugmentPayload() != null )
			augmentField.setText(serviceDefinition.getAugmentPayload());
		
		updateTemplateInfo();
	    serviceDefinition.getOnChangedEvent().connect((args)->{
	    	updateTemplateInfo();
	    });
	}
	
	private void updateTemplateInfo() {
		System.out.println("Updating template info " + serviceDefinition.getTransformationType());
		
		String inputType = "[None]";
		String type = serviceDefinition.getTransformationType();
		if ( type != null )
			inputType = "[" + type + "]";
		
		templateLabel.setText(inputType);
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
	    
	    ColumnConstraints columnOneConstraints = new ColumnConstraints(150, 100, Double.MAX_VALUE);
	    columnOneConstraints.setHalignment(HPos.RIGHT);

	    // columnTwoConstraints will be applied to all the nodes placed in column two.
	    ColumnConstraints columnTwoConstrains = new ColumnConstraints(200, 200, Double.MAX_VALUE);
	    columnTwoConstrains.setHgrow(Priority.ALWAYS);

	    gridPane.getColumnConstraints().addAll(columnOneConstraints, columnTwoConstrains);
	    
	    int y = 1; // Current line

	    // Add Route Id
	    {
		    HBox hLayout = new HBox();
		    hLayout.setSpacing(8);
		    hLayout.setAlignment(Pos.CENTER_RIGHT);
		    hLayout.getChildren().add(new Label("Service Id:"));
		    hLayout.getChildren().add(TooltipHelper.buildTooltip("Service ids are used to differentiate service definition configurations.\nIdeally they should all be a unique value, but this is not enforced."));
		    gridPane.add(hLayout, 0, y);

		    routeField = new CustomTextField();
		    gridPane.add(routeField, 1, y++);
		    
		    Button mockButton = new Button("", IconHelper.GEAR.create());
		    mockButton.setPrefSize(16, 16);
		    mockButton.setPadding(new Insets(2,4,2,4));
		    routeField.setRight(mockButton);
		    
		    mockButton.setOnAction((event)->{
		    	new MockResponseEditor(serviceDefinition).show();
		    });
	    }

	    // Add Template configuration
	    {
		    HBox hLayout = new HBox();
		    hLayout.setSpacing(8);
		    hLayout.setAlignment(Pos.CENTER_RIGHT);
		    hLayout.getChildren().add(new Label("Input Template:"));
		    hLayout.getChildren().add(TooltipHelper.buildTooltip("Transformation templates are used to modify the current data payload running through the chain.\nThe result of this transformation will be dirrectly fed in to the service definition."));
		    gridPane.add(hLayout, 0, y);

		    templateLabel = new CustomTextField();
		    templateLabel.setEditable(false);
		    gridPane.add(templateLabel, 1, y++);
		    
		    Button mockButton = new Button("", IconHelper.EDIT.create());
		    mockButton.setPrefSize(16, 16);
		    mockButton.setPadding(new Insets(2,4,2,4));
		    templateLabel.setRight(mockButton);
		    
		    mockButton.setOnAction((event)->{
		    	new TemplateEditor(TemplateEditorType.INPUT, serviceDefinition).show();
		    });
	    }

	    // Add Name Text Field
	    {
		    
		    HBox hLayout = new HBox();
		    hLayout.setSpacing(8);
		    hLayout.setAlignment(Pos.CENTER_RIGHT);
		    Label nameLabel = new Label("Augment Payload:");
		    hLayout.getChildren().add(nameLabel);
		    hLayout.getChildren().add(TooltipHelper.buildTooltip("Augment Payload field is used to take the output data from the service definition and set it as a key in the current input data.\nIf not set, the input data coming in to this service definition will be replaced with the output of the service definition\nand be sent to the next node in the chain."));
		    gridPane.add(hLayout, 0, y);
		    
		    augmentField = new TextField();
		    gridPane.add(augmentField, 1,y++);
	    }
	    
	    layout.setCenter(gridPane);
	    
	    // ok button
	    Button b6 = new Button("Save");
	    b6.setMaxWidth(Double.MAX_VALUE);
	    b6.setOnMouseClicked((event)->{
	    	stage.close();
	    	close();
	    });
	    gridPane.add(b6, 0, y++, 2, 1);
		
		stage.setScene(new Scene(layout));
		this.show();
		
		stage.setOnCloseRequest((event)->{
			close();
		});
		
		stage.setTitle("Servive Definition Editor");
	}

	private void close() {
		serviceDefinition.setAugmentPayload(augmentField.getText());
		serviceDefinition.setDesination(routeField.getText());
	}
}
