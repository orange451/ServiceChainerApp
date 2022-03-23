package dev.anarchy.ui.control;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import dev.anarchy.common.DCollection;
import dev.anarchy.common.DServiceChain;
import dev.anarchy.translate.util.ServiceChainHelper;
import dev.anarchy.ui.ApplicationData;
import dev.anarchy.ui.ServiceChainerApp;
import dev.anarchy.ui.ServiceChainerUIBuilder;
import dev.anarchy.ui.control.servicechain.ServiceChainEditor;
import dev.anarchy.ui.control.servicechain.ServiceChainRunner;
import dev.anarchy.ui.util.IconHelper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
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
	
	private Tab findTab(DServiceChain chain) {
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
			
			// Handle request close logic
			tab.setOnCloseRequest((event)->{
				if ( !modifiedStatus.get(internal).get() )
					return;
				
				ButtonType result = requestSave();
				
				if ( result.equals(ButtonType.YES) ) {
					save(internal);
				} else if ( result.equals(ButtonType.CANCEL) ) {
					event.consume();
				}
			});
			
			// Context Menu
			{
				ContextMenu contextMenu = new ContextMenu();
				
				// Close
				{
					MenuItem option = new MenuItem("Close");
					option.setOnAction((event) -> {
						close(tab);
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
				
				tab.setContextMenu(contextMenu);
			}
			
			// Mark service chain as modified
			editor.getServiceChain().getOnChangedEvent().connect((args)->{
				tab.setText("*" + internal.getName());
				modifiedStatus.get(internal).setValue(true);
			});
			
			// On Closed event
			tab.setOnClosed((event) -> {
				openTabs.remove(internal);
				modifiedStatus.get(internal).setValue(false);
				System.out.println("REEEEEEE");
			});

			tab.setContent(editor);

			Tab finalTab = tab;
			internal.getOnNameChangeEvent().connect((args) -> {
				finalTab.setText(args[0].toString());
			});
		}

		tabs.getSelectionModel().select(openTab);
	}

	private void close(Tab tab) {
		EventHandler<Event> handler = tab.getOnClosed();
		handler.handle(null);
		
		tabs.getTabs().remove(tab);
	}

	private ButtonType requestSave() {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you wish to save changes?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
		ServiceChainerUIBuilder.setTheme(alert.getDialogPane());
		alert.getDialogPane().getStylesheets();
		ButtonType result = alert.showAndWait().orElse(ButtonType.NO);
		
		return result;
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
		tab.setText(internal.getName());
		modifiedStatus.get(internal).setValue(false);
		
		// Write to file
		ServiceChainerApp.get().getData().save();
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
			ButtonType result = requestSave();
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
				close((Tab)tab);
			}
		}
		
		return true;
	}

	private void saveOpenTabs() {
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
