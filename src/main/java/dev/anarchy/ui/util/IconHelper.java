package dev.anarchy.ui.util;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.scene.Node;
import javafx.scene.paint.Color;

public enum IconHelper {

	DELETE(FontAwesomeIcon.TRASH),
	CUT(FontAwesomeIcon.CUT),
	COPY(FontAwesomeIcon.COPY),
	PASTE(FontAwesomeIcon.PASTE),
	CHAIN(FontAwesomeIcon.LINK),
	CHAIN_BROKEN(FontAwesomeIcon.UNLINK),
	FOLDER(FontAwesomeIcon.FOLDER),
	EDIT(FontAwesomeIcon.EDIT),
	GEAR(FontAwesomeIcon.GEARS),
	PLAY(FontAwesomeIcon.PLAY),
	;
	
	IconHelper(FontAwesomeIcon icon) {
		this.icon = icon;
	}

	private FontAwesomeIcon icon;
	
	public Node create() {
		FontAwesomeIconView icon = new FontAwesomeIconView(this.icon);
		icon.setFill(Color.color(0.3, 0.3, 0.4, 0.75));
		return icon;
	}

}
