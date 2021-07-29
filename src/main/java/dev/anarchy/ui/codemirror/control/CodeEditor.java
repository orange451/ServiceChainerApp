package dev.anarchy.ui.codemirror.control;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.html.HTMLDivElement;

import dev.anarchy.ui.codemirror.CodeSyntax;
import javafx.application.Platform;
import javafx.concurrent.Worker.State;
import javafx.geometry.Insets;
import javafx.scene.layout.Border;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;

/**
 * A syntax highlighting code editor for JavaFX created by wrapping a CodeMirror
 * code editor in a WebView.
 *
 * See http://codemirror.net for more information on using the codemirror
 * editor.
 */
public class CodeEditor extends Pane {
	/** a webview used to encapsulate the CodeMirror JavaScript. */
	private final WebView webview = new WebView();

	/**
	 * a snapshot of the code to be edited kept for easy initilization and reversion
	 * of editable code.
	 */
	private String editingCode;

	/**
	 * Syntax of the code editor
	 */
	private CodeSyntax type;
	
	/**
	 * a template for editing code - this can be changed to any template derived
	 * from the supported modes at http://codemirror.net to allow syntax highlighted
	 * editing of a wide variety of languages.
	 */
	private final String editingTemplate =
			    "<!DOCTYPE html>" + 
			    "<html lang=\"en\">" + 
			    "	<head>" + 
			    "		<title>ACE in Action</title>" +
			    "		<script src=\"https://cdnjs.cloudflare.com/ajax/libs/ace/1.4.12/ace.min.js\" type=\"text/javascript\" charset=\"utf-8\"></script>" + 
			    "	</head>" + 
			    "	<body style=\"border:0px; margin:0px; width:100%; height:100%;\">" + 
			    "		" + 
			    "		<div id=\"editor\" style=\"width:2px; height:2px;\">function foo(items) {\n" + 
			    "		    var x = \"All this is syntax highlighted\";\n" + 
			    "		    return x;\n" + 
			    "		}</div>" + 
			    "		    " +  
			    "		<script>" + 
			    "		    var editor = ace.edit(\"editor\");" + 
			    "		    editor.setTheme(\"ace/theme/monokai\");" + 
			    "		    editor.session.setMode(\"ace/mode/javascript\");" + 
			    "		</script>" + 
			    "	</body>" + 
			    "</html>";
	  
	/**
	 * Set the syntax used to style this code editor.
	 */
	public void setSyntax(CodeSyntax syntax) {
		this.type = syntax;
		this.refresh();
	}
	
	/**
	 * Return the current syntax used to style this code editor.
	 */
	public CodeSyntax getSyntax() {
		return this.type;
	}
	
	/**
	 * applies the editing template to the editing code to create the
	 * html+javascript source for a code editor.
	 */
	private void refresh() {
		webview.getEngine().loadContent(applyEditingTemplate());
	}

	/**
	 * Generates new template for the code editor
	 */
	private String applyEditingTemplate() {
		return editingTemplate.replace("${code}", editingCode).replace("${type}", type.getType());
	}

	/**
	 * Sets the current code in the editor and creates an editing snapshot of the
	 * code which can be reverted to.
	 */
	public void setText(String newCode) {
		this.editingCode = newCode;
		
		//String escapedCode = editingCode.replace("\"", "\\\"");
		String js = "editor.setValue(\"${val}\");".replace("${val}", newCode);
		System.out.println(js);
		//this.webview.getEngine().executeScript(js);
		//this.refresh();
	}

	/**
	 * Returns the current code in the editor.
	 */
	public String getText() {
		return "";//(String) webview.getEngine().executeScript("editor.getValue();");
	}

	/**
	 * returns the current code in the editor and updates an editing snapshot of the
	 * code which can be reverted to.
	 */
	public String getTextAndSnapshot() {
		this.editingCode = getText();
		return editingCode;
	}

	/** revert edits of the code to the last edit snapshot taken. */
	public void revertEdits() {
		setText(editingCode);
	}

	/**
	 * Create a new code editor.
	 */
	public CodeEditor(String editingCode) {
		this.editingCode = editingCode;
		this.type = CodeSyntax.JAVA;

		this.setPrefSize(100, 100);
		this.setPadding(Insets.EMPTY);
		this.setBorder(Border.EMPTY);
		this.webview.prefWidthProperty().bind(this.widthProperty());
		this.webview.prefHeightProperty().bind(this.heightProperty());
		this.webview.maxWidthProperty().bind(this.widthProperty());
		this.webview.maxHeightProperty().bind(this.heightProperty());
		this.refresh();

		this.getChildren().add(webview);
		
		this.webview.getEngine().getLoadWorker().stateProperty().addListener((observable, oldState, newState) -> {
		    if (newState == State.SUCCEEDED) {
		        Document doc = this.webview.getEngine().getDocument();
				Element element = doc.getElementById("editor");
				
				System.out.println(webview.getEngine().executeScript("document.getElementById(\"editor\")"));
		    }
		});
	}

	/**
	 * Create a new code editor.
	 */
	public CodeEditor() {
		this("");
	}
}