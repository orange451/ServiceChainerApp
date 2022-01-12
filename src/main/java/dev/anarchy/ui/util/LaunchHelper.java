package dev.anarchy.ui.util;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.StandardOpenOption;
import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

public class LaunchHelper {

	public static void checkCanLaunch() {
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		System.setProperty("com.apple.mrj.application.apple.menu.about.name", "ImageRotator");
		
		String userHome = System.getProperty("user.home");
		File file = new File(userHome, "ServiceChainingUI.lock");
		try {
			file.deleteOnExit();
		    FileChannel fc = FileChannel.open(file.toPath(),
		            StandardOpenOption.CREATE,
		            StandardOpenOption.WRITE);
		    FileLock lock = fc.tryLock();
		    if (lock == null) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error");
				alert.setHeaderText("Could Not Launch Application");
				alert.setContentText("Is the application already running?");

				Optional<ButtonType> result = alert.showAndWait();
				if (result.get() == ButtonType.OK){
					System.exit(0);
				}
		    }
		} catch (IOException e) {
		    throw new Error(e);
		}
	}

}
