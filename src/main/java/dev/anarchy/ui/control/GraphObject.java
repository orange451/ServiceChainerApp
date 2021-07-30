package dev.anarchy.ui.control;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class GraphObject extends StackPane {

	private final Label label;
	
	private Color fill;
	
	private double cornerRadius;
	
	public GraphObject(Pane parent) {
		this.setAlignment(Pos.CENTER);
		this.setPrefSize(140, 80);

		DropShadow shadow = new DropShadow();
		shadow.setRadius(4.0);
		shadow.setOffsetX(0.0);
		shadow.setOffsetY(2.0);
		shadow.setColor(Color.color(0.2, 0.25, 0.25));
		this.setEffect(shadow);
		
		this.setFill(Color.color(0.196, 0.6274, 0.235));
		this.setCornerRadius(256);

		parent.getChildren().add(this);
		this.setTranslateX(round(parent.getPrefWidth() / 2) - round(this.getPrefWidth() / 2));
		this.setTranslateY(round(parent.getPrefWidth() / 2 * 0.925) - round(this.getPrefHeight() / 2));

		label = new Label("Node");
		this.getChildren().add(label);

		this.setOnMouseDragged(event -> {
			this.setManaged(false);
			double hWid = this.getWidth()/2;
			double hHei = this.getHeight()/2;
			double x = event.getX() + this.getTranslateX() - hWid;
			double y = event.getY() + this.getTranslateY() - hHei;
			this.setTranslateX(round(x));
			this.setTranslateY(round(y));
			event.consume();
		});
	}
	
	public void setCornerRadius(double x) {
		this.cornerRadius = x;
		update();
	}

	public void setFill(Color color) {
		this.fill = color;
		update();
	}

	private void update() {
		this.setBackground(new Background(new BackgroundFill(fill, new CornerRadii(cornerRadius,cornerRadius,cornerRadius,cornerRadius, false), Insets.EMPTY)));
	}

	private double round(double x) {
		return Math.floor(x / 20d) * 20d;
	}

	public void setName(String string) {
		this.label.setText(string);
	}
}
