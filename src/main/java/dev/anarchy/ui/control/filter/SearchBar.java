package dev.anarchy.ui.control.filter;

import org.controlsfx.control.textfield.CustomTextField;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class SearchBar extends CustomTextField {
	public SearchBar() {
		this("");
	}
	
	public SearchBar(String text) {
		this.setText(text);
		this.setPromptText("Search");
		
		// Search icon
		{
	        FontAwesomeIconView icon = new FontAwesomeIconView(FontAwesomeIcon.SEARCH);
	        icon.setFill(Color.DARKGRAY);
	        
	        StackPane filler = new StackPane();
	        filler.setPadding(new Insets(0,0,0,8));
	        filler.setAlignment(Pos.CENTER_RIGHT);
	        filler.getChildren().add(icon);
	        this.setLeft(filler);
		}

		// Clear Icon
		{
	        FontAwesomeIconView clear = new FontAwesomeIconView(FontAwesomeIcon.TIMES_CIRCLE);
	        clear.setFill(Color.DARKGRAY);
			clear.setCursor(Cursor.HAND);
	        this.setRight(clear);
	        
	        clear.setOnMouseReleased((event)->{
	        	this.setText("");
	        });
		}
        
        this.setPadding(new Insets(6, 6, 6, 3));
		this.setStyle("-fx-border-radius: 24 24; -fx-background-radius: 24 24;");
	}
}
