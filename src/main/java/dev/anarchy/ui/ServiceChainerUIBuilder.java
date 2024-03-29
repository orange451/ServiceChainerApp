package dev.anarchy.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.jangassen.MenuToolkit;
import dev.anarchy.common.DCollection;
import dev.anarchy.ui.control.filter.Collection;
import dev.anarchy.ui.control.filter.FolderElement;
import dev.anarchy.ui.control.filter.SearchBar;
import dev.anarchy.ui.control.workspace.Workspace;
import dev.anarchy.ui.util.IconHelper;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class ServiceChainerUIBuilder {
	
	private static boolean building;
	
	protected static void build(Stage stage) {
		building = true;
		
		BorderPane root = new BorderPane();
		root.setPrefSize(1152, 648);
		stage.setScene(new Scene(root));
		
		// Menu
		menu(stage);
		
		SplitPane split = new SplitPane();
		split.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
	    split.setStyle("-fx-background-color: transparent, -fx-background; -fx-padding: 0; -fx-background-insets: 0, 0;");
		root.setCenter(split);
		
		Pane filter = filter();
		SplitPane.setResizableWithParent(filter, false);
		
		Workspace workspace = ServiceChainerApp.get().getWorkspace();
		split.getItems().addAll(filter, workspace);
		
		Platform.runLater(()-> {
			split.setDividerPosition(0, 0);
			root.requestFocus();
			building = false;
		});
		
		accelerators(stage);
		
		setIcon(stage);

		setTheme(stage.getScene());
	}
	
	protected static String getExternalResource(String resourceName) {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		try {
			return classLoader.getResource(resourceName).toExternalForm();
		} catch(Exception e) {
			return null;
		}
	}
	
	protected static List<String> getStylesheet() {
		
		// List of stylesheets we want to load
		List<String> styleSheets = Arrays.asList(
			"stylesheet/bootstrap2.css",
			"stylesheet/style.css"
		);
		
		// Styles in IDE
		List<String> styles = new ArrayList<>();
		styles.addAll(styleSheets);
		
		// Styles outside of IDE (external)
		for (String style : styleSheets) {
			String externalStyle = "resources/" + style;
			String externalResource = getExternalResource(externalStyle);
			if ( externalResource != null ) {
				styles.add(externalStyle);
			}
		}
		
		return styles;
	}

	public static void setTheme(Parent scene) {
		try {
			scene.getStylesheets().addAll(getStylesheet());
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}

	public static void setTheme(Scene scene) {
		try {
			scene.getStylesheets().addAll(getStylesheet());
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}

	private static void accelerators(Stage stage) {
		// Save
		stage.getScene().getAccelerators().put(
			new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN), () -> {
				ServiceChainerApp.get().saveCurrent();
			}
		);
	}

	private static void menu(Stage stage) {
		// MAC Menu bar
		{
	    	MenuToolkit tk = MenuToolkit.toolkit();
	    	Menu defaultApplicationMenu = tk.createDefaultApplicationMenu("Service Chainer");
	    	tk.setApplicationMenu(defaultApplicationMenu);
	    	//defaultApplicationMenu.
	    	//defaultApplicationMenu.getItems().get(1).setText("Hide all the otters");
		}
		
		MenuBar menuBar = new MenuBar();
		final String os = System.getProperty("os.name");
		if (os != null && os.startsWith("Mac"))
		  menuBar.useSystemMenuBarProperty().set(true);
		
		Menu file = new Menu("File");
		menuBar.getMenus().add(file);
		
		MenuItem close = new MenuItem("Close");
		file.getItems().add(close);
		close.setOnAction((event)->{
			stage.setIconified(true);
		});
		
		{
			file.getItems().add(new SeparatorMenuItem());
			
			MenuItem impCol = new MenuItem("Import Collection");
			file.getItems().add(impCol);
			impCol.setOnAction((event)->{
				ServiceChainerApp.get().importCollectionDirectory();
			});
			
			MenuItem imp = new MenuItem("Import");
			file.getItems().add(imp);
			imp.setOnAction((event)->{
				ServiceChainerApp.get().importCollection(null);
			});
			
			file.getItems().add(new SeparatorMenuItem());
		}
		
		MenuItem quit = new MenuItem("Quit");
		file.getItems().add(quit);
		quit.setOnAction((event)->{
			if ( ServiceChainerApp.get().closeAll() ) {
				System.exit(0);
			}
		});
		
		((BorderPane)stage.getScene().getRoot()).setTop(menuBar);
	}

	private static void setIcon(Stage stage) {
		// Set Javafx8 icon
		try {
			stage.getIcons().add(new Image("images/icon.png"));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private static Pane filter() {
		VBox pane = new VBox();
		pane.setPrefHeight(Double.MAX_VALUE);
		pane.getStyleClass().add("Filter-Base");
		pane.setSpacing(8);
		pane.setMinWidth(200);
		pane.setMaxWidth(400);
	
		// Top bar
		VBox v = new VBox();
		v.getStyleClass().add("Filter-Topbar");
		v.setSpacing(8);
		
		pane.getChildren().add(v);
		
		SearchBar search = new SearchBar();
		
		v.setPadding(new Insets(8,8,8,8));
		v.getChildren().add(search);
		
		Label newCollection = new Label("+ New Collection");
		newCollection.setStyle("-fx-text-fill: #F5823A");
		v.getChildren().add(newCollection);
		
		DropShadow dropShadow = new DropShadow();
		dropShadow.setRadius(5.0);
		dropShadow.setOffsetX(0.0);
		dropShadow.setOffsetY(1.0);
		dropShadow.setColor(Color.color(0.4, 0.5, 0.5));
		v.setEffect(dropShadow);
		
		// Collections
		ScrollPane scroll = new ScrollPane();
		scroll.setPadding(new Insets(0,0,1,0));
		scroll.fitToWidthProperty().set(true);
		scroll.fitToHeightProperty().set(true);
		scroll.hbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.NEVER);
		scroll.vbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		scroll.setStyle("-fx-background: transparent; -fx-background-color:transparent;");
		scroll.setFitToHeight(true);
		scroll.setFitToWidth(true);
		pane.getChildren().add(scroll);
		
		VBox elements = new VBox();
		elements.getStyleClass().add("Collection-Elements");
		elements.setSpacing(1);
		elements.setPadding(new Insets(0,0,1,0));
		scroll.setContent(elements);
		
		// Add new collections to UI
		ServiceChainerApp.get().getData().getOnCollectionAddedEvent().connect((args)->{
			newCollection(elements, scroll, (DCollection) args[0]);
		});
		for (DCollection collection : ServiceChainerApp.get().getData().getCollectionsUnmodifyable())
			newCollection(elements, scroll, collection);
		
		// Remove collections from UI
		ServiceChainerApp.get().getData().getOnCollectionRemovedEvent().connect((args)->{
			DCollection collection = (DCollection) args[0];
			
    		synchronized(elements.getChildren()) {
        		for (Node node : elements.getChildren()) {
        			if ( node instanceof Collection ) {
        				Collection uiCollection = (Collection)node;
        				if ( uiCollection.getInternal().equals(collection) ) {
        					elements.getChildren().remove(uiCollection);
        				}
        			}
        		}
    		}
		});
		
		// Search implementation
	    search.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
    		for (Node node : elements.getChildren()) {
    			if ( node instanceof FolderElement ) {
    				FolderElement element = (FolderElement)node;
    				element.computeVisible(newValue);
    			}
    		}
	    });
		
		// Add new collections on click
		newCollection.setOnMouseClicked((event)->{
			ServiceChainerApp.get().getData().newCollection();
		});
		
		return pane;
	}
	
	private static void newCollection(VBox elements, ScrollPane scroll, DCollection dcoll) {
		Collection collection = new Collection(dcoll);
		elements.getChildren().add(collection);
		if ( !building )
			collection.rename();
		
		checkGit(dcoll, collection);
		dcoll.getOnNameChangeEvent().connect((args) -> checkGit(dcoll, collection));
		dcoll.getOnChildAddedEvent().connect((args) -> checkGit(dcoll, collection));
		dcoll.getOnChildRemovedEvent().connect((args) -> checkGit(dcoll, collection));

		// Why does JavaFX not want people to use it...
		// Platform runlater doesn't wait for next frame...
		// We have to literally WAIT until the next frame in milliseconds...
		Platform.runLater(()->{
			try {
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						ensureVisible(scroll, collection);
					}
				}).start();
			} catch(Exception e) {
				//
			}
		});
	}
	
	private static void checkGit(DCollection collectionData, Collection collectionNode) {
		File collectionDirectory = ServiceChainerApp.get().getData().getDirectory(collectionData);
		File git = new File(collectionDirectory, ".git");
		if ( git.exists() ) {
			collectionNode.setGraphic(IconHelper.GIT.create());
		} else {
			collectionNode.setGraphic(null);
		}
	}

	private static void ensureVisible(ScrollPane scrollPane, Node node) {
	    double h = scrollPane.getContent().getBoundsInLocal().getHeight();
	    double y = (node.getBoundsInParent().getMaxY() + 
	                node.getBoundsInParent().getMinY()) / 2.0;
	    double v = scrollPane.getViewportBounds().getHeight();
	    scrollPane.setVvalue(scrollPane.getVmax() * ((y - 0.5 * v) / (h - v)));
	}
}
