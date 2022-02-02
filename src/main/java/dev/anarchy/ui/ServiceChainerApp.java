package dev.anarchy.ui;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import com.fasterxml.jackson.databind.ObjectMapper;

import dev.anarchy.ApplicationData;
import dev.anarchy.common.DCollection;
import dev.anarchy.common.DFolder;
import dev.anarchy.common.DFolderElement;
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

	public void save() {
		// Save current open chain
		this.saveCurrent();
		
		// Write to file
		this.getData().save();
	}

	public void importCollection(DFolder parentFolder) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Resource File");
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("JSON Files", "*.json"));
		File selectedFile = fileChooser.showOpenDialog(stage);
		
		if (selectedFile != null) {
			try {
				Path path = selectedFile.toPath();
				byte[] data = Files.readAllBytes(path);
				String json = new String(data, StandardCharsets.UTF_8);
				
				ObjectMapper objectMapper = new ObjectMapper();
				
				DCollection newCollection = objectMapper.readValue(json, DCollection.class);
				
				if ( parentFolder == null ) {
					ServiceChainerApp.get().getData().addCollection(newCollection);
				} else {
					DFolder newFolder = new DFolder();
					newFolder.setName(newCollection.getName());
					for(DFolderElement element : newCollection.getChildrenUnmodifyable()) {
						newFolder.addChild(element);
					}
					parentFolder.addChild(newFolder);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
