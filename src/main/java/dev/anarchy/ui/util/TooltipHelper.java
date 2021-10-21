package dev.anarchy.ui.util;

import java.lang.reflect.Field;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;

public class TooltipHelper {
	public static Node buildTooltip(String string) {
		Label label = new Label("", IconHelper.QUESTION.create());
		Tooltip tip = new Tooltip(string);
		label.setTooltip(tip);
		
		hackTooltipStartTiming(tip);
		return label;
	}
	
	private static void hackTooltipStartTiming(Tooltip tooltip) {
	    try {
	        Field fieldBehavior = tooltip.getClass().getDeclaredField("BEHAVIOR");
	        fieldBehavior.setAccessible(true);
	        Object objBehavior = fieldBehavior.get(tooltip);

	        Field fieldTimer = objBehavior.getClass().getDeclaredField("hideTimer");
	        fieldTimer.setAccessible(true);
	        Timeline objTimer = (Timeline) fieldTimer.get(objBehavior);

	        objTimer.getKeyFrames().clear();
	        objTimer.getKeyFrames().add(new KeyFrame(new Duration(30000)));
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
}
