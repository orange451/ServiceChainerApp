package dev.anarchy.ui.control;

import dev.anarchy.common.DRouteElementI;
import dev.anarchy.common.DServiceChain;
import dev.anarchy.common.util.RouteHelper;
import dev.anarchy.ui.util.IconHelper;
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
	
	private Node linkTo;
	
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
			RouteHelper.linkRoutes(serviceChain.getRoutesUnmodifyable(), this.routeElement, null);
			this.setLinkTo(null);
		});
		
		this.setCursor(Cursor.HAND);
		this.setLinkTo(null);
	}
	
	public DRouteElementI getRouteElement() {
		return this.routeElement;
	}
	
	public void setLinkTo(Node newLink) {
		linkTo = newLink;

		this.getChildren().clear();
		
		if ( newLink != null ) {
			icon = IconHelper.CHAIN.create();
			this.getChildren().add(icon);
		} else {
			icon = IconHelper.CHAIN_BROKEN.create();
			this.getChildren().add(icon);
		}
		
		icon.setScaleX(3);
		icon.setScaleY(3);
		
		if ( parent != null )
			parent.getEditor().connectNodes();
	}
	
	public Node getLinkTo() {
		return this.linkTo;
	}
}