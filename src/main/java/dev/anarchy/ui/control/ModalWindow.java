package dev.anarchy.ui.control;

import dev.anarchy.ui.AnarchyApp;
import javafx.stage.Modality;
import javafx.stage.Stage;

public abstract class ModalWindow {
	
	private Stage toolStage;

	public ModalWindow() {
        toolStage = new Stage();
        toolStage.initOwner(AnarchyApp.get().getStage());
        toolStage.initModality(Modality.APPLICATION_MODAL);
        //toolStage.setAlwaysOnTop(true);
        
        start(toolStage);
	}
	
	public void show() {
        toolStage.centerOnScreen();
        toolStage.show();
	}
	
	protected abstract void start(Stage stage);
}
