package ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.w3c.dom.events.Event;
import service.EmployeeService;

import java.io.IOException;
import java.util.Objects;

public class LoginController {

    // Label / Text
    @FXML
    private TextField userTxt;
    @FXML
    private TextField passTxt;
    @FXML
    private Label labelException;

    // Button
    @FXML
    private Button loginButton;


    @FXML
    public void onLogin (ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(
                "/com/example/haarmonikaprojekt/main-menu-view.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

    }


}
