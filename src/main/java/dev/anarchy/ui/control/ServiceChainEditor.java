package dev.anarchy.ui.control;

import dev.anarchy.DRouteElement;
import dev.anarchy.DRouteElementBase;
import dev.anarchy.DServiceChain;
import dev.anarchy.DServiceDefinition;
import dev.anarchy.ui.util.ColorHelper;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class ServiceChainEditor extends BorderPane {

	private Pane editPane;

	public ServiceChainEditor(DServiceChain internal) {
		Pane topBar = new StackPane();
		topBar.setStyle("-fx-background-color: rgb(240,240,240);");
		topBar.setPadding(new Insets(8, 8, 8, 8));
		topBar.prefWidthProperty().bind(this.widthProperty());
		this.setTop(topBar);

		HBox buttons = new HBox();
		Button newB = new Button("New Service Definition");
		newB.setOnAction((event)->{
			DServiceDefinition sDef = new DServiceDefinition();
			sDef.setExtensionHandlerRouteId("Service Definition");
			sDef.setColor(ColorHelper.toHexString(Color.DARKCYAN));
			sDef.setSize(220, 60);
			double x = round(editPane.getPrefWidth() / 2) - round(sDef.getWidth() / 2);
			double y = round(editPane.getPrefWidth() / 2) - round(sDef.getHeight() / 2);
			sDef.setPosition(x, y);
			internal.addRoute(sDef);
		});
		buttons.getChildren().add(newB);
		topBar.getChildren().add(buttons);

		DropShadow dropShadow = new DropShadow();
		dropShadow.setRadius(5.0);
		dropShadow.setOffsetX(0.0);
		dropShadow.setOffsetY(3.0);
		dropShadow.setColor(Color.color(0.4, 0.5, 0.5));
		topBar.setEffect(dropShadow);

		ScrollPane scroll = new ScrollPane();
		scroll.setStyle("-fx-background: transparent; -fx-border-color: transparent; -fx-background-color:transparent;");
		scroll.setPadding(Insets.EMPTY);
		scroll.setBorder(Border.EMPTY);
		scroll.setHvalue(0.5);
		scroll.setVvalue(0.5);
		this.setCenter(scroll);

		this.editPane = new Pane();
		this.editPane.setPrefSize(2048, 2048);
		this.editPane.setStyle("-fx-background-color: rgba(150, 150, 150, 0.1),"
				+ "linear-gradient(from 0.5px 0.0px to 10.5px  0.0px, repeat, rgba(102, 128, 128, 0.33) 5%, transparent 5%),"
				+ "linear-gradient(from 0.0px 0.5px to  0.0px 10.5px, repeat, rgba(102, 128, 128, 0.33) 5%, transparent 5%);");
		scroll.setContent(this.editPane);
		
		// Entry node
		GraphObject entryNode = newRouteElementNode(internal, internal);
		entryNode.setCornerAsPercent();
		if ( internal.getX() == 0 && internal.getY() == 0 ) {
			internal.setSize(140, 80);
			double x = round(editPane.getPrefWidth() / 2) - round(entryNode.getPrefWidth() / 2);
			double y = round(editPane.getPrefWidth() / 2 * 0.9125) - round(entryNode.getPrefHeight() / 2);
			internal.setPosition(x, y);
		}
		
		// Service Defs
		internal.getOnRouteAddedEvent().connect((args)->{
			newRouteElementNode(internal, (DRouteElement) args[0]);
		});
		for (DRouteElement element : internal.getRoutesUnmodifyable())
			newRouteElementNode(internal, element);
		
		internal.getOnRouteRemovedEvent().connect((args)->{
			remoteRouteElementNode(internal, (DRouteElement) args[0]);
		});
	}

	private void remoteRouteElementNode(DServiceChain parent, DRouteElement routeElement) {
		for (Node node : this.editPane.getChildrenUnmodifiable()) {
			if ( node instanceof GraphObject ) {
				if (((GraphObject)node).getRouteElement().equals(routeElement) ) {
					this.editPane.getChildren().remove(node);
				}
			}
		}
	}

	private GraphObject newRouteElementNode(DServiceChain parent, DRouteElementBase routeElement) {
		GraphObject g = new GraphObject(parent, routeElement);
		g.setCornerRadius(8);
		
		updateRouteElement(routeElement, g);
		this.editPane.getChildren().add(g);
		
		routeElement.getOnChangedEvent().connect((args)->{
			updateRouteElement(routeElement, g);
		});
		
		return g;
	}
	
	private void updateRouteElement(DRouteElementBase routeElement, GraphObject g) {
		g.setFill(ColorHelper.fromHexString(routeElement.getColor()));
		g.setName(routeElement.getName());
		g.setPrefSize(routeElement.getWidth(), routeElement.getHeight());
		g.setTranslateX(routeElement.getX());
		g.setTranslateY(routeElement.getY());
		
		if ( routeElement instanceof DServiceChain ) {
			g.setName(((DServiceChain)routeElement).getHandlerId());
		}
	}

	private double round(double x) {
		return Math.floor(x / 20d) * 20d;
	}
}
