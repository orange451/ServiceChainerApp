package dev.anarchy.ui.codemirror.control;

import com.sun.javafx.webkit.WebConsoleListener;

import dev.anarchy.ui.codemirror.CodeSyntax;
import javafx.concurrent.Worker.State;
import javafx.geometry.Insets;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.Border;
import javafx.scene.web.WebView;

/**
 * A syntax highlighting code editor for JavaFX created by wrapping Ace code
 * editor in a WebView.
 */
public class CodeEditor extends Control {
	/** webview used to encapsulate Ace JavaScript. */
	private final WebView webview;

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
	 * Create a new code editor.
	 */
	public CodeEditor() {
		this("");
	}

	/**
	 * Create a new code editor.
	 */
	public CodeEditor(String editingCode) {
		if ( editingCode == null )
			editingCode = "";
		
		this.editingCode = editingCode;
		this.type = CodeSyntax.JAVA;
		
		this.setPadding(Insets.EMPTY);
		this.setBorder(Border.EMPTY);

		this.webview = new WebView();
		this.webview.prefWidthProperty().bind(this.widthProperty());
		this.webview.prefHeightProperty().bind(this.heightProperty());
		this.webview.maxWidthProperty().bind(this.widthProperty());
		this.webview.maxHeightProperty().bind(this.heightProperty());
		webview.getEngine().load(getClass().getResource("/ace/editor.html").toExternalForm());

		this.getChildren().add(webview);

		this.webview.getEngine().getLoadWorker().stateProperty().addListener((observable, oldState, newState) -> {
			if (newState == State.SUCCEEDED) {
				refresh();
				applyCode(true);
			}
		});
		
		WebConsoleListener.setDefaultListener((webview, message, lineNumber, sourceId) -> {
		    System.out.println(message + "[at " + lineNumber + "]");
		});
	}

	/**
	 * Set the syntax used to style this code editor.
	 */
	public void setSyntax(CodeSyntax syntax) {
		this.type = syntax;
		this.applySyntax();
	}

	/**
	 * Return the current syntax used to style this code editor.
	 */
	public CodeSyntax getSyntax() {
		return this.type;
	}

	/**
	 * Sets the current code in the editor and creates an editing snapshot of the
	 * code which can be reverted to.
	 */
	public void setText(String newCode) {
		this.editingCode = newCode;
		this.applyCode(false);
	}

	/**
	 * Returns the current code in the editor.
	 */
	public String getText() {
		String text = null;
		try {
			text = (String) webview.getEngine().executeScript("editor.getValue();");
		} catch (Exception e) {
			//
		}
		if (text == null)
			return this.editingCode;
		return text;
	}

	private void refresh() {
		applyCode(false);
		applySyntax();
	}

	public static String toJavaScriptString(String value) {
		value = value.replace("\u0000", "\\0").replace("'", "\\'").replace("\\", "\\\\").replace("\"", "\\\"")
				.replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
		return "\"" + value + "\"";
	}

	private void applyCode(boolean resetHistory) {
		try {
			String js = "editor.setValue(${val});";
			if (resetHistory)
				js = "editor.session.setValue(${val});";

			String encoded = js.replace("${val}", toJavaScriptString(this.editingCode));

			webview.getEngine().executeScript(encoded);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void applySyntax() {
		try {
			String js = ("editor.session.setMode(\"${val}\");").replace("${val}", this.type.getType());
			webview.getEngine().executeScript(js);
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}

	@Override
	protected Skin<CodeEditor> createDefaultSkin() {
		return new CodeEditorSkin(this);
	}
}

class CodeEditorSkin extends SkinBase<CodeEditor> implements Skin<CodeEditor> {
	private static final double PREFERRED_WIDTH = 100;
	private static final double PREFERRED_HEIGHT = 100;
	private CodeEditor control;

	// ******************** Constructors **************************************
	public CodeEditorSkin(final CodeEditor control) {
		super(control);
		this.control = control;
		initGraphics();
	}

	// ******************** Initialization ************************************
	private void initGraphics() {
		if (Double.compare(control.getPrefWidth(), 0.0) <= 0 || Double.compare(control.getPrefHeight(), 0.0) <= 0
				|| Double.compare(control.getWidth(), 0.0) <= 0 || Double.compare(control.getHeight(), 0.0) <= 0) {
			if (control.getPrefWidth() > 0 && control.getPrefHeight() > 0) {
				control.setPrefSize(control.getPrefWidth(), control.getPrefHeight());
			} else {
				control.setPrefSize(PREFERRED_WIDTH, PREFERRED_HEIGHT);
			}
		}
	}

	// ******************** Methods *******************************************
	@Override
	protected double computePrefWidth(final double height, final double top, final double right, final double bottom,
			final double left) {
		return super.computePrefWidth(height, top, right, bottom, left);
	}

	@Override
	protected double computePrefHeight(final double width, final double top, final double right, final double bottom,
			final double left) {
		return super.computePrefHeight(width, top, right, bottom, left);
	}

	@Override
	public void dispose() {
		control = null;
	}

	// ******************** Layout ********************************************
	@Override
	public void layoutChildren(final double x, final double y, final double width, final double height) {
		super.layoutChildren(x, y, width, height);
	}
}