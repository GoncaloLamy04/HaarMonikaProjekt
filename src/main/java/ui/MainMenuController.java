package ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;

public class MainMenuController {

    @FXML
    private StackPane contentArea;
    @FXML
    private Button mainButton;
    @FXML
    private Button createButton;
    @FXML
    private Button changeButton;


    @FXML
    private void onMenu(){
    }

    @FXML
    private void onCreate(){
        loadView("create-booking-view.fxml");
    }

    @FXML
    private void onChange(){

    }

    private void loadView(String fxml) {
        String path = "/com/example/haarmonikaprojekt/" + fxml;

        URL resource = getClass().getResource(path);
        if (resource == null) {
            System.err.println("[FXML] Not found: " + path);
            return;
        }

        try {
            Node view = FXMLLoader.load(resource);
            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            System.err.println("[FXML] Could not load: " + path);
            System.err.println("Reason: " + e.getMessage());
        }
    }
}
