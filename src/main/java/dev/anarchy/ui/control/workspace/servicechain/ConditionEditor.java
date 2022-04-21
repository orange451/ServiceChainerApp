package dev.anarchy.ui.control.workspace.servicechain;

import java.util.List;

import dev.anarchy.ace.AceEditor;
import dev.anarchy.ace.AceEvents;
import dev.anarchy.ace.Modes;
import dev.anarchy.common.DConditionElement;
import dev.anarchy.common.DRouteElementI;
import dev.anarchy.common.DServiceChain;
import dev.anarchy.common.DServiceDefinition;
import dev.anarchy.common.util.RouteHelper;
import dev.anarchy.ui.control.PopupWindow;
import dev.anarchy.ui.util.IconHelper;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class ConditionEditor extends PopupWindow {
	
	private DConditionElement condition;
	
	private Stage stage;
	
	private BorderPane layout;
	
	public ConditionEditor(DServiceChain serviceChain, DConditionElement condition) {
		this.condition = condition;
		
        getStage().setTitle("Condition Editor - " + condition.getName());
		
		BorderPane topLayout = new BorderPane();
		topLayout.setStyle("-fx-background-color: rgb(245, 245, 245);");
		topLayout.setPadding(new Insets(8,8,8,8));
		
		DropShadow dropShadow = new DropShadow();
		dropShadow.setRadius(5.0);
		dropShadow.setOffsetX(0.0);
		dropShadow.setOffsetY(1.0);
		dropShadow.setColor(Color.color(0.4, 0.5, 0.5));
		topLayout.setEffect(dropShadow);
		
		Button test = new Button("", IconHelper.PLAY.create());
		test.setOnAction((event)->{
			new ConditionTester(condition).show();
		});
		topLayout.setLeft(test);
		
		AceEditor code = new AceEditor(condition.getCondition());
		code.setMode(Modes.CSharp);
		
		layout.setCenter(code);
		layout.setTop(topLayout);
		
		stage.setOnCloseRequest((event)->{
			condition.getConditionMeta().setCondition(code.getText());
			condition.setCondition(code.getText());
			updateConnectedCondition(serviceChain.getRoutesUnmodifyable(), condition);
        });
		
		code.addEventHandler(AceEvents.onChangeEvent, (event)->{
			condition.getConditionMeta().setCondition(code.getText());
			condition.setCondition(code.getText());
			updateConnectedCondition(serviceChain.getRoutesUnmodifyable(), condition);
		});
	}

	private void updateConnectedCondition(List<DRouteElementI> routesUnmodifyable, DConditionElement condition) {
		for (DRouteElementI element : routesUnmodifyable) {
			if ( element.getDestinationId().equals(condition.getConditionMeta().getLinkedToId()) ) {
				((DServiceDefinition)element).setCondition(condition.getCondition());
			}
		}
	}

	@Override
	protected void start(Stage stage) {
		this.stage = stage;
		
		layout = new BorderPane();
        Scene toolScene = new Scene(layout, 400, 300);
        stage.setScene(toolScene);
	}
}