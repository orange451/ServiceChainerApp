package dev.anarchy.ui.control;

import dev.anarchy.common.DFolderElement;
import javafx.beans.property.DoubleProperty;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

public interface FolderElement {

	void setTextFill(Paint color);

	String getText();

	void setText(String name);

	Font getFont();

	void setFont(Font font);

	DoubleProperty prefWidthProperty();

	DFolderElement getFolderElement();

}
