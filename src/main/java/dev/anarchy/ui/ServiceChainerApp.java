package dev.anarchy.ui;

import dev.anarchy.ApplicationData;
import dev.anarchy.common.DServiceChain;
import dev.anarchy.ui.control.Workspace;
import dev.anarchy.ui.util.LaunchHelper;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.Window;

public class ServiceChainerApp extends Application {
	
	private static ServiceChainerApp app;
	
	private ApplicationData data;
	
	private Workspace workspace;
	
	private Stage stage;
	
	@Override
	public void start(Stage stage) {
		LaunchHelper.checkCanLaunch();
		
		data = ApplicationData.load();
		app = this;
		this.stage = stage;
		
		workspace = new Workspace();
		
		// Build main UI
		ServiceChainerUIBuilder.build(stage);
		
		// Show
		stage.centerOnScreen();
		stage.show();
		
		// No closing
		stage.setOnCloseRequest((event)->{
			stage.setIconified(true);
			event.consume();
		});
	}
	
	public static ServiceChainerApp get() {
		return app;
	}
	
	public ApplicationData getData() {
		return data;
	}
	
	public void saveCurrent() {
		workspace.save(workspace.getOpen());
	}
	
	public void modify() {
		DServiceChain chain = workspace.getOpen();
		if ( chain == null )
			return;
		
		chain.getOnChangedEvent().fire();
	}

	public void edit(DServiceChain internal) {
		workspace.open(internal);
	}

	public Workspace getWorkspace() {
		return this.workspace;
	}

	public static void main(String[] args) {
		launch(args);
	}

	public Window getStage() {
		return stage;
	}
}
