package dev.anarchy.ui.control.workspace;

import dev.anarchy.common.DConditionElement;
import dev.anarchy.common.DRouteElement;
import dev.anarchy.common.DRouteElementI;
import dev.anarchy.common.DServiceChain;
import dev.anarchy.common.DServiceDefinition;
import dev.anarchy.common.util.RouteHelper;
import dev.anarchy.ui.control.workspace.servicechain.ServiceChainConfigurator;
import dev.anarchy.ui.control.workspace.servicechain.ServiceChainEditor;
import dev.anarchy.ui.control.workspace.servicechain.ServiceChainRunner;
import dev.anarchy.ui.control.workspace.servicechain.ServiceDefinitionEditor;
import dev.anarchy.ui.control.workspace.servicechain.TemplateEditor;
import dev.anarchy.ui.control.workspace.servicechain.TemplateEditorType;
import dev.anarchy.ui.util.IconHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class GraphObject extends StackPane {

	private final Label label;
	
	private Color fill;
	
	private double cornerRadius;
	
	private DRouteElementI routeElement;
	
	private ServiceChainEditor editor;
	
	private LinkNode linkerNode;
	
	private DServiceChain serviceChain;
	
	private ContextMenu context;

	private double clickOffsetX;
	private double clickOffsetY;
	
	public GraphObject(ServiceChainEditor editor, DServiceChain serviceChain, DRouteElementI routeElement) {
		this.setAlignment(Pos.CENTER);
		this.setPrefSize(140, 80);
		
		this.editor = editor;
		this.routeElement = routeElement;
		this.serviceChain = serviceChain;

		DropShadow shadow = new DropShadow();
		shadow.setRadius(4.0);
		shadow.setOffsetX(0.0);
		shadow.setOffsetY(2.0);
		shadow.setColor(Color.color(0.2, 0.25, 0.25));
		this.setEffect(shadow);
		
		this.setFill(Color.color(0.196, 0.6274, 0.235));
		this.setCornerRadius(256);

		label = new Label("Node");
		label.setMouseTransparent(true);
		this.getChildren().add(label);
		
		linkerNode = new LinkNode(routeElement, serviceChain, this);
		
		this.setOnMouseDragged(event -> {
			if ( event.getButton() != MouseButton.PRIMARY )
				return;
			
			if ( !event.getTarget().equals(this) )
				return;
			
			double hWid = this.getWidth()/2;
			double hHei = this.getHeight()/2;
			double x = round(event.getX() - clickOffsetX + this.getTranslateX() - hWid);
			double y = round(event.getY() - clickOffsetY + this.getTranslateY() - hHei);
			this.setTranslateX(x);
			this.setTranslateY(y);
			routeElement.setPosition(x, y);
			event.consume();
		});
		
		this.setOnDragOver(event -> {
			/* data is dragged over the target */
			if (event.getGestureSource() != this ) {
				event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
			}

			event.consume();
		});
		
		this.setOnDragDropped(event -> {
			if ( event.getGestureSource() instanceof LinkNode ) {
				LinkNode droppedFrom = (LinkNode)event.getGestureSource();
				if ( this.getRouteElement() instanceof DRouteElement ) {
					RouteHelper.linkRoutes(editor.getGraphObjectRoutesUnmodifyable(), droppedFrom.getRouteElement(), (DRouteElement) this.getRouteElement());
					droppedFrom.setLinkTo(this.linkerNode);
					this.onConnectFrom(droppedFrom.getRouteElement());
					
					getEditor().connectNodes();
				} else {
					System.out.println("Cannot link " + droppedFrom.getRouteElement() + " to " + this.getRouteElement() + ". Element must be a RouteElement");
				}
			}
		});
		
		this.setOnMousePressed((event)->{
	        if(event.getButton().equals(MouseButton.PRIMARY)){
				double hWid = this.getWidth()/2;
				double hHei = this.getHeight()/2;
	        	clickOffsetX = event.getX()-hWid;
	        	clickOffsetY = event.getY()-hHei;
				editor.setSelectedNode(this);
				event.consume();
	        }
		});
		
		this.context = new ContextMenu();
		this.context.setAutoHide(true);
		this.context.setHideOnEscape(true);

		this.context.getItems().clear();
		updateContext(this.context);

		// Show context
		this.setOnMouseClicked((event) -> {
	        if(event.getButton().equals(MouseButton.PRIMARY)){
	            if(event.getClickCount() == 2){
	            	if ( routeElement instanceof DServiceDefinition ) {
		        		new ServiceDefinitionEditor((DServiceDefinition) routeElement).show();
	            	} else if ( routeElement instanceof DServiceChain ) {
	            		new ServiceChainConfigurator((DServiceChain) routeElement).show();
	            	}
	            	onDoubleClick();
	            }
	        } else if (event.getButton() == MouseButton.SECONDARY) {
				if (!context.isShowing()) {
					this.context.getItems().clear();
					updateContext(this.context);
					context.show(this, event.getScreenX(), event.getScreenY());
				}
			}
		});
		
		update();
	}
	
	protected void updateContext(ContextMenu context) {

		// CONFIGURE SERVICE CHAIN
		if ( routeElement instanceof DServiceChain ) {
			MenuItem option = new MenuItem("Configure", IconHelper.GEAR.create());
			option.setOnAction((event) -> {
				new ServiceChainConfigurator((DServiceChain) routeElement);
				//new ServiceDefinitionEditor((DServiceDefinition) routeElement).show();
			});
			context.getItems().add(option);
		}
		
		// TEST SERVICE CHAIN
		if ( routeElement instanceof DServiceChain ) {
			MenuItem option = new MenuItem("Test", IconHelper.PLAY.create());
			option.setOnAction((event) -> {
				new ServiceChainRunner((DServiceChain) routeElement).show();
			});
			context.getItems().add(option);
		}
		
		String inputType = "[None]";
		if ( routeElement instanceof DServiceDefinition ) {
			DServiceDefinition serviceDef = (DServiceDefinition)routeElement;
			String type = serviceDef.getTransformationType();
			
			if ( serviceDef.getTemplateContent() != null && serviceDef.getTemplateContent().length() > 1 )
				inputType = "[" + type + "]";
		}
		
		// options context
		if ( routeElement instanceof DServiceDefinition ) {
			MenuItem option = new MenuItem("Configure", IconHelper.GEAR.create());
			option.setOnAction((event) -> {
				new ServiceDefinitionEditor((DServiceDefinition) routeElement).show();
			});
			context.getItems().add(option);
		}
		
		// Edit context
		if ( routeElement instanceof DServiceDefinition ) {
			MenuItem option = new MenuItem("Edit Input Template\t\t" + inputType, IconHelper.EDIT.create());
			option.setOnAction((event) -> {
				new TemplateEditor(TemplateEditorType.INPUT, (DServiceDefinition) routeElement).show();
			});
			context.getItems().add(option);
		}
		
		context.getItems().add(new SeparatorMenuItem());
		
		// Delete context
		if ( !(routeElement instanceof DServiceChain) ) {
			MenuItem option = new MenuItem("Delete", IconHelper.DELETE.create());
			option.setOnAction((event) -> {
				serviceChain.removeRoute((DRouteElement) routeElement);
				onDelete();
			});
			context.getItems().add(option);
		}
	}
	
	protected void onDelete() {
		//
	}
	
	protected void onDoubleClick() {
		//
	}
	
	protected void onDisconnectFrom(DRouteElementI routeElement) {
		/*if ( this.routeElement instanceof DServiceDefinition && routeElement instanceof DConditionElement ) {
			System.err.println("Clearing condition meta: " + routeElement);
			((DServiceDefinition)this.routeElement).setCondition(null);
			((DServiceDefinition)this.routeElement).setConditionMeta(null);
		}*/
	}
	
	protected void onConnectFrom(DRouteElementI routeElement) {
		/*// Attach Condition nodes to child
		if (this.routeElement instanceof DServiceDefinition && routeElement instanceof DConditionElement) {
			DConditionElement condition = (DConditionElement)routeElement;
			System.err.println("Setting condition meta from: " + condition + " / " + condition.getConditionMeta());
			
			((DServiceDefinition)this.routeElement).setCondition(condition.getCondition());
			((DServiceDefinition)this.routeElement).setConditionMeta(condition.getConditionMeta());
			((DServiceDefinition)this.routeElement).getConditionMeta().setLinkedToId(this.routeElement.getDestinationId());
			DRouteElementI linkedFrom = RouteHelper.getLinkedFrom(this.editor.getGraphObjectRoutesUnmodifyable(), routeElement);
			System.err.println("Connecting from condition node. Condition node attached from: " + linkedFrom);
			if ( linkedFrom != null ) {
				RouteHelper.linkRoutes(this.editor.getGraphObjectRoutesUnmodifyable(), linkedFrom, (DRouteElement) this.routeElement);
				
				// Test
				((DRouteElement) routeElement).setSourceId(linkedFrom.getDestinationId());
				((DRouteElement) this.routeElement).setSourceId(routeElement.getDestinationId());
				
				this.editor.connectNodes();
			}
		}
		
		// Route parent node THROUGH condition nodes to child
		if ( this.routeElement instanceof DConditionElement ) {
			DRouteElementI linkedTo = RouteHelper.getLinkedTo(this.editor.getGraphObjectRoutesUnmodifyable(), this.routeElement);
			if ( linkedTo != null ) {
				System.err.println("Attempting to link condition node. From: " + routeElement + " to " + linkedTo);
				RouteHelper.linkRoutes(this.editor.getGraphObjectRoutesUnmodifyable(), routeElement, (DRouteElement) linkedTo);
				this.editor.connectNodes();
			}
		}*/
	}
	
	public DServiceChain getServiceChain() {
		return this.serviceChain;
	}

	public void setCornerRadius(double x) {
		this.cornerRadius = x;
		update();
	}

	public void setFill(Color color) {
		this.fill = color;
		update();
	}
	
	private boolean percentCorner = false;
	public void setCornerAsPercent() {
		percentCorner = true;
		update();
	}

	private void update() {
		this.setBackground(new Background(new BackgroundFill(fill, new CornerRadii(cornerRadius,cornerRadius,cornerRadius,cornerRadius, percentCorner), Insets.EMPTY)));
	}

	private double round(double x) {
		return Math.floor(x / 20d) * 20d;
	}

	public void setName(String string) {
		this.label.setText(string);
	}

	public DRouteElementI getRouteElement() {
		return routeElement;
	}

	public ServiceChainEditor getEditor() {
		return this.editor;
	}

	public LinkNode getLinkNode() {
		return this.linkerNode;
	}
}
