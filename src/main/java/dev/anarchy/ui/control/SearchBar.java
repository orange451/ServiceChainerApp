package dev.anarchy.ui.control;

import org.controlsfx.control.textfield.CustomTextField;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.geometry.Insets;
import javafx.scene.paint.Color;

public class SearchBar extends CustomTextField {
	public SearchBar() {
		this("");
	}
	
	public SearchBar(String text) {
		this.setText(text);
		this.setPromptText("Search");
		
        FontAwesomeIconView icon = new FontAwesomeIconView(FontAwesomeIcon.SEARCH);
        icon.setFill(Color.DARKGRAY);
        this.setLeft(icon);
        
        this.setPadding(new Insets(6, 6, 6, 6));
		this.setStyle("-fx-border-radius: 24 24; -fx-background-radius: 24 24;");
	}
}
