package dev.anarchy.ui.control;

import java.util.Map;

import dev.anarchy.common.DServiceChain;
import dev.anarchy.translate.runner.BasicServiceChainRunner;
import dev.anarchy.translate.util.JSONUtils;
import dev.anarchy.ace.AceEditor;
import dev.anarchy.ace.Modes;
import dev.anarchy.ui.util.IconHelper;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class ServiceChainRunner extends ModalWindow {
	
	private DServiceChain serviceChain;
	
	private AceEditor code;
	
	private SplitPane split;
	
	private TabPane resultPane;
	
	public ServiceChainRunner(DServiceChain serviceChain) {
		this.serviceChain = serviceChain;
		
		code.setText(serviceChain.getLastInput());
		code.setMode(Modes.JSON);
	}
	
	private void addResult(String title, String text) {
		Tab resultTab = new Tab();
		resultTab.setText(title);
		resultPane.getTabs().add(resultTab);
		
		AceEditor layout = new AceEditor(text);
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
		
		HBox topLayout = new HBox();
		topLayout.setStyle("-fx-background-color: rgb(245, 245, 245);");
		topLayout.setPadding(new Insets(8,8,8,8));
		topLayout.setSpacing(8);
		topLayout.setAlignment(Pos.CENTER);
		
		Button run = new Button("Test", IconHelper.PLAY.create());
		topLayout.getChildren().add(run);
		
		run.setOnMouseClicked((event)->{
			try {
				serviceChain.setLastInput(code.getText());
				Map<String, Object> inputPayload = JSONUtils.jsonToMap(code.getText());
				Map<String, Object> result = new BasicServiceChainRunner(serviceChain).run(inputPayload);
				String prettyResult = JSONUtils.mapToJsonPretty(result);
				addResult("Test Result [ok]", prettyResult);
			} catch (Exception e) {
				e.printStackTrace();
				addResult("Test Result [err]", e.toString());
			}
		});
		
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
		layout.setCenter(code);
		layout.setTop(topLayout);

        split.getItems().add(layout);
        Scene toolScene = new Scene(split, 640, 480);
        stage.setScene(toolScene);
        
        stage.setTitle("Service Chain Runner");
	}
}
