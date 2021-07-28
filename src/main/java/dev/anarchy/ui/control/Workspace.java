package dev.anarchy.ui.control;

import java.util.HashMap;
import java.util.Map;

import dev.anarchy.DServiceChain;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.layout.BorderPane;

public class Workspace extends BorderPane {
	private TabPane tabs;
	
	private Map<DServiceChain, Tab> openTabs = new HashMap<>();
	
	public Workspace() {
		tabs = new TabPane();
		tabs.getTabs().add(newTab());
		tabs.setTabClosingPolicy(TabClosingPolicy.ALL_TABS);
		this.setCenter(tabs);
	}

	private Tab newTab() {
		ButtonTab newTab = new ButtonTab(new Label("\u2795"));
		newTab.setClosable(false);
		
		newTab.setOnMouseClicked((event) ->{
			Tab tab = new Tab("Test 123");
			tabs.getTabs().add(tabs.getTabs().size()-1, tab);
			tabs.getSelectionModel().select(tab);
		});
		
		return newTab;
	}

	public void open(DServiceChain internal) {
		Tab tab = openTabs.get(internal);
		if ( tab == null ) {
			tab = new Tab(internal.getName());
			tabs.getTabs().add(tabs.getTabs().size()-1, tab);
			openTabs.put(internal, tab);
			
			tab.setOnClosed((event)-> {
				openTabs.remove(internal);
			});
			
			tab.setContent(new ServiceChainEditor(internal));
		}
		
		tabs.getSelectionModel().select(tab);
	}
}
