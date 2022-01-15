package dev.anarchy.ui.control;

import dev.anarchy.common.DFolder;
import dev.anarchy.common.DFolderElement;
import dev.anarchy.common.DServiceChain;
import dev.anarchy.ui.ServiceChainerApp;
import dev.anarchy.ui.util.IconHelper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextField;

public class ServiceChain extends Label implements FolderElement {
	private DServiceChain internal;
	
	private TextField textField;
	
	private final int height = 16;

	public ServiceChain(DFolder collection, DServiceChain internal) {
		super(internal.getName());
		this.internal = internal;
		
		this.internal.getOnNameChangeEvent().connect((args)->{
			this.setText(args[0].toString());
		});

		this.setGraphicTextGap(0);

		this.setCursor(Cursor.HAND);
		this.setPadding(new Insets(height/2f, 8, height/2f, 24));
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
				ServiceChainerApp.get().edit(internal);
			}
		});

		// Edit context
		{
			MenuItem option = new MenuItem("Edit", IconHelper.EDIT.create());
			option.setOnAction((event) -> {
				ServiceChainerApp.get().edit(internal);
			});
			context.getItems().add(option);
		}

		// Export
		{
			MenuItem option = new MenuItem("Export", IconHelper.EXPORT.create());
			option.setOnAction((event) -> {
				new ExportWindow(internal).show();
				//RouteHelper.export(internal);
			});
			context.getItems().add(option);
		}
		
		context.getItems().add(new SeparatorMenuItem());

		// Rename context
		{
			MenuItem option = new MenuItem("Rename");
			option.setOnAction((event) -> {
				rename();
			});
			context.getItems().add(option);
		}

		// Delete context
		{
			MenuItem option = new MenuItem("Delete", IconHelper.DELETE.create());
			option.setOnAction((event) -> {
				collection.removeChild(internal);
			});
			context.getItems().add(option);
		}
		this.setContextMenu(context);
	}
	
	private void rename() {
		this.textField = new TextField(this.getText());
		this.textField.prefHeightProperty().bind(this.heightProperty().subtract(height));
		this.textField.setPadding(new Insets(0, 0, 0, 0));
		this.textField.setOnAction(event -> renameFinish());
		this.setGraphic(textField);
		this.setText("");
		
		this.textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue) {
				if (textField.getText().trim().length() > 0)
					internal.setName(textField.getText());

				renameFinish();
			}
		});
		
		this.textField.requestFocus();
		this.textField.selectAll();
	}

	private void renameFinish() {
		this.setGraphic(null);
		this.setText(textField.getText());
	}

	public DServiceChain getServiceChain() {
		return this.internal;
	}

	@Override
	public DFolderElement getFolderElement() {
		return this.internal;
	}
}
