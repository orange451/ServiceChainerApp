package dev.anarchy.ui.control;

import java.util.HashMap;
import java.util.Map;

import dev.anarchy.DServiceChain;
import dev.anarchy.ui.AnarchyApp;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

public class Workspace extends BorderPane {
	private TabPane tabs;

	private Map<DServiceChain, Tab> openTabs = new HashMap<>();

	public Workspace() {
		Tab createNewTab = newTab();

		tabs = new TabPane();
		tabs.getTabs().add(createNewTab);
		tabs.setTabClosingPolicy(TabClosingPolicy.ALL_TABS);
		this.setCenter(tabs);

		// Create new service chain
		tabs.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
			@Override
			public void changed(ObservableValue<? extends Tab> observable, Tab oldTab, Tab newTab) {
				if (newTab.equals(createNewTab) && tabs.getTabs().size() > 1) {
					DServiceChain chain = AnarchyApp.get().getData().newServiceChain(AnarchyApp.get().getData().UNORGANIZED);
					open(chain);
				}
			}
		});
	}

	private Tab newTab() {
		ButtonTab newTab = new ButtonTab(new Label("\u2795"));
		newTab.setClosable(false);
		
		// Create new service chain
		newTab.setOnMouseClicked((event) ->{
			DServiceChain chain = AnarchyApp.get().getData().newServiceChain(AnarchyApp.get().getData().UNORGANIZED);
			open(chain);
		});

		// Create new service chain
		Button createNew = new Button("Create New Service Chain");
		createNew.setOnAction((event) -> {
			DServiceChain chain = AnarchyApp.get().getData().newServiceChain(AnarchyApp.get().getData().UNORGANIZED);
			open(chain);
		});
		
		StackPane p = new StackPane();
		newTab.setContent(p);
		
		p.setAlignment(Pos.CENTER);
		p.getChildren().add(createNew);

		return newTab;
	}

	public void open(DServiceChain internal) {
		Tab tab = openTabs.get(internal);
		if (tab == null) {
			tab = new Tab(internal.getName());
			tabs.getTabs().add(tabs.getTabs().size() - 1, tab);
			openTabs.put(internal, tab);

			tab.setOnClosed((event) -> {
				openTabs.remove(internal);
				AnarchyApp.get().getData().save();
			});

			tab.setContent(new ServiceChainEditor(internal));

			Tab finalTab = tab;
			internal.getOnNameChangeEvent().connect((args) -> {
				finalTab.setText(args[0].toString());
			});
		}

		tabs.getSelectionModel().select(tab);
	}
}
