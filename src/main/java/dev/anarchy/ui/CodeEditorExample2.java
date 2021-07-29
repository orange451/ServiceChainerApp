package dev.anarchy.ui;

import dev.anarchy.ui.codemirror.CodeSyntax;
import dev.anarchy.ui.codemirror.control.CodeEditor;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * An example application which demonstrates use of a CodeMirror based
 * JavaScript CodeEditor wrapped in a JavaFX WebView.
 */
public class CodeEditorExample2 extends Application {
	// some sample code to be edited.
	static final private String editingCode = "import javafx.application.Application;\n"
			+ "import javafx.scene.Scene;\n" + "import javafx.scene.web.WebView;\n" + "import javafx.stage.Stage;\n"
			+ "\n" + "/** Sample code editing application wrapping an editor in a WebView. */\n"
			+ "public class CodeEditorExample extends Application {\n"
			+ "  public static void main(String[] args) { launch(args); }\n"
			+ "  @Override public void start(Stage stage) throws Exception {\n"
			+ "    WebView webView = new WebView();\n"
			+ "    webView.getEngine().load(\"http://codemirror.net/mode/groovy/index.html\");\n"
			+ "    final Scene scene = new Scene(webView);\n"
			+ "    webView.prefWidthProperty().bind(scene.widthProperty());\n"
			+ "    webView.prefHeightProperty().bind(scene.heightProperty());\n" + "    stage.setScene(scene);\n"
			+ "    stage.show();\n" + "  }\n" + "}";

	@Override
	public void start(Stage stage) {
		StackPane pane = new StackPane();
		pane.setStyle("-fx-background-color: rgb(100,100,100);");
		pane.setAlignment(Pos.CENTER);
		
		CodeEditor code = new CodeEditor();
		code.setPrefSize(512, 512);
		code.setSyntax(CodeSyntax.JAVA);
		code.setText(editingCode);
		pane.getChildren().add(code);
		
		stage.setScene(new Scene(pane));
		stage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}