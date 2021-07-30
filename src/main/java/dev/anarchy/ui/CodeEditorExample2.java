package dev.anarchy.ui;

import dev.anarchy.ui.codemirror.control.CodeEditor;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * An example application which demonstrates use of a CodeMirror based
 * JavaScript CodeEditor wrapped in a JavaFX WebView.
 */
public class CodeEditorExample2 extends Application {
	
	@Override
	public void start(Stage stage) {
		CodeEditor code = new CodeEditor("package com.ace.editor;\n" + 
				"\n" + 
				"import java.util.ArrayList;\n" + 
				"\n" + 
				"public class AceEditor {\n" + 
				"\n" + 
				"    /*\n" + 
				"     * This is a demo\n" + 
				"     */\n" + 
				"    public static void main(String[] args) {\n" + 
				"        System.out.println(\"Hello World\");\n" + 
				"    }\n" + 
				"}");
		
		stage.setScene(new Scene(code, 640, 480));
		stage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}