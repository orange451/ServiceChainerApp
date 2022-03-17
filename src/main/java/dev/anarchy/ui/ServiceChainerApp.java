package dev.anarchy.ui;

import java.io.File;
import dev.anarchy.common.DApp;
import dev.anarchy.common.DCollection;
import dev.anarchy.common.DFolder;
import dev.anarchy.common.DServiceChain;
import dev.anarchy.ui.control.Workspace;
import dev.anarchy.ui.util.LaunchHelper;
import javafx.application.Application;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.FileChooser.ExtensionFilter;

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
		
		// Delete event. TODO clean this up.
		DApp.get().getOnDeleteEvent().connect((args) -> {
			if ( args[0] instanceof DFolder ) { 
				DFolder parentNode = ((DFolder)args[0]).getParent();
				if ( parentNode == null )
					parentNode = ServiceChainerApp.get().getData().UNORGANIZED;
				parentNode.removeChild(((DFolder)args[0]));
			}
			
			if ( args[0] instanceof DCollection ) {
				data.removeCollection(((DCollection)args[0]));
			}
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

	public Window getStage() {
		return stage;
	}

	public void save() {
		// Save current open chain
		this.saveCurrent();
		
		// Write to file
		this.getData().save();
	}

	public void importCollection(DFolder parentFolder) {
		File selectedFile = importFilePicker();
		data.importCollection(selectedFile, parentFolder);
	}
	
	public File importFilePicker() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Resource File");
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("JSON Files", "*.json"));
		File file = fileChooser.showOpenDialog(stage);
		
		return file;
	}

	public File exportFilePicker() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Json files (*.json)", "*.json");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showSaveDialog(ServiceChainerApp.get().getStage());
        
        return file;
	}

	public static void main(String[] args) {
		launch(args);
	}
}
