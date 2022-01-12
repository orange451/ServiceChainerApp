package dev.anarchy.ui.control;

import java.io.File;

import dev.anarchy.common.DFolderElement;
import dev.anarchy.common.util.RouteHelper;
import dev.anarchy.ui.ServiceChainerApp;
import dev.anarchy.ui.util.TooltipHelper;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ExportWindow extends ModalWindow {
	
	private DFolderElement targetElement;
	
	private CheckBox exportMetadataCheckBox;
	
	private TextField destinationTextField;
	
	public ExportWindow(DFolderElement element) {
		this.targetElement = element;
		
		this.getStage().setTitle("Export " + element.getName());
	}

	@Override
	protected void start(Stage stage) {
		BorderPane border = new BorderPane();
		border.setPadding(new Insets(16,16,16,16));
		
	    GridPane gridPane = new GridPane();
	    gridPane.setAlignment(Pos.CENTER);
	    gridPane.setPadding(new Insets(16, 16, 16, 16));
	    gridPane.setHgap(10);
	    gridPane.setVgap(10);
	    
	    border.setCenter(gridPane);
	    
	    int buttonWidth = 100;
	    
	    int row = 0;
	    
	    // Filepath
	    {
	    	destinationTextField = new TextField();
	    	
	    	Button browse = new Button("Browse ...");
	    	browse.setPrefWidth(buttonWidth);
	    	
	    	browse.setOnMouseClicked((event)->{
	            FileChooser fileChooser = new FileChooser();
	            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Json files (*.json)", "*.json");
	            fileChooser.getExtensionFilters().add(extFilter);
	            File file = fileChooser.showSaveDialog(ServiceChainerApp.get().getStage());
	            
	            destinationTextField.setText(file.getAbsolutePath());
	    	});
	    	
	    	gridPane.add(new Label("Destination"), 0, row);
	    	gridPane.add(destinationTextField, 1, row);
	    	gridPane.add(browse, 2, row);
	    	
	    	row++;
	    }
	    
	    // Metadata
	    {
	    	exportMetadataCheckBox = new CheckBox();
	    	exportMetadataCheckBox.setSelected(true);
	    	
	    	HBox hLayout = new HBox();
	    	hLayout.setSpacing(4);
	    	hLayout.getChildren().add(exportMetadataCheckBox);
	    	hLayout.getChildren().add(TooltipHelper.buildTooltip("Metadata references all the internal data used to label/align/color visual nodes used by the service chain.\nSet this to false for a final export.\nSet this to true if sending to a colleague to edit."));
	    	
	    	gridPane.add(new Label("Export Metadata?"), 0, row);
	    	gridPane.add(hLayout, 1, row);
	    	row++;
	    }
	    
	    // Export
	    {
	    	row++;
	    	Button export = new Button("Export");
	    	export.setPrefWidth(buttonWidth);
	    	Button cancel = new Button("Cancel");
	    	cancel.setPrefWidth(buttonWidth);
	    	cancel.setOnMouseClicked((event)->{
	    		ExportWindow.this.getStage().close();
	    	});
	    	GridPane.setHalignment(cancel, HPos.RIGHT);
	    	
	    	export.setOnMouseClicked((event)->{
	    		RouteHelper.export(RouteHelper.getServiceChains(targetElement), new File(destinationTextField.getText()), !exportMetadataCheckBox.isSelected());
	    		ExportWindow.this.getStage().close();
	    	});

	    	gridPane.add(cancel, 1, row);
	    	gridPane.add(export, 2, row);
	    	row++;
	    }
		
		stage.setScene(new Scene(border));
		stage.centerOnScreen();
	}

}
