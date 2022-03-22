package dev.anarchy.ui.control;

import com.fasterxml.jackson.core.JsonProcessingException;

import dev.anarchy.ace.AceEditor;
import dev.anarchy.ace.Modes;
import dev.anarchy.translate.util.JSONUtils;
import dev.anarchy.ui.ServiceChainerUIBuilder;
import dev.anarchy.ui.util.IconHelper;
import dev.anarchy.ui.util.StyleClassToggleUtil;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public abstract class RunnerWindowBase extends PopupWindow {
	
	protected AceEditor code;
	
	private SplitPane split;
	
	private TabPane resultPane;
	
	private Button run;
	
	protected void addResult(String title, String text) {
		Tab resultTab = new Tab();
		resultTab.setText(title);
		
		resultPane.getTabs().add(resultTab);
		resultPane.getSelectionModel().select(resultTab);
		
		AceEditor layout = new AceEditor(text);
		layout.setOption("scrollPastEnd", 0.5d);
		layout.setMode(Modes.JSON);
		resultTab.setContent(layout);
		
		layout.setOnLoad((event)->{
			layout.getEditor().setReadOnly(true);
		});
		
		if ( !split.getItems().contains(resultPane) )
			split.getItems().add(resultPane);
	}

	@Override
	protected void start(Stage stage) {
		split = new SplitPane();
		split.setOrientation(Orientation.VERTICAL);
		BorderPane layout = new BorderPane();
		
		BorderPane topLayout = new BorderPane();
		topLayout.setStyle("-fx-background-color: rgb(245, 245, 245);");
		topLayout.setPadding(new Insets(8,8,8,8));
		
		// Test button
		this.run = new Button("Test", IconHelper.PLAY.create());
		run.setOnMouseClicked((event)->{
			onTest();
		});
		topLayout.setCenter(run);
		
		// Beautify button
		StackPane t = new StackPane();
		t.prefHeightProperty().bind(run.heightProperty());
		t.setAlignment(Pos.CENTER);
		Label beautify = new Label("Beautify");
		beautify.setStyle("-fx-text-fill: #F5823A");
		beautify.setOnMouseClicked((event)->{
			try {
				String json = code.getText();
				String pretty = JSONUtils.mapToJsonPretty(JSONUtils.jsonToMap(json));
				code.setText(pretty);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		});
		t.getChildren().add(beautify);
		topLayout.setRight(t);
		
		resultPane = new TabPane();
		resultPane.getTabs().addListener(new ListChangeListener<Tab>() {
			@Override
			public void onChanged(Change<? extends Tab> tab) {
				if ( resultPane.getTabs().size() < 1 )
					split.getItems().remove(resultPane);
			}
		});
		
		DropShadow dropShadow = new DropShadow();
		dropShadow.setRadius(5.0);
		dropShadow.setOffsetX(0.0);
		dropShadow.setOffsetY(1.0);
		dropShadow.setColor(Color.color(0.4, 0.5, 0.5));
		topLayout.setEffect(dropShadow);
		
		code = new AceEditor();
		code.setOption("scrollPastEnd", 0.5d);
		layout.setCenter(code);
		layout.setTop(topLayout);

        split.getItems().add(layout);
        Scene toolScene = new Scene(split, 640, 480);
        ServiceChainerUIBuilder.setTheme(toolScene);
        stage.setScene(toolScene);
        
        stage.setTitle("Runner");
	}
	
	protected void animateFlash(String className) {
		StyleClassToggleUtil.toggleStyleClass(run.getStyleClass(), className, 4, 300);
	}
	
	protected void animateLong(String className) {
		StyleClassToggleUtil.toggleStyleClass(run.getStyleClass(), className, 1, 750);
	}
	
	protected abstract void onTest();
}
