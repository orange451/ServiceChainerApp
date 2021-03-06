package dev.anarchy.ui.control.workspace;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import dev.anarchy.common.DCollection;
import dev.anarchy.common.DServiceChain;
import dev.anarchy.translate.util.ServiceChainHelper;
import dev.anarchy.ui.ApplicationData;
import dev.anarchy.ui.ServiceChainerApp;
import dev.anarchy.ui.control.workspace.servicechain.ServiceChainEditor;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

public class Workspace extends BorderPane {
	private TabPane tabs;

	private Map<DServiceChain, Tab> openTabs = new HashMap<>();
	private Map<DServiceChain, SimpleBooleanProperty> modifiedStatus = new HashMap<>();

	public Workspace() {
		Tab createNewTab = newTab();

		tabs = new TabPane();
		tabs.getTabs().add(createNewTab);
		tabs.setTabClosingPolicy(TabClosingPolicy.ALL_TABS);
		this.setCenter(tabs);

		// Create new service chain
		tabs.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Tab> observable, Tab oldTab, Tab newTab)->{
			if (newTab.equals(createNewTab) && tabs.getTabs().size() > 1) {
				DServiceChain chain = ServiceChainerApp.get().getData().newServiceChain(ServiceChainerApp.get().getData().UNORGANIZED);
				open(chain);
			}
		});
	}
	
	public DServiceChain getOpen() {
		Tab open = tabs.getSelectionModel().getSelectedItem();
		for (Entry<DServiceChain, Tab> entry : openTabs.entrySet()) {
			if ( entry.getValue() == open ) {
				return entry.getKey();
			}
		}
		
		return null;
	}
	
	public boolean isEditing(DServiceChain chain) {
		return findTab(chain) != null;
	}
	
	public Tab findTab(DServiceChain chain) {
		for (Entry<DServiceChain, Tab> entry : openTabs.entrySet()) {
			if ( entry.getKey() == chain ) {
				return entry.getValue();
			}
		}
		
		return null;
	}

	private Tab newTab() {
		ButtonTab newTab = new ButtonTab(new Label("+"));
		newTab.setClosable(false);
		
		// Create new service chain
		newTab.setOnMouseClicked((event) ->{
			createAndOpenUnorganizedServiceChain();
		});

		// Create new service chain
		Button createNew = new Button("Create New Service Chain");
		createNew.setOnAction((event) -> {
			createAndOpenUnorganizedServiceChain();
		});
		
		StackPane p = new StackPane();
		newTab.setContent(p);
		
		p.setAlignment(Pos.CENTER);
		p.getChildren().add(createNew);

		return newTab;
	}
	
	private void createAndOpenUnorganizedServiceChain() {
		ApplicationData applicationData = ServiceChainerApp.get().getData();
		DCollection parent = applicationData.UNORGANIZED;
		DServiceChain chain = applicationData.newServiceChain(parent);
		open(chain);
	}

	public void open(DServiceChain internal) {
		Tab openTab = openTabs.get(internal);
		if (openTab == null) {
			final Tab tab = new Tab(internal.getName());
			openTab = tab;
			
			// Add to SECOND TO LAST tab. Last tab is "+" button
			tabs.getTabs().add(tabs.getTabs().size() - 1, tab);
			
			// Update trackers
			openTabs.put(internal, tab);
			modifiedStatus.put(internal, new SimpleBooleanProperty(false));
			
			// Create new editor
			ServiceChainEditor editor = new ServiceChainEditor(internal);
			
			// Context Menu
			ContextMenu contextMenu = new ContextMenu();
			generateContextMenu(tab, contextMenu);
			tab.setContextMenu(contextMenu);
			
			// Mark service chain as modified
			editor.getServiceChain().getOnChangedEvent().connect((args)->{
				modifiedStatus.get(internal).setValue(true);
				updateTabText(tab, internal);
			});
			
			// Handle request close logic
			tab.setOnCloseRequest((event)->{
				if ( !modifiedStatus.get(internal).get() )
					return;
				
				ButtonType result = ServiceChainerApp.get().requestSave();
				
				if ( result.equals(ButtonType.YES) ) {
					save(internal);
				} else if ( result.equals(ButtonType.CANCEL) ) {
					event.consume();
				}
			});
			
			// On Closed event
			tab.setOnClosed((event) -> {
				openTabs.remove(internal);
				modifiedStatus.get(internal).setValue(false);
			});

			tab.setContent(editor);

			// Track name changes
			internal.getOnNameChangeEvent().connect((args) -> {
				updateTabText(tab, internal);
			});
		}

		tabs.getSelectionModel().select(openTab);
	}
	
	private void updateTabText(Tab tab, DServiceChain serviceChain) {
		String prefix = modifiedStatus.get(serviceChain).get() ? "*" : "";
		tab.setText(prefix + serviceChain.getName());
	}

	private void generateContextMenu(Tab tab, ContextMenu contextMenu) {
		// Close
		{
			MenuItem option = new MenuItem("Close");
			option.setOnAction((event) -> {
				close(tab);
			});
			contextMenu.getItems().add(option);
		}
		
		// Close Others
		{
			MenuItem option = new MenuItem("Close Others");
			option.setOnAction((event) -> {
				closeOthers(tab);
			});
			contextMenu.getItems().add(option);
		}
		
		// Close All
		{
			MenuItem option = new MenuItem("Close All");
			option.setOnAction((event) -> {
				closeAll();
			});
			contextMenu.getItems().add(option);
		}
	}

	private void forceClose(Tab tab) {
		EventHandler<Event> handler = tab.getOnClosed();
		handler.handle(null);
		
		tabs.getTabs().remove(tab);
	}
	
	private boolean requestClose(Tab tab) {
		Event event = new Event(Tab.TAB_CLOSE_REQUEST_EVENT);
		EventHandler<Event> handler = tab.getOnCloseRequest();
		handler.handle(event);
		
		return !event.isConsumed();
	}

	public boolean close(Tab tab) {
		if ( requestClose(tab) ) {
			forceClose(tab);
			return true;
		}
		
		return false;
	}

	public void save(DServiceChain internal) {
		System.out.println("Attempting to save");
		if ( internal == null )
			return;
		
		Tab tab = findTab(internal);
		if ( tab == null )
			return;
		
		Node node = tab.getContent();
		if ( node == null || !(node instanceof ServiceChainEditor) )
			return;
		
		// Get editor
		ServiceChainEditor editor = (ServiceChainEditor) tab.getContent();
		
		// Copy service chain back to app data
		internal.copyFrom(editor.getServiceChain());
		
		// Extra save transformation logic
		ServiceChainHelper.saveServiceChain(internal);
		
		// Mark as unmodified
		modifiedStatus.get(internal).setValue(false);
		updateTabText(tab, internal);
		
		// Write to file
		try {
			ServiceChainerApp.get().getData().save(internal);
		} catch (Exception e) {
			ServiceChainerApp.get().alert(AlertType.ERROR, "Something went wrong saving service chain.\n" + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public SimpleBooleanProperty getModifiedStatusProperty(DServiceChain serviceChain) {
		return modifiedStatus.get(serviceChain);
	}

	public boolean closeAll() {
		boolean needsToSave = false;
		for (SimpleBooleanProperty value : modifiedStatus.values()) {
			if ( value.get() ) {
				needsToSave = true;
			}
		}
		
		if ( needsToSave ) {
			ButtonType result = ServiceChainerApp.get().requestSave();
			if ( result == ButtonType.YES ) {
				saveOpenTabs();
			} else if ( result == ButtonType.CANCEL ) {
				return false;
			}
		}
		
		// Mark everything as saved
		clearModifiedStatus();
		
		// Close tabs
		Object[] myTabs = openTabs.values().toArray();
		synchronized(myTabs) {
			for (Object tab : myTabs) {
				forceClose((Tab)tab);
			}
		}
		
		return true;
	}
	
	public void closeOthers(Tab exclude) {
		Object[] myTabs = openTabs.values().toArray();
		synchronized(myTabs) {
			for (Object tab : myTabs) {
				if ( tab.equals(exclude) )
					continue;
				
				tabs.getSelectionModel().select((Tab) tab);
				close((Tab)tab);
			}
		}
	}

	public void saveOpenTabs() {
		for (Entry<DServiceChain, SimpleBooleanProperty> entrySet : modifiedStatus.entrySet()) {
			if ( entrySet.getValue().get() ) {
				save(entrySet.getKey());
			}
		}
	}

	private void clearModifiedStatus() {
		for (SimpleBooleanProperty value : modifiedStatus.values()) {
			value.set(false);
		}
	}
}
