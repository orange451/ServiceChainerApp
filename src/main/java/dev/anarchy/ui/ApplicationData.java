package dev.anarchy.ui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.anarchy.common.DCollection;
import dev.anarchy.common.DFolder;
import dev.anarchy.common.DFolderElement;
import dev.anarchy.common.DServiceChain;
import dev.anarchy.event.Event;
import dev.anarchy.translate.util.FileUtils;
import dev.anarchy.translate.util.ServiceChainHelper;
import dev.anarchy.ui.util.StringHelper;

public class ApplicationData {
	@JsonIgnore
	public DCollection UNORGANIZED = new DCollection();
	
	@JsonProperty("Collections")
	private List<DCollection> collections = new ArrayList<>();
	
	@JsonIgnore
	private Map<DCollection, File> collectionFileMap = new HashMap<>();

	@JsonIgnore
	private Event onCollectionAddedEvent = new Event();
	
	@JsonIgnore
	private Event onCollectionRemovedEvent = new Event();

	@JsonIgnore
	private static final String APPLICATION_NAME = "ServiceChainer";

	@JsonIgnore
	private static final String APPLICATION_FILENAME = "AppData.json";
	
	public ApplicationData() {
		UNORGANIZED.setDeletable(false);
		UNORGANIZED.setArchivable(false);
		UNORGANIZED.setName("Unorganized");
	}
	
	private void onLoad() {
		if ( UNORGANIZED != null && !this.collections.contains(UNORGANIZED) )
			this.addCollection(UNORGANIZED);
	}
	
	public DServiceChain newServiceChain(DFolder parent) {
		DServiceChain chain = new DServiceChain();
		String baseName = chain.getName();
		
		int index = 0;
		String checkName = baseName;
		while(parent.getChild(checkName) != null) {
			index += 1;
			checkName = baseName + " (" + index + ")"; 
		}
		
		chain.setName(checkName);
		parent.addChild(chain);
		
		save();
		return chain;
	}

	public DFolder newFolder(DFolder internal) {
		DFolder folder = new DFolder();
		String baseName = folder.getName();
		
		int index = 0;
		String checkName = baseName;
		while(internal.getChild(checkName) != null) {
			index += 1;
			checkName = baseName + " (" + index + ")"; 
		}
		
		folder.setName(checkName);
		internal.addChild(folder);
		
		save();
		
		return folder;
	}

	@JsonIgnore
	public List<DCollection> getCollectionsUnmodifyable() {
		DCollection[] arr = new DCollection[this.collections.size()];
		for (int i = 0; i < collections.size(); i++) {
			arr[i] = collections.get(i);
		}
		return Arrays.asList(arr);
	}
	
	public void addCollection(DCollection collection) {
		if ( collections.add(collection) ) {
			onCollectionAddedEvent.fire(collection);
		}
		
		save();
	}
	
	public void removeCollection(DCollection collection) {
		// Remove collection internally
		if ( collections.remove(collection) ) {
			onCollectionRemovedEvent.fire(collection);
		}
		
		// Remove Collection file
		try {
			File file = collectionFileMap.get(collection);
			if ( file != null && file.exists() ) {
				file.delete();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		// Save json
		save();
	}
	
	public Event getOnCollectionAddedEvent() {
		return this.onCollectionAddedEvent;
	}
	
	public Event getOnCollectionRemovedEvent() {
		return this.onCollectionRemovedEvent;
	}
	
	public static String getAppDataPath() {
		return System.getProperty("user.home") + File.separator + APPLICATION_NAME + File.separator;
	}
	
	public static String getAppDataFilePath() {
		return getAppDataPath() + APPLICATION_FILENAME;
	}
	
	public static ApplicationData load() {
		ApplicationData appData = new ApplicationData();
		
		// Get all potential collections
		File[] directories = new File(getAppDataPath()).listFiles(new FilenameFilter() {
		  @Override
		  public boolean accept(File current, String name) {
		    return new File(current, name).isDirectory();
		  }
		});
		
		// Check all collections
		List<DCollection> newCollections = new ArrayList<>();
		for (File file : directories) {
			String appDataPath = file.getAbsolutePath() + File.separator + APPLICATION_FILENAME;
			System.out.println("Checking: " + appDataPath);
			File appDataFile = new File(appDataPath);
			if ( appDataFile.exists() ) {
				try {
					Path path = Paths.get(appDataPath);
					byte[] data = Files.readAllBytes(path);
					String json = new String(data, StandardCharsets.UTF_8);
					System.out.println("READ JSON: " + json);
					ObjectMapper objectMapper = new ObjectMapper();
					
					DCollection collection = objectMapper.readValue(json, DCollection.class);
					
					if ( collection.getName().equals("Unorganized") && !collection.isDeletable() ) {
						appData.UNORGANIZED = collection;
						newCollections.add(0, collection);
					} else {
						newCollections.add(collection);
					}
					
					appData.collectionFileMap.put(collection, file);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		// Add
		for (DCollection collection : newCollections) {
			appData.collections.add(collection);
		}
		
		appData.onLoad();
		return appData;
	}
	
	@Deprecated
	public static ApplicationData loadLegacy() {
		ApplicationData appData;
		String json = null;
		try {
			System.out.println("LOAD1: " + ApplicationData.getAppDataFilePath());
			Path path = Paths.get(ApplicationData.getAppDataFilePath());
			System.out.println("LOAD2: " + path);
			byte[] data = Files.readAllBytes(path);
			json = new String(data, StandardCharsets.UTF_8);
			
			System.out.println("READ JSON: " + json);
			
			ObjectMapper objectMapper = new ObjectMapper();
			appData = objectMapper.readValue(json, ApplicationData.class);
		} catch (Exception e) {
			e.printStackTrace();
			appData = new ApplicationData();
		}
		
		for (DCollection collection : appData.getCollectionsUnmodifyable()) {
			if ( collection.getName().equals("Unorganized") && !collection.isDeletable() ) {
				appData.UNORGANIZED = collection;
				break;
			}
		}
		
		appData.onLoad();
		return appData;
	}
	
	public void save() {
		File appData = new File(getAppDataPath());
		if ( !appData.exists() )
			appData.mkdirs();
		
		for (DCollection collection : collections) {
			String folderPath = getAppDataPath() + collection.getName();
			File folder = new File(folderPath);
			if ( !folder.exists() )
				folder.mkdirs();
			
			try {
				String json = serializeJSON(collection);
				
			    BufferedWriter writer = new BufferedWriter(new FileWriter(folderPath + File.separator + APPLICATION_FILENAME));
			    writer.write(json);
			    writer.close();
			    
			    collectionFileMap.put(collection, folder);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@Deprecated
	public void saveLegacy() {
		String json = this.serializeJSON(this);
		
		try {
			String path = getAppDataFilePath();
			File file = new File(path);
			if ( !file.getParentFile().exists() )
				file.getParentFile().mkdirs();
			
		    BufferedWriter writer = new BufferedWriter(new FileWriter(path));
		    writer.write(json);
		    writer.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private String serializeJSON(Object object) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		return "{}";
	}

	
	public DFolder getParent(DFolderElement element) {
		for (DCollection collection : getCollectionsUnmodifyable()) {
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
			this.addCollection((DCollection) newFolder);
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

	protected void importCollection(File selectedFile, DFolder parentFolder) {
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
}
