package dev.anarchy.ui;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import com.fasterxml.jackson.databind.ObjectMapper;

import dev.anarchy.common.DApp;
import dev.anarchy.common.DCollection;
import dev.anarchy.common.DFolder;
import dev.anarchy.common.DFolderElement;
import dev.anarchy.common.DServiceChain;
import dev.anarchy.translate.util.FileUtils;
import dev.anarchy.translate.util.ServiceChainHelper;
import dev.anarchy.ui.control.Workspace;
import dev.anarchy.ui.util.LaunchHelper;
import dev.anarchy.ui.util.StringHelper;
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
				ServiceChainerApp.get().getData().removeCollection(((DCollection)args[0]));
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
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Resource File");
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("JSON Files", "*.json"));
		File selectedFile = fileChooser.showOpenDialog(stage);
		
		if (selectedFile != null) {
			String fileName = FileUtils.getFileNameFromPathWithoutExtension(selectedFile.getAbsolutePath());
			
			try {
				Path path = selectedFile.toPath();
				byte[] data = Files.readAllBytes(path);
				String json = new String(data, StandardCharsets.UTF_8);
				json = json.trim();
				
				// Make sure jackson understannds this is a COLLECTION
				if ( json.startsWith("{") ) {
					json = StringHelper.insert(json, 2, "\"_IsCollection\": true,");
				}
				
				// Create new collection
				ObjectMapper objectMapper = new ObjectMapper();
				DCollection newCollection = objectMapper.readValue(json, DCollection.class);
				newCollection.setName(fileName);
				
				// Fix missing Metadata
				for (DFolderElement child : newCollection.getChildrenUnmodifyable()) {
					ServiceChainHelper.fixServiceChain((DServiceChain)child);
				}
				
				// If no parent is specified, put it as a new collection
				if ( parentFolder == null ) {
					ServiceChainerApp.get().getData().addCollection(newCollection);
				} else {
					// If parent is specified, empty its children in to the parent
					int x = 0;
					for(DFolderElement element : newCollection.getChildrenUnmodifyable()) {
						if ( x > 0 ) {
							((DServiceChain)element).setName(fileName + " " + x);
						} else {
							((DServiceChain)element).setName(fileName);
						}
						
						x += 1;
						
						parentFolder.addChild(element);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
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
	
	public DFolder getParent(DFolderElement element) {
		for (DCollection collection : data.getCollectionsUnmodifyable()) {
			DFolder folder = getParentRecursive(element, collection);
			if ( folder != null )
				return folder;
		}
		
		return null;
	}
	
	private DFolder getParentRecursive(DFolderElement check, DFolder parent) {
		for (DFolderElement child : parent.getChildrenUnmodifyable()) {
			if ( child == check ) {
				return parent;
			}
			
			if ( child instanceof DFolder ) {
				DFolder match = getParentRecursive(check, (DFolder) child);
				if ( match != null )
					return match;
			}
		}
		
		return null;
	}

	public DFolder duplicate(DFolder internal) {
		DFolder newFolder = internal.clone();
		newFolder.setDeletable(true);
		newFolder.setArchivable(true);
		
		if ( newFolder instanceof DCollection ) {
			data.addCollection((DCollection) newFolder);
		} else {
			DFolder parent = getParent(internal);
			parent.addChild(newFolder);
		}
		
		return newFolder;
	}

	public DServiceChain duplicate(DServiceChain internal) {
		DServiceChain newObject = internal.clone();

		DFolder parent = getParent(internal);
		parent.addChild(newObject);
		
		return newObject;
	}
}
