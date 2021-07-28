package dev.anarchy.ui.control;

import dev.anarchy.DCollection;
import dev.anarchy.DServiceChain;
import dev.anarchy.ui.AnarchyApp;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;

public class ServiceChain extends Label {
	private DCollection collection;

	private DServiceChain internal;

	public ServiceChain(DCollection collection, DServiceChain internal) {
		super(internal.getName());
		this.internal = internal;

		this.setCursor(Cursor.HAND);
		this.setPadding(new Insets(8, 8, 8, 32));
		this.hoverProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean hovered) {
				if (hovered)
					setStyle("-fx-background-color: rgba(100,100,100,0.5);");
				else
					setStyle("-fx-background-color: transparent;");
			}
		});

		ContextMenu context = new ContextMenu();

		// Double Click
		this.setOnMouseClicked((mouseEvent) -> {
			if (mouseEvent.getClickCount() == 2) {
				AnarchyApp.get().edit(internal);
			}
		});

		// Delete context
		{
			MenuItem option = new MenuItem("Delete");
			option.setOnAction((event) -> {
				collection.removeChild(internal);
			});
			context.getItems().add(option);
		}

		// Edit context
		{
			MenuItem option = new MenuItem("Edit");
			option.setOnAction((event) -> {
				AnarchyApp.get().edit(internal);
			});
			context.getItems().add(option);
		}
		this.setContextMenu(context);
	}

	public DServiceChain getServiceChain() {
		return this.internal;
	}
}
