package dev.anarchy.ui.control;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import dev.anarchy.common.DRouteElement;
import dev.anarchy.common.DRouteElementI;
import dev.anarchy.common.DServiceChain;
import dev.anarchy.common.DServiceDefinition;
import dev.anarchy.ui.util.ColorHelper;
import dev.anarchy.ui.util.IconHelper;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.StrokeLineCap;

public class ServiceChainEditor extends BorderPane {

	private Pane editPane;

	private GraphObject selectedNode;

	private List<CubicCurve> curves = new ArrayList<>();

	private List<GraphObject> nodes = new ArrayList<>();

	public ServiceChainEditor(DServiceChain internal) {
		BorderPane topBar = new BorderPane();
		topBar.setStyle("-fx-background-color: rgb(240,240,240);");
		topBar.setPadding(new Insets(8, 8, 8, 8));
		topBar.prefWidthProperty().bind(this.widthProperty());
		this.setTop(topBar);

		// Left top bar
		{
			HBox buttons = new HBox();
			buttons.setSpacing(6);
			Button newB = new Button("New Service Definition");
			newB.setOnAction((event) -> {
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
			topBar.setLeft(buttons);
		}

		// Right top bar
		{
			HBox buttons2 = new HBox();
			buttons2.setSpacing(6);
			
			Button play = new Button("", IconHelper.PLAY.create());
			play.setOnMouseClicked((event)->{
				new ServiceChainRunner(internal).show();
			});
			buttons2.getChildren().add(play);
			
			Button edit = new Button("", IconHelper.GEAR.create());
			buttons2.getChildren().add(edit);
			
			topBar.setRight(buttons2);
		}

		DropShadow dropShadow = new DropShadow();
		dropShadow.setRadius(5.0);
		dropShadow.setOffsetX(0.0);
		dropShadow.setOffsetY(3.0);
		dropShadow.setColor(Color.color(0.4, 0.5, 0.5));
		topBar.setEffect(dropShadow);

		ScrollPane scroll = new ScrollPane();
		scroll.setStyle(
				"-fx-background: transparent; -fx-border-color: transparent; -fx-background-color:transparent;");
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

		this.editPane.setOnMousePressed((event) -> {
			setSelectedNode(null);
		});

		// Entry node
		GraphObject entryNode = newRouteElementNode(internal, internal);
		entryNode.setCornerAsPercent();
		if (internal.getX() == 0 && internal.getY() == 0) {
			internal.setSize(140, 80);
			double x = round(editPane.getPrefWidth() / 2) - round(entryNode.getPrefWidth() / 2);
			double y = round(editPane.getPrefWidth() / 2 * 0.9125) - round(entryNode.getPrefHeight() / 2);
			internal.setPosition(x, y);
		}

		// Service Defs
		internal.getOnRouteAddedEvent().connect((args) -> {
			newRouteElementNode(internal, (DRouteElement) args[0]);
		});
		for (DRouteElementI element : internal.getRoutesUnmodifyable())
			newRouteElementNode(internal, element);

		internal.getOnRouteRemovedEvent().connect((args) -> {
			removeRouteElementNode(internal, (DRouteElement) args[0]);
		});
		
		new Thread(()->{
			try {Thread.sleep(50);} catch (InterruptedException e) {}
			Platform.runLater(()->{
				connectNodes();
			});
		}).start();
	}

	protected void clearCurves() {
		for (Node node : this.editPane.getChildrenUnmodifiable()) {
			if (node instanceof CubicCurve)
				this.editPane.getChildren().remove(node);
		}
		
		for (Node node : this.curves) {
			this.editPane.getChildren().remove(node);
		}

		curves.clear();
	}

	protected void connectNodes() {
		clearCurves();

		System.out.println("Reconnecting nodes");
		
		for (GraphObject node : nodes) {
			for (GraphObject conTo : nodes) {
				if (conTo == node)
					continue;

				String source = conTo.getRouteElement().getSourceId();
				String dest = node.getRouteElement().getDestinationId();
				if (source != null && source.equals(dest)) {
					connectNode(node, conTo);
					continue;
				}
			}
		}
	}

	private void connectNode(GraphObject from, GraphObject to) {

		CubicCurve curve = new CubicCurve();
		curve.setStroke(Color.FORESTGREEN);
		curve.setStrokeWidth(4);
		curve.setStrokeLineCap(StrokeLineCap.ROUND);
		curve.setFill(Color.TRANSPARENT);

		updateCurve(curve, from, to);

		from.widthProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				updateCurve(curve, from, to);
			}
		});

		from.heightProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				updateCurve(curve, from, to);
			}
		});

		from.translateXProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				updateCurve(curve, from, to);
			}
		});

		from.translateYProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				updateCurve(curve, from, to);
			}
		});
		
		to.widthProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				updateCurve(curve, from, to);
			}
		});

		to.heightProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				updateCurve(curve, from, to);
			}
		});

		to.translateXProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				updateCurve(curve, from, to);
			}
		});

		to.translateYProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				updateCurve(curve, from, to);
			}
		});

		this.editPane.getChildren().add(0, curve);
		curves.add(curve);

		from.getLinkNode().update();
	}

	private void updateCurve(CubicCurve curve, GraphObject from, GraphObject to) {
		double x1 = from.getTranslateX() + from.getWidth() / 2;
		double y1 = from.getTranslateY() + from.getHeight() / 2;
		double x2 = to.getTranslateX() + to.getWidth() / 2;
		double y2 = to.getTranslateY() + to.getHeight() / 2;

		curve.setStartX(x1);
		curve.setStartY(y1);
		curve.setControlX1(x1);
		curve.setControlY1(y2);
		curve.setControlX2(x2);
		curve.setControlY2(y1);
		curve.setEndX(x2);
		curve.setEndY(y2);
	}

	private void removeRouteElementNode(DServiceChain parent, DRouteElement routeElement) {
		List<Node> toRemove = new ArrayList<>();
		
		for (Node node : this.nodes) {
			if (node instanceof GraphObject) {
				if (routeElement.equals(((GraphObject) node).getRouteElement())) {
					this.editPane.getChildren().remove(node);
					toRemove.add(node);
				}
			}
		}
		
		for (Node node : toRemove)
			nodes.remove(node);
		
		Platform.runLater(()->{
			connectNodes();
		});
	}

	private GraphObject newRouteElementNode(DServiceChain parent, DRouteElementI routeElement) {
		GraphObject g = new GraphObject(this, parent, routeElement);
		g.setCornerRadius(8);

		updateRouteElement(routeElement, g);
		this.editPane.getChildren().add(g);

		routeElement.getOnChangedEvent().connect((args) -> {
			updateRouteElement(routeElement, g);
		});

		nodes.add(g);

		return g;
	}

	private void updateRouteElement(DRouteElementI routeElement, GraphObject g) {
		g.setFill(ColorHelper.fromHexString(routeElement.getColor()));
		g.setName(routeElement.getName());
		g.setPrefSize(routeElement.getWidth(), routeElement.getHeight());
		g.setTranslateX(routeElement.getX());
		g.setTranslateY(routeElement.getY());

		if (routeElement instanceof DServiceChain) {
			g.setName(((DServiceChain) routeElement).getHandlerId());
		}
	}

	private double round(double x) {
		return Math.floor(x / 20d) * 20d;
	}

	public void setSelectedNode(GraphObject newNode) {
		GraphObject oldNode = selectedNode;
		selectedNode = newNode;

		if (oldNode != null) {
			oldNode.setStyle("-fx-border-style: none;");
		}

		if (newNode != null) {
			newNode.setStyle(
					"-fx-border-color:orange; -fx-border-width: 3; -fx-border-style: segments(10, 10) line-cap square;");
		}
	}
	
	public List<GraphObject> getGraphObjectsUnmodifyable() {
		return Arrays.asList(nodes.toArray(new GraphObject[nodes.size()]));
	}
	
	public List<DRouteElementI> getGraphObjectRoutesUnmodifyable() {
		List<DRouteElementI> routes = new ArrayList<DRouteElementI>();
		for (GraphObject g : nodes) {
			if ( g.getRouteElement() != null )
				routes.add(g.getRouteElement());
		}
		
		return Arrays.asList(routes.toArray(new DRouteElementI[routes.size()]));
	}

	public GraphObject getGraphObjectFromRoute(DRouteElementI routeElement) {
		if ( routeElement == null )
			return null;
		
		for (Node node : this.editPane.getChildrenUnmodifiable()) {
			if (node instanceof GraphObject) {
				if (((GraphObject) node).getRouteElement().equals(routeElement)) {
					return (GraphObject) node;
				}
			}
		}
		
		return null;
	}
}
