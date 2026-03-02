package ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import service.EmployeeService;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class MainMenuController {

    private EmployeeService employeeService;

    public MainMenuController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @FXML private StackPane contentArea;
    @FXML private Button mainButton;
    @FXML private Button createButton;
    @FXML private Button logoutButton;

    @FXML
    public void initialize() {
        onMenu();
    }

    @FXML
    private void onMenu() {
        contentArea.getChildren().clear();
        Label welcome = new Label("Velkommen til Hårmoni'ka booking system");
        welcome.setStyle("-fx-font-size: 21px;");
        contentArea.getChildren().add(welcome);
    }

    @FXML
    private void onCreate() {
        loadView();
    }

    @FXML
    private void onLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(
                    getClass().getResource("/com/example/haarmonikaprojekt/login-view.fxml")));
            loader.setControllerFactory(type -> {
                if (type == LoginController.class) return new LoginController(employeeService);
                try { return type.getDeclaredConstructor().newInstance(); }
                catch (Exception e) { throw new RuntimeException(e); }
            });
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.show();
        } catch (IOException e) {
            System.err.println("Kunne ikke loade login: " + e.getMessage());
        }
    }

    private void loadView() {
        String path = "/com/example/haarmonikaprojekt/" + "create-booking-view.fxml";
        URL resource = getClass().getResource(path);
        if (resource == null) {
            System.err.println("[FXML] Not found: " + path);
            return;
        }
        try {
            javafx.scene.Node view = FXMLLoader.load(resource);
            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            System.err.println("[FXML] Could not load: " + path);
            System.err.println("Reason: " + e.getMessage());
        }
    }
}