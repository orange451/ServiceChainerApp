package dev.anarchy.ui;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import dev.anarchy.common.DApp;
import dev.anarchy.common.DCollection;
import dev.anarchy.common.DFolder;
import dev.anarchy.common.DFolderElement;
import dev.anarchy.common.DServiceChain;
import dev.anarchy.ui.control.Workspace;
import dev.anarchy.ui.util.LaunchHelper;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Tab;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.FileChooser.ExtensionFilter;

public class ServiceChainerApp extends Application {
	
	private static ServiceChainerApp app;
	
	private ApplicationData data;
	
	private Workspace workspace;
	
	private Stage stage;
	
	private boolean cancelDeletes;
	
	@Override
	public void start(Stage stage) {
		LaunchHelper.checkCanLaunch();
		
		data = ApplicationData.load();
		app = this;
		this.stage = stage;
		
		workspace = new Workspace();
		
		// Build main UI
		ServiceChainerUIBuilder.build(stage);
		
		// Show
		stage.centerOnScreen();
		stage.show();
		
		// No closing
		stage.setOnCloseRequest((event)->{
			if ( !workspace.closeAll() ) {
				event.consume();
			}
		});
		
		// Delete event. TODO clean this up.
		DApp.get().getOnDeleteEvent().connect((args) -> {
			if ( cancelDeletes )
				return;
			
			DCollection collection = getData().getCollection((DFolderElement) args[0]);
			
			if ( args[0] instanceof DCollection ) {
				AtomicBoolean deleteFromDisk = new AtomicBoolean(false);
				ButtonType remove = requestRemoveFile(((DCollection) args[0]).getName(), deleteFromDisk);
				if ( remove == ButtonType.CANCEL ) {
					cancelDeletes = true;
					Platform.runLater(()->{
						cancelDeletes = false;
					});
					return;
				}
				
				// Remove it from list
				data.removeCollection(((DCollection)args[0]));
				
				// Delete it from disk maybe
				if ( deleteFromDisk.get() ) {
					this.getData().deleteCollection((DCollection) args[0]);
				}
			}else if ( args[0] instanceof DFolderElement ) { 
				DFolder parentNode = getData().getParent((DFolderElement) args[0]);
				
				AtomicBoolean deleteFromDisk = new AtomicBoolean(false);
				ButtonType remove = requestRemoveFile(((DFolderElement) args[0]).getName(), deleteFromDisk);
				if ( remove == ButtonType.CANCEL ) {
					cancelDeletes = true;
					Platform.runLater(()->{
						cancelDeletes = false;
					});
					return;
				}
				
				
				// Attempt to close
				if ( args[0] instanceof DServiceChain ) {
					Tab tab = getWorkspace().findTab((DServiceChain) args[0]);
					if ( tab != null ) {
						if ( !getWorkspace().close(tab) )
							return;
					}
					
					// Delete from disk
					if ( deleteFromDisk.get() ) {
						this.getData().deleteServiceChain((DServiceChain) args[0]);
					}
				}
				
				// Remove it from parent
				if ( parentNode != null )
					parentNode.removeChild(((DFolderElement)args[0]));

				// Save the collection (Most likely not necessary)
				this.getData().saveCollection(collection);
			} else {
				System.out.println("Attempting to delete an element that is not yet supported. Please implement.");
			}
		});
	}

	public static ServiceChainerApp get() {
		return app;
	}
	
	public ApplicationData getData() {
		return data;
	}
	
	public void saveCurrent() {
		workspace.save(workspace.getOpen());
	}
	
	public void modify() {
		DServiceChain chain = workspace.getOpen();
		if ( chain == null )
			return;
		
		chain.getOnChangedEvent().fire();
	}

	public void edit(DServiceChain internal) {
		workspace.open(internal);
	}

	public Workspace getWorkspace() {
		return this.workspace;
	}

	public Window getStage() {
		return stage;
	}

	/**
	 * Save current open tab in {@link Workspace}.
	 * Will also write service chains to file.
	 */
	public void save() {
		// Save current open chain
		this.saveCurrent();
		
		// Write to file
		this.getData().save();
	}

	/**
	 * Attempts to close all tabs in {@link Workspace}.
	 * @return Whether or not all the tabs were all closed.
	 */
	public boolean closeAll() {
		return this.getWorkspace().closeAll();
	}

	/**
	 * Opens FileChooser used to import collection. Takes resultant file and attempts to import.
	 * The collection will attempt to be placed within the supplied Parent Directory. If no directory
	 * is supplied, it will be placed in Unorganized.
	 * @param parentFolder
	 */
	public void importCollection(DFolder parentFolder) {
		File selectedFile = importFilePicker();
		data.importFile(selectedFile, parentFolder);
	}
	
	/**
	 * Opens FileChooser used to import collection.
	 * @return The File selected to import.
	 */
	public File importFilePicker() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Resource File");
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("JSON Files", "*.json"));
		File file = fileChooser.showOpenDialog(stage);
		
		return file;
	}

	/**
	 * Opens FileChooser used to export collection.
	 * @return The File selected to export.
	 */
	public File exportFilePicker() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Json files (*.json)", "*.json");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showSaveDialog(ServiceChainerApp.get().getStage());
        
        return file;
	}

	/**
	 * Prompts the user to save changes.
	 * @return ButtonType representing what the user selected. {@link ButtonType#YES}, {@link ButtonType#NO}, or {@link ButtonType#CANCEL}
	 */
	public ButtonType requestSave() {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you wish to save changes?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
		ServiceChainerUIBuilder.setTheme(alert.getDialogPane());
		alert.getDialogPane().getStylesheets().addAll(ServiceChainerUIBuilder.getStylesheet());
		ButtonType result = alert.showAndWait().orElse(ButtonType.NO);
		
		return result;
	}
	
	private ButtonType requestRemoveFile(String name, AtomicBoolean deleteFromDisk) {
		/*Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete " + name + " from disk?", ButtonType.YES, ButtonType.NO);
		ServiceChainerUIBuilder.setTheme(alert.getDialogPane());
		alert.getDialogPane().getStylesheets().addAll(ServiceChainerUIBuilder.getStylesheet());
		ButtonType result = alert.showAndWait().orElse(ButtonType.NO);
		
		return result == ButtonType.YES;*/
		
		// Create alert
		Alert alert = createAlertWithOptOut(AlertType.CONFIRMATION, "Delete", null, 
				"Are you sure you wish to remove " + name + "?", "Delete file contents from disk (cannot be undone)", 
				param -> deleteFromDisk.set(param), ButtonType.YES, ButtonType.CANCEL);
		
		// Get Result
		ButtonType result = alert.showAndWait().orElse(ButtonType.NO);
		return result;
	}
	
	public static Alert createAlertWithOptOut(AlertType type, String title, String headerText, String message, String optOutMessage, Consumer<Boolean> optOutAction, ButtonType... buttonTypes) {
		Alert alert = new Alert(type);
		// Need to force the alert to layout in order to grab the graphic,
		// as we are replacing the dialog pane with a custom pane
		alert.getDialogPane().applyCss();
		Node graphic = alert.getDialogPane().getGraphic();
		// Create a new dialog pane that has a checkbox instead of the hide/show details button
		// Use the supplied callback for the action of the checkbox
		alert.setDialogPane(new DialogPane() {
			@Override
			protected Node createDetailsButton() {
				CheckBox optOut = new CheckBox();
				optOut.setText(optOutMessage);
				optOut.setOnAction(e -> optOutAction.accept(optOut.isSelected()));
				return optOut;
			}
		});
		alert.getDialogPane().getButtonTypes().addAll(buttonTypes);
		alert.getDialogPane().setContentText(message);
		// Fool the dialog into thinking there is some expandable content
		// a Group won't take up any space if it has no children
		alert.getDialogPane().setExpandableContent(new Group());
		alert.getDialogPane().setExpanded(true);
		// Reset the dialog graphic using the default style
		alert.getDialogPane().setGraphic(graphic);
		alert.setTitle(title);
		alert.setHeaderText(headerText);
		return alert;
	}

	/**
	 * Prompts the user to save changes.
	 * @return ButtonType representing what the user selected. {@link ButtonType#YES}, {@link ButtonType#NO}, or {@link ButtonType#CANCEL}
	 */
	public ButtonType alert(AlertType alertType, String text) {
		System.err.println(text);
		
		Alert alert = new Alert(alertType, text, ButtonType.OK);
		ServiceChainerUIBuilder.setTheme(alert.getDialogPane());
		alert.getDialogPane().getStylesheets().addAll(ServiceChainerUIBuilder.getStylesheet());
		ButtonType result = alert.showAndWait().orElse(ButtonType.OK);
		return result;
	}

	/**
	 * Notifies the program to unminimize and be brought to front.
	 */
	public void wakeup() {
		Platform.runLater(()->{
			stage.setIconified(false);
			stage.toFront();
			
			// Hack
			stage.setAlwaysOnTop(true);
			stage.setAlwaysOnTop(false);
		});
	}

	public static void main(String[] args) {
		launch(args);
	}
}
