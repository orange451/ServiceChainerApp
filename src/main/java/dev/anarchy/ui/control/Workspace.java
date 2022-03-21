package dev.anarchy.ui.control;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import dev.anarchy.common.DCollection;
import dev.anarchy.common.DServiceChain;
import dev.anarchy.ui.ApplicationData;
import dev.anarchy.ui.ServiceChainerApp;
import dev.anarchy.ui.ServiceChainerUIBuilder;
import dev.anarchy.ui.control.servicechain.ServiceChainEditor;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

public class Workspace extends BorderPane {
	private TabPane tabs;

	private Map<DServiceChain, Tab> openTabs = new HashMap<>();
	
	private boolean MODIFIED = false;

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
			
			tabs.getTabs().add(tabs.getTabs().size() - 1, tab);
			openTabs.put(internal, tab);
			
			ServiceChainEditor editor = new ServiceChainEditor(internal);

			tab.setOnCloseRequest((event)->{
				if ( !MODIFIED )
					return;
				
			    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you wish to save changes?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
		        ServiceChainerUIBuilder.setTheme(alert.getDialogPane());
		        alert.getDialogPane().getStylesheets();
			    ButtonType result = alert.showAndWait().orElse(ButtonType.NO);
			    
			    if ( result.equals(ButtonType.YES) ) {
			    	save(internal);
			    } else if ( result.equals(ButtonType.CANCEL) ) {
			    	event.consume();
			    }
			});
			
			editor.getServiceChain().getOnChangedEvent().connect((args)->{
				tab.setText("*" + internal.getName());
				MODIFIED = true;
			});
			
			tab.setOnClosed((event) -> {
				openTabs.remove(internal);
				ServiceChainerApp.get().getData().save();
			});

			tab.setContent(editor);

			Tab finalTab = tab;
			internal.getOnNameChangeEvent().connect((args) -> {
				finalTab.setText(args[0].toString());
			});
		}

		tabs.getSelectionModel().select(openTab);
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
		
		ServiceChainEditor editor = (ServiceChainEditor) tab.getContent();
    	internal.copyFrom(editor.getServiceChain());
    	//ServiceChainHelper.saveServiceChain(internal);
		tab.setText(internal.getName());
		MODIFIED = false;
	}
}
