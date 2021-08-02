package dev.anarchy.ui;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.fasterxml.jackson.databind.ObjectMapper;

import dev.anarchy.ApplicationData;
import dev.anarchy.common.DServiceChain;
import dev.anarchy.ui.control.Workspace;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.Window;

public class AnarchyApp extends Application {
	
	private static AnarchyApp app;
	
	private ApplicationData data;
	
	private Workspace workspace;
	
	private Stage stage;
	
	@Override
	public void start(Stage stage) {
		data = loadData();
		app = this;
		this.stage = stage;
		
		workspace = new Workspace();
		AnarchyAppUI.build(stage);
		
		stage.centerOnScreen();
		stage.show();
		
		app.getData().load();
	}
	
	private ApplicationData loadData() {
		String json = null;
		try {
			byte[] data = Files.readAllBytes(Paths.get("APPDATA.json"));
			json = new String(data, StandardCharsets.UTF_8);
			
			System.out.println("READ JSON: " + json);
			
			ObjectMapper objectMapper = new ObjectMapper();
			return objectMapper.readValue(json, ApplicationData.class);
		} catch (Exception e) {
			//e.printStackTrace();
			return new ApplicationData();
		}
	}
	
	public static AnarchyApp get() {
		return app;
	}
	
	public ApplicationData getData() {
		return data;
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
