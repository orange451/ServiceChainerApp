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
		File file = new File(userHome + ".ServiceChainerUI.lock");
		try {
		    FileChannel fc = FileChannel.open(file.toPath(),
		            StandardOpenOption.CREATE,
		            StandardOpenOption.WRITE);
		    FileLock lock = fc.tryLock();
		    if (lock == null) {
		    	onApplicationAlreadyRunning();
		    } else {
				file.deleteOnExit();
		    	onApplicationNotYetRunning();
		    }
		    
	    	setHiddenAttrib(file);
		} catch (IOException e) {
		    throw new Error(e);
		}
	}
	
	/**
	 * Called when the application is running and no other instances of this application are running.
	 */
	private static void onApplicationNotYetRunning() {
		try {
			// Start simple rest server
	        JRest app = JRest.create()
	        		.setPort(PORT)
	        		.setKeepApplicationAlive(false)
	        		.start();
	        
	        // Add wakeup api
	        app.get("/" + API_WAKEUP, (request) -> {
	        	ServiceChainerApp.get().wakeup();
	            return new ResponseEntity<>(HttpStatus.OK);
	        });
		} catch(Exception e) {
			onApplicationAlreadyRunning();
		}
	}
	
	/**
	 * Called when another instance of this application is already running.
	 */
	private static void onApplicationAlreadyRunning() {
		try {
			// Call wakeup API for other application
			RequestEntity<String> request = new RequestEntity<>(HttpMethod.GET);
			request.exchange("http://localhost:" + PORT + "/" + API_WAKEUP, String.class);
			
			// Quit
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

	private static void setHiddenAttrib(File file) {
		// for removing hide attribute
		//Process p = Runtime.getRuntime().exec("attrib -H " + file.getPath());
		
		try {
			// execute attrib command to set hide attribute
			Process p = Runtime.getRuntime().exec("attrib +H " + file.getPath());
			p.waitFor();
		} catch (IOException | InterruptedException e) {
			//
		}
	}
}
