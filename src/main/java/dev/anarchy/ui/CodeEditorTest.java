package dev.anarchy.ui;

import dev.anarchy.ace.control.CodeEditor;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CodeEditorTest extends Application {
	
	@Override
	public void start(Stage stage) {
		stage.setScene(new Scene(new CodeEditor("Hello World"), 500, 500));
		
		stage.centerOnScreen();
		stage.show();
	}
	public static void main(String[] args) {
		launch(args);
	}
}
