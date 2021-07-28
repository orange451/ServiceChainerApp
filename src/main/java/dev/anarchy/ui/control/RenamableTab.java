package dev.anarchy.ui.control;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;

public class RenamableTab extends Tab {
    private final Label     label;
    private final TextField textField;

    public RenamableTab() {
        this("New Tab", null);
    }

    public RenamableTab(String text) {
        this(text, null);
    }

    public RenamableTab(String text, Node content) {
        super();
        label = new Label(text);
        textField = new TextField(text);
        setContent(content);
        textField.setStyle("-fx-background-color: transparent");
        setGraphic(label);
        label.setOnMouseClicked((mouseEvent) -> {
            if (mouseEvent.getClickCount() == 2) {
                rename();
            }
        });
        textField.setOnAction(event -> setGraphic(label));
        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                label.setText(textField.getText());
                setGraphic(label);
            }
        });
    }

    public TextField getTextField() {
        return textField;
    }

    public void rename() {
        //textField.setPrefWidth(label.getWidth());
        //textField.setText(label.getText());
        setGraphic(textField);
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        textField.selectAll();
                        textField.requestFocus();
                    }
                });
            }
        }.start();
    }
}