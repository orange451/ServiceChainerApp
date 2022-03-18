package dev.anarchy.ui.control;

import dev.anarchy.common.DCollection;
import dev.anarchy.ui.ApplicationData;
import dev.anarchy.ui.ServiceChainerApp;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
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
	
	@Override
	protected void generateContextMenu(ContextMenu context) {
		super.generateContextMenu(context);
		
		context.getItems().add(new SeparatorMenuItem());

		// Export
		{
			MenuItem option = new MenuItem("Open In Explorer");
			option.setOnAction((event) -> {
				ServiceChainerApp.get().getData().openExplorer((DCollection) this.internal);
			});
			context.getItems().add(option);
		}
	}

	@Override
	protected String filter(String input) {
		return ApplicationData.getFileName(input);
	}
}