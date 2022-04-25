package dev.anarchy.ui.util;

import java.util.List;

import javafx.application.Platform;

public class StyleClassToggleUtil {
	public static void toggleStyleClass(List<String> classes, String styleClass, int repeat, long interval) {
		new Thread(()-> {
			for (int i = 0; i < repeat; i++) {
				try {
					// Toggle On
					Platform.runLater(()->{
						synchronized(classes) {
							classes.add(styleClass);
						}
					});
					Thread.sleep(interval);

					// Toggle Off
					Platform.runLater(()->{
						synchronized(classes) {
							classes.remove(styleClass);
						}
					});
					Thread.sleep(interval);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
}
