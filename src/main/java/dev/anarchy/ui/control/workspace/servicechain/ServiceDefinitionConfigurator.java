package dev.anarchy.ui.control.workspace.servicechain;

import dev.anarchy.common.DServiceDefinition;
import dev.anarchy.ui.control.ModalWindow;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class ServiceDefinitionConfigurator extends ModalWindow {

	private DServiceDefinition serviceDefinition;
	
	public ServiceDefinitionConfigurator(DServiceDefinition serviceDefinition) {
		this.serviceDefinition = serviceDefinition;
		
		updateTemplateInfo();
	    serviceDefinition.getOnChangedEvent().connect((args)->{
	    	updateTemplateInfo();
	    });
	}
	
	private void updateTemplateInfo() {
		
		this.getStage().setTitle("Service Definition Configurator - " + serviceDefinition.getName());
	}

	@Override
	protected void start(Stage stage) {
		BorderPane layout = new BorderPane();
		layout.setPrefSize(512, 288);
		
		stage.setScene(new Scene(layout));
		this.show();
		
		stage.setOnCloseRequest((event)->{
			close();
		});
	}

	private void close() {
		//
	}
}
