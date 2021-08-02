package dev.anarchy.ui.control;

import dev.anarchy.common.DServiceDefinition;
import dev.anarchy.ui.codemirror.CodeSyntax;
import dev.anarchy.ui.codemirror.control.CodeEditor;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MockResponseEditor extends ModalWindow {
	
	private DServiceDefinition serviceDefinition;
	
	private Stage stage;
	
	private CodeEditor code;
	
	public MockResponseEditor(DServiceDefinition serviceDefinition) {
		this.serviceDefinition = serviceDefinition;
		
		code.setText(serviceDefinition.getMockResponse());
		code.setSyntax(CodeSyntax.JSON);
		
		stage.setOnCloseRequest((event)->{
        	serviceDefinition.setMockResponse(code.getText());
        });
	}

	@Override
	protected void start(Stage stage) {
		this.stage = stage;
		
		BorderPane layout = new BorderPane();
        Scene toolScene = new Scene(layout, 640, 480);
        stage.setScene(toolScene);
        
		code = new CodeEditor();		
		layout.setCenter(code);
		
		stage.setTitle("Mock Response Editor");
	}
}
