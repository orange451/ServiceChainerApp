package dev.anarchy.ui.control;

import java.io.File;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import dev.anarchy.common.DFolder;
import dev.anarchy.common.DFolderElement;
import dev.anarchy.common.DServiceChain;
import dev.anarchy.common.util.RouteHelper;
import dev.anarchy.ui.ServiceChainerApp;
import dev.anarchy.ui.util.IconHelper;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;

public class Folder extends VBox implements FolderElement {
	private final Label label;

	private final Label childrenLabel;

	protected final DFolder internal;

	protected final HBox rootPane;

	private final TextField textField;

	private final StackPane labelRenamePane;

	private final StackPane expandHolder;

	private final VBox childrenBox;

	private boolean open;

	public Folder(DFolder internal) {
		this.internal = internal;

		ContextMenu context = new ContextMenu();
		context.setAutoHide(true);
		
		generateContextMenu(context);

		rootPane = new HBox();
		rootPane.setSpacing(8);
		rootPane.setPadding(new Insets(0, 4, 0, 8));
		rootPane.setAlignment(Pos.CENTER_LEFT);
		rootPane.getStyleClass().add("Folder");
		rootPane.setPrefSize(128, 32);
		rootPane.setMaxHeight(rootPane.getPrefHeight());
		rootPane.setMinHeight(rootPane.getPrefHeight());
		rootPane.setOnMouseClicked((event) -> {
			if (event.getButton() == MouseButton.SECONDARY) {
				if (!context.isShowing()) {
					context.show(rootPane, event.getScreenX(), event.getScreenY());
				}
			}
		});
		this.getChildren().add(rootPane);

		// Expand
		{
			expandHolder = new StackPane();
			expandHolder.setCursor(Cursor.HAND);
			expandHolder.setAlignment(Pos.CENTER);
			expandHolder.setPrefSize(20, 20);
			expandHolder.setMinSize(expandHolder.getPrefWidth(), expandHolder.getPrefHeight());
			rootPane.getChildren().add(expandHolder);

			FontAwesomeIconView icon = new FontAwesomeIconView(FontAwesomeIcon.CHEVRON_RIGHT);
			icon.setFill(Color.color(0.3, 0.3, 0.4, 0.75));
			expandHolder.getChildren().add(icon);
			
			expandHolder.setOnMouseClicked((event) -> {
				toggleOpen();
			});
		}

		// Folder
		{
			FontAwesomeIconView icon = new FontAwesomeIconView(FontAwesomeIcon.FOLDER_ALT);
			icon.setSize("20px");
			icon.setFill(Color.color(0.3, 0.3, 0.4, 0.75));
			rootPane.getChildren().add(icon);
		}

		// Main label
		{
			VBox t = new VBox();
			t.fillWidthProperty().set(true);
			t.setAlignment(Pos.CENTER_LEFT);
			rootPane.getChildren().add(t);

			labelRenamePane = new StackPane();
			labelRenamePane.prefWidthProperty().bind(t.widthProperty());
			labelRenamePane.setAlignment(Pos.CENTER_LEFT);
			t.getChildren().add(labelRenamePane);

			label = new Label("Collection");
			label.setTextFill(Color.color(0.3, 0.3, 0.4));
			labelRenamePane.getChildren().add((Node)label);

			childrenLabel = new Label("0 children");
			childrenLabel.setTextFill(Color.color(0.3, 0.3, 0.4, 0.66));
			if ( this instanceof Collection ) {
				t.getChildren().add(childrenLabel);
			}

			HBox.setHgrow(t, Priority.ALWAYS);
		}

		// Settings
		{
			StackPane iconHolder = new StackPane();
			iconHolder.setCursor(Cursor.HAND);
			iconHolder.setAlignment(Pos.CENTER);
			iconHolder.setPrefSize(20, 20);
			iconHolder.setMinSize(iconHolder.getPrefWidth(), iconHolder.getPrefHeight());
			rootPane.getChildren().add(iconHolder);

			FontAwesomeIconView icon = new FontAwesomeIconView(
					open ? FontAwesomeIcon.ELLIPSIS_H : FontAwesomeIcon.ELLIPSIS_V);
			icon.setMouseTransparent(true);
			icon.setSize("16px");
			icon.setFill(Color.color(0.3, 0.3, 0.4, 0.75));
			iconHolder.getChildren().add(icon);

			iconHolder.setOnMouseClicked((event) -> {
				context.show(iconHolder, event.getScreenX(), event.getScreenY());
			});

			rebuildIconHolder();
		}

		this.setName(internal.getName());
		this.internal.getOnNameChangeEvent().connect((args) -> {
			this.setName((String) args[0]);
		});

		// Textfield renaming
		this.textField = new TextField(label.getText());
		this.textField.prefHeightProperty().bind(label.heightProperty());
		this.textField.setPadding(new Insets(0, 0, 0, 0));
		this.textField.setOnAction(event -> renameFinish());
		this.textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue) {
				if (textField.getText().trim().length() > 0)
					internal.setName(filter(textField.getText()));

				renameFinish();
			}
		});

		// Child Box
		childrenBox = new VBox();
		childrenBox.setPadding(new Insets(0,0,0,8));
		internal.getOnChildAddedEvent().connect((args)->{
			onChildAdded((DFolderElement) args[0]);
		});
		internal.getOnChildRemovedEvent().connect((args)->{
			onChildRemoved((DFolderElement) args[0]);
		});
		for (DFolderElement serviceChain : internal.getChildrenUnmodifyable()) {
			onChildAdded(serviceChain);
		}
	}
	
	protected String filter(String text) {
		return text;
	}

	protected void generateContextMenu(ContextMenu context) {
		// Add Service Chain
		{
			MenuItem option = new MenuItem("Add Service Chain", IconHelper.CHAIN.create());
			option.setOnAction((event) -> {
				setOpen(true);
				ServiceChainerApp.get().getData().newServiceChain(internal);
			});
			context.getItems().add(option);
		}

		// Add Folder
		{
			MenuItem option = new MenuItem("Add Folder", IconHelper.FOLDER.create());
			option.setOnAction((event) -> {
				setOpen(true);
				ServiceChainerApp.get().getData().newFolder(internal);
			});
			context.getItems().add(option);
		}
		
		context.getItems().add(new SeparatorMenuItem());

		// Import
		{
			MenuItem option = new MenuItem("Import", IconHelper.IMPORT.create());
			option.setOnAction((event) -> {
				ServiceChainerApp.get().importCollection(internal);
			});
			context.getItems().add(option);
		}

		// Export
		{
			MenuItem option = new MenuItem("Export", IconHelper.EXPORT.create());
			option.setOnAction((event) -> {
				File file = ServiceChainerApp.get().exportFilePicker();
				RouteHelper.export(RouteHelper.getServiceChains(Folder.this.internal), file, false);
			});
			context.getItems().add(option);
		}
		
		context.getItems().add(new SeparatorMenuItem());

		// Rename context
		{
			if ( internal.isDeletable() ) {
				MenuItem option = new MenuItem("Rename");
				option.setOnAction((event) -> {
					rename();
				});
				context.getItems().add(option);
			}
		}

		// Duplicate context
		{
			MenuItem option = new MenuItem("Duplicate");
			option.setOnAction((event) -> {
				ServiceChainerApp.get().getData().duplicate(this.internal);
			});
			context.getItems().add(option);
		}

		// Delete context
		{
			if ( internal.isDeletable() ) {
				MenuItem option = new MenuItem("Delete", IconHelper.DELETE.create());
				option.setOnAction((event) -> {
					this.internal.delete();
				});
				context.getItems().add(option);
			}
		}
	}

	private void onChildAdded(DFolderElement maybeServiceChain) {
		if ( maybeServiceChain instanceof DServiceChain ) {
			FolderElement label = new ServiceChain(internal, (DServiceChain) maybeServiceChain);
			label.prefWidthProperty().bind(childrenBox.widthProperty());
			childrenBox.getChildren().add((Node)label);
		} else if ( maybeServiceChain instanceof DFolder ) {
			FolderElement label = new Folder((DFolder)maybeServiceChain);
			label.prefWidthProperty().bind(childrenBox.widthProperty());
			childrenBox.getChildren().add((Node)label);
			
			//((DFolder)maybeServiceChain).setParent(this.internal);
		}
		
		updateChildrenLabel();
	}

	private void onChildRemoved(DFolderElement serviceChain) {
		for (int i = 0 ; i < childrenBox.getChildren().size(); i++) {
			if ( i >= childrenBox.getChildren().size() )
				continue;
			
			Node node = childrenBox.getChildren().get(i);
			if ( node == null )
				continue;
			
			if ( node instanceof FolderElement ) {
				if ( ((FolderElement)node).getFolderElement().equals(serviceChain) ) {
					childrenBox.getChildren().remove(node);
				}
			}
		}
		
		Platform.runLater(()->{
			updateChildrenLabel();
		});
	}

	private void updateChildrenLabel() {
		int children = internal.getChildrenUnmodifyable().size();
		if ( children == 1 ) {
			childrenLabel.setText("1 child");
		} else {
			childrenLabel.setText(children + " children");
		}
	}

	public void rename() {
		this.labelRenamePane.getChildren().clear();
		this.labelRenamePane.getChildren().add(textField);
		this.textField.setText(label.getText());

		this.textField.requestFocus();
		this.textField.selectAll();
	}

	private void renameFinish() {
		this.labelRenamePane.getChildren().clear();
		this.labelRenamePane.getChildren().add((Node)label);
	}

	private void rebuildIconHolder() {
		FontAwesomeIconView icon = new FontAwesomeIconView(
				open ? FontAwesomeIcon.CHEVRON_DOWN : FontAwesomeIcon.CHEVRON_RIGHT);
		icon.setFill(Color.color(0.3, 0.3, 0.4, 0.75));
		expandHolder.getChildren().clear();
		expandHolder.getChildren().add(icon);
	}

	private void toggleOpen() {
		open = !open;

		if (open) {
			this.getChildren().add(childrenBox);
		} else {
			this.getChildren().remove(childrenBox);
		}

		rebuildIconHolder();
	}

	public void setOpen(boolean open) {
		if (this.open == open)
			return;

		toggleOpen();
	}

	public void setName(String name) {
		FontPosture posture = internal.isDeletable()?FontPosture.REGULAR:FontPosture.ITALIC;
		label.setFont(Font.font(Font.getDefault().getFamily(), posture, label.getFont().getSize()));
		label.setText(name);
	}

	public DFolder getInternal() {
		return this.internal;
	}

	@Override
	public void setTextFill(Paint color) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getText() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setText(String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Font getFont() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setFont(Font font) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public DFolderElement getFolderElement() {
		return this.internal;
	}
}