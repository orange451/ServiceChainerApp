package dev.anarchy.ui;

import dev.anarchy.common.DCollection;
import dev.anarchy.ui.control.Collection;
import dev.anarchy.ui.control.SearchBar;
import dev.anarchy.ui.control.Workspace;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class AnarchyAppUI {
	
	protected static void build(Stage stage) {
		BorderPane root = new BorderPane();
		root.setPrefSize(1152, 648);
		stage.setScene(new Scene(root));
		
		SplitPane split = new SplitPane();
		split.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
	    split.setStyle("-fx-background-color: transparent, -fx-background; -fx-padding: 0; -fx-background-insets: 0, 0;");
		root.setCenter(split);
		
		Pane filter = filter();
		SplitPane.setResizableWithParent(filter, false);
		
		Workspace workspace = AnarchyApp.get().getWorkspace();
		split.getItems().addAll(filter, workspace);
		
		Platform.runLater(()-> {
			split.setDividerPosition(0, 0);
		});
	}

	private static Pane filter() {
		VBox pane = new VBox();
		pane.setPrefHeight(Double.MAX_VALUE);
		pane.setSpacing(8);
		pane.setMinWidth(200);
		pane.setMaxWidth(400);
	
		// Top bar
		VBox v = new VBox();
		v.setStyle("-fx-background-color: rgb(245,245,245);");
		v.setSpacing(8);
		
		pane.getChildren().add(v);
		
		v.setPadding(new Insets(8,8,8,8));
		v.getChildren().add(new SearchBar());
		
		Label newCollection = new Label("\u2795 New Collection");
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
		elements.setStyle("-fx-background-color:rgb(200, 200, 200);");
		elements.setSpacing(1);
		elements.setPadding(new Insets(0,0,1,0));
		scroll.setContent(elements);
		
		// Add new collections to UI
		AnarchyApp.get().getData().getOnCollectionAddedEvent().connect((args)->{
			newCollection(elements, scroll, (DCollection) args[0]);
		});
		for (DCollection collection : AnarchyApp.get().getData().getCollectionsUnmodifyable())
			newCollection(elements, scroll, collection);
		
		// Remove collections from UI
		AnarchyApp.get().getData().getOnCollectionRemovedEvent().connect((args)->{
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
		
		// Add new collections on click
		newCollection.setOnMouseClicked((event)->{
			DCollection collection = new DCollection();
			collection.setName("New Collection");
			AnarchyApp.get().getData().addCollection(collection);
		});
		
		return pane;
	}
	
	private static void newCollection(VBox elements, ScrollPane scroll, DCollection dcoll) {
		Collection collection = new Collection(dcoll);
		elements.getChildren().add(collection);
		collection.rename();

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
	
	private static void ensureVisible(ScrollPane scrollPane, Node node) {
	    double h = scrollPane.getContent().getBoundsInLocal().getHeight();
	    double y = (node.getBoundsInParent().getMaxY() + 
	                node.getBoundsInParent().getMinY()) / 2.0;
	    double v = scrollPane.getViewportBounds().getHeight();
	    scrollPane.setVvalue(scrollPane.getVmax() * ((y - 0.5 * v) / (h - v)));
	}
}
