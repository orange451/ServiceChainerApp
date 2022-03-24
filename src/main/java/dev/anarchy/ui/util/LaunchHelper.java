package dev.anarchy.ui.util;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.StandardOpenOption;
import java.util.Optional;

import dev.anarchy.ui.ApplicationData;
import dev.anarchy.ui.ServiceChainerApp;
import io.jrest.HttpMethod;
import io.jrest.HttpStatus;
import io.jrest.JRest;
import io.jrest.RequestEntity;
import io.jrest.ResponseEntity;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

public class LaunchHelper {
	
	private static final int PORT = 39571;
	
	private static final String API_WAKEUP = "wakeup";

	public static void checkCanLaunch() {
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Test Name 123");
		
		String userHome = ApplicationData.getAppDataPath();
		File file = new File(userHome + "ServiceChainingUI.lock");
		try {
			file.deleteOnExit();
		    FileChannel fc = FileChannel.open(file.toPath(),
		            StandardOpenOption.CREATE,
		            StandardOpenOption.WRITE);
		    FileLock lock = fc.tryLock();
		    if (lock == null) {
		    	onApplicationAlreadyRunning();
		    } else {
		    	onApplicationNotYetRunning();
		    }
		} catch (IOException e) {
		    throw new Error(e);
		}
	}
	
	/**
	 * Called when the application is running and no other instances of this application are running.
	 */
	private static void onApplicationNotYetRunning() {
		try {
	        JRest app = JRest.create()
	        		.setPort(PORT)
	        		.setKeepApplicationAlive(false)
	        		.start();
	        
	        app.get("/" + API_WAKEUP, (request) -> {
	        	ServiceChainerApp.get().wakeup();
	            return new ResponseEntity<>(HttpStatus.OK);
	        });
		} catch(Exception e) {
			onApplicationAlreadyRunning();
			System.exit(0);
		}
	}
	
	/**
	 * Called when another instance of this application is already running.
	 */
	private static void onApplicationAlreadyRunning() {
		try {
			RequestEntity<String> request = new RequestEntity<>(HttpMethod.GET);
			request.exchange("http://localhost:" + PORT + "/" + API_WAKEUP, String.class);
			System.exit(0);
		} catch(Exception e) {
			e.printStackTrace();
			
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Could Not Launch Application");
			alert.setContentText("Is the application already running?");

			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK){
				System.exit(0);
			}
		}
	}

}
