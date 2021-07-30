package dev.anarchy.ui.control;

import dev.anarchy.DCollection;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

public class Collection extends Folder {
	public Collection(DCollection internal) {
		super(internal);
		
		rootPane.setPrefSize(128, 64);
		rootPane.setMaxHeight(rootPane.getPrefHeight());
		rootPane.setMinHeight(rootPane.getPrefHeight());
		
		DropShadow dropShadow = new DropShadow();
		dropShadow.setRadius(5.0);
		dropShadow.setOffsetX(0.0);
		dropShadow.setOffsetY(1.0);
		dropShadow.setColor(Color.color(0.4, 0.5, 0.5, 0.5));
		this.setEffect(dropShadow);
	}
}