package dev.anarchy.ui.control.filter;

import dev.anarchy.common.DFolderElement;
import javafx.beans.property.DoubleProperty;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

public interface FolderElement {
	DFolderElement getFolderElement();
	
	boolean computeVisible(String searchTerm);
}
