package dev.anarchy.ui.control;

import java.util.Map;

import dev.anarchy.common.DServiceChain;
import dev.anarchy.translate.runner.BasicServiceChainRunner;
import dev.anarchy.translate.util.JSONUtils;
import dev.anarchy.ui.codemirror.CodeSyntax;
import dev.anarchy.ui.codemirror.control.CodeEditor;
import dev.anarchy.ui.util.IconHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class ServiceChainRunner extends ModalWindow {
	
	private DServiceChain serviceChain;
	
	private CodeEditor code;
	
	public ServiceChainRunner(DServiceChain serviceChain) {
		this.serviceChain = serviceChain;
		
		code.setSyntax(CodeSyntax.JSON);
		code.setText(serviceChain.getLastInput());
	}

	@Override
	protected void start(Stage stage) {
		BorderPane layout = new BorderPane();
		
		HBox topLayout = new HBox();
		topLayout.setStyle("-fx-background-color: rgb(245, 245, 245);");
		topLayout.setPadding(new Insets(8,8,8,8));
		topLayout.setSpacing(8);
		topLayout.setAlignment(Pos.CENTER);
		
		Button run = new Button("", IconHelper.PLAY.create());
		topLayout.getChildren().add(run);
		
		run.setOnMouseClicked((event)->{
			try {
				serviceChain.setLastInput(code.getText());
				Map<String, Object> inputPayload = JSONUtils.jsonToMap(code.getText());
				Map<String, Object> result = new BasicServiceChainRunner(serviceChain).run(inputPayload);
				System.out.println("Ran and got result: \n" + JSONUtils.mapToJsonPretty(result));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
		DropShadow dropShadow = new DropShadow();
		dropShadow.setRadius(5.0);
		dropShadow.setOffsetX(0.0);
		dropShadow.setOffsetY(1.0);
		dropShadow.setColor(Color.color(0.4, 0.5, 0.5));
		topLayout.setEffect(dropShadow);
		
		code = new CodeEditor();
		layout.setCenter(code);
		layout.setTop(topLayout);
		
        Scene toolScene = new Scene(layout, 640, 480);
        stage.setScene(toolScene);
        
        stage.setTitle("Service Chain Runner");
	}
}