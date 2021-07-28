package dev.anarchy.ui.control;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.input.MouseEvent;

public class ButtonTab extends Tab {
    private final Node label;
    
    private EventHandler<? super MouseEvent> onMouseClickedEvent;

    public ButtonTab(Node label) {
        this(label, null);
    }

    public ButtonTab(Node label, Node content) {
        super();
        this.label = label;
        
        setContent(content);
        setGraphic(label);
        label.setOnMouseClicked((mouseEvent) -> {
        	if ( onMouseClickedEvent != null ) {
        		onMouseClickedEvent.handle(mouseEvent);
        	}
        });
    }
    
    public void setOnMouseClicked(EventHandler<? super MouseEvent> onMouseClickedEvent) {
    	this.onMouseClickedEvent = onMouseClickedEvent;
    }
}