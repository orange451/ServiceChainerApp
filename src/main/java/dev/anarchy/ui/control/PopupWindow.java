package dev.anarchy.ui.control;

import dev.anarchy.ui.AnarchyApp;
import javafx.stage.Modality;
import javafx.stage.Stage;

public abstract class PopupWindow {
	
	protected Stage toolStage;

	public PopupWindow() {
        this(false);
	}

	public PopupWindow(boolean modal) {
        toolStage = new Stage();
        toolStage.initOwner(AnarchyApp.get().getStage());
        if ( modal )
        	toolStage.initModality(Modality.WINDOW_MODAL);
        
        start(toolStage);
	}
	
	public void show() {
        toolStage.centerOnScreen();
        toolStage.show();
	}
	
	public Stage getStage() {
		return this.toolStage;
	}
	
	protected abstract void start(Stage stage);
}
