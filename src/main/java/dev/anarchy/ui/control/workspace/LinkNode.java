package dev.anarchy.ui.control.workspace;

import java.util.ArrayList;
import java.util.List;

import dev.anarchy.common.DConditionElement;
import dev.anarchy.common.DRouteElementI;
import dev.anarchy.common.DServiceChain;
import dev.anarchy.common.util.RouteHelper;
import dev.anarchy.ui.util.IconHelper;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;

public class LinkNode extends StackPane {
	private static final int WIDTH = 32;
	private static final int HEIGHT = 32;
	
	private List<Node> linkToNodes = new ArrayList<>();
	
	private Node icon;
	
	private DRouteElementI routeElement;
	
	private GraphObject parent;
	
	public LinkNode(DRouteElementI routeElement, DServiceChain serviceChain, GraphObject parent) {
		this.routeElement = routeElement;
		this.parent = parent;
		
		this.setPrefSize(WIDTH, HEIGHT);
		this.setMaxSize(WIDTH, HEIGHT);
		this.setMinSize(WIDTH, HEIGHT);
		this.setTranslateY(parent.getHeight()/2f);
		parent.heightProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				LinkNode.this.setTranslateY(parent.getHeight()/2f);
			}
		});
		parent.getChildren().add(this);
		
		this.setOnDragDetected((event)->{
			Dragboard db = this.startDragAndDrop(TransferMode.ANY);
	        ClipboardContent content = new ClipboardContent();
	        content.putString("asdf123"); // This is needed for javafx to recognise dragging... Kind of dumb.
	        db.setContent(content);
			
			event.setDragDetect(true);
			event.consume();
		});
		
		this.setOnMouseClicked((event)->{
			// Find Graph node we are connected to
			List<DRouteElementI> connectedToElements = RouteHelper.getLinkedTo(parent.getEditor().getGraphObjectRoutesUnmodifyable(), this.routeElement);
			for (DRouteElementI connectedToElement : connectedToElements) {
				GraphObject connectedToNode = parent.getEditor().getGraphObjectFromRoute(connectedToElement);
				connectedToNode.onDisconnectFrom(routeElement);
			}
			
			// Condition nodes need to unlink 
			if ( routeElement instanceof DConditionElement ) {
				DRouteElementI connectedFromElement = RouteHelper.getLinkedFrom(parent.getEditor().getGraphObjectRoutesUnmodifyable(), routeElement);
				if ( connectedFromElement != null ) {
					RouteHelper.linkRoutes(parent.getEditor().getGraphObjectRoutesUnmodifyable(), connectedFromElement, null);
				}
			}
			
			// unlink this route element
			RouteHelper.linkRoutes(parent.getEditor().getGraphObjectRoutesUnmodifyable(), this.routeElement, null);
			if ( this.routeElement instanceof DConditionElement )
				RouteHelper.disconnectCondition(parent.getEditor().getGraphObjectRoutesUnmodifyable(), (DConditionElement) this.routeElement);
			this.clearLinkTo();
			
			// Redraw path
			parent.getEditor().connectNodes();
		});

		// Establish initial connection
		Platform.runLater(()->{
			List<DRouteElementI> routeElementTo = RouteHelper.getLinkedTo(parent.getEditor().getGraphObjectRoutesUnmodifyable(), this.routeElement);
			for (DRouteElementI element : routeElementTo)
				addLinkTo(parent.getEditor().getGraphObjectFromRoute(element));
			update();
		});
		
		this.setCursor(Cursor.HAND);
	}
	
	public GraphObject getGraphObject() {
		return this.parent;
	}
	
	public DRouteElementI getRouteElement() {
		return this.routeElement;
	}
	
	public void addLinkTo(Node newLink) {
		if ( linkToNodes.contains(newLink) )
			return;
		
		linkToNodes.add(newLink);
		update();
	}
	
	public List<Node> getLinkToNodes() {
		return this.linkToNodes;
	}
	
	public void clearLinkTo() {
		this.linkToNodes.clear();
		update();
	}

	public void update() {
		this.getChildren().clear();
		
		if ( linkToNodes != null && linkToNodes.size() > 0 ) {
			icon = IconHelper.CHAIN.create();
			this.getChildren().add(icon);
		} else {
			icon = IconHelper.CHAIN_BROKEN.create();
			this.getChildren().add(icon);
		}
		
		icon.setScaleX(3);
		icon.setScaleY(3);
	}
}
