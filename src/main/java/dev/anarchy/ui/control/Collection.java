package dev.anarchy.ui.control;

import dev.anarchy.DCollection;

public class Collection extends Folder {
	public Collection(DCollection internal) {
		super(internal);
		
		rootPane.setPrefSize(128, 64);
		rootPane.setMaxHeight(rootPane.getPrefHeight());
		rootPane.setMinHeight(rootPane.getPrefHeight());
	}
}