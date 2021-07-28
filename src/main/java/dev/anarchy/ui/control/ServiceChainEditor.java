package dev.anarchy.ui.control;

import dev.anarchy.DServiceChain;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class ServiceChainEditor extends BorderPane {

	private Pane editPane;

	public ServiceChainEditor(DServiceChain internal) {
		Pane topBar = new StackPane();
		topBar.setStyle("-fx-background-color: rgb(240,240,240);");
		topBar.setPadding(new Insets(8, 8, 8, 8));
		topBar.prefWidthProperty().bind(this.widthProperty());
		this.setTop(topBar);

		HBox buttons = new HBox();
		Button newB = new Button("New Service Definition");
		newB.setOnAction((event)->{
			GraphObject g = new GraphObject(this.editPane);
			g.setFill(Color.AQUAMARINE);
			g.setCornerRadius(8);
			g.setName("Service Definition");
			g.setPrefSize(220, 60);
		});
		buttons.getChildren().add(newB);
		topBar.getChildren().add(buttons);

		DropShadow dropShadow = new DropShadow();
		dropShadow.setRadius(5.0);
		dropShadow.setOffsetX(0.0);
		dropShadow.setOffsetY(3.0);
		dropShadow.setColor(Color.color(0.4, 0.5, 0.5));
		topBar.setEffect(dropShadow);

		ScrollPane scroll = new ScrollPane();
		scroll.setStyle("-fx-background: transparent; -fx-border-color: transparent; -fx-background-color:transparent;");
		scroll.setPadding(Insets.EMPTY);
		scroll.setBorder(Border.EMPTY);
		scroll.setHvalue(0.5);
		scroll.setVvalue(0.5);
		this.setCenter(scroll);

		this.editPane = new Pane();
		this.editPane.setPrefSize(4096, 4096);
		this.editPane.setStyle("-fx-background-color: rgba(150, 150, 150, 0.1),"
				+ "linear-gradient(from 0.5px 0.0px to 10.5px  0.0px, repeat, rgba(102, 128, 128, 0.33) 5%, transparent 5%),"
				+ "linear-gradient(from 0.0px 0.5px to  0.0px 10.5px, repeat, rgba(102, 128, 128, 0.33) 5%, transparent 5%);");
		scroll.setContent(this.editPane);

		{
			GraphObject g = new GraphObject(this.editPane);
			g.setName("Entry Point");
		}
	}
}
