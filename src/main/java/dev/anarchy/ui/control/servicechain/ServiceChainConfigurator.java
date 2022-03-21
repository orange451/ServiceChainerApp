package dev.anarchy.ui.control.servicechain;

import dev.anarchy.common.DExtensionPoint;
import dev.anarchy.common.DServiceChain;
import dev.anarchy.ui.ServiceChainerUIBuilder;
import dev.anarchy.ui.control.ModalWindow;
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

public class ServiceChainConfigurator extends ModalWindow {

	private DServiceChain serviceChain;

	private TextField nameField;

	private TextField pointField;
	
	public ServiceChainConfigurator(DServiceChain serviceChain) {
		this.serviceChain = serviceChain;
		
		if ( serviceChain.getRegisteredExtensionPoints().size() == 0 )
			serviceChain.getRegisteredExtensionPoints().add(new DExtensionPoint());
		
		nameField.setText(serviceChain.getHandlerId());
		pointField.setText(serviceChain.getRegisteredExtensionPoints().get(0).getExtensionPointId());
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

	    // Add Name Text Field
	    {
		    
		    HBox hLayout = new HBox();
		    hLayout.setSpacing(8);
		    hLayout.setAlignment(Pos.CENTER_RIGHT);
		    Label nameLabel = new Label("Extension Name:");
		    hLayout.getChildren().add(nameLabel);
		    hLayout.getChildren().add(TooltipHelper.buildTooltip("Extension Name field should be a unique identifier to locate this service chain."));
		    gridPane.add(hLayout, 0, y);
		    
		    nameField = new TextField();
		    gridPane.add(nameField, 1, y++);
	    }

	    // Extension Point
	    {
		    
		    HBox hLayout = new HBox();
		    hLayout.setSpacing(8);
		    hLayout.setAlignment(Pos.CENTER_RIGHT);
		    Label nameLabel = new Label("Extension Point:");
		    hLayout.getChildren().add(nameLabel);
		    hLayout.getChildren().add(TooltipHelper.buildTooltip("Extension Point field is used to map where a service chain will be invoked."));
		    gridPane.add(hLayout, 0, y);
		    
		    pointField = new TextField();
		    gridPane.add(pointField, 1, y++);
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
        ServiceChainerUIBuilder.setTheme(stage.getScene());
        
		this.show();
		
		stage.setOnCloseRequest((event)->{
			close();
		});
		
		stage.setTitle("Servive Chain Configurator");
	}

	private void close() {
		serviceChain.setHandlerId(nameField.getText());
		serviceChain.getRegisteredExtensionPoints().get(0).setExtensionPointId(pointField.getText());
	}
}
