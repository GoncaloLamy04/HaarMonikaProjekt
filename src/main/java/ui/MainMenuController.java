package ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import service.AppointmentService;
import service.EmployeeService;
import service.TreatmentService;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class MainMenuController {

    private final EmployeeService employeeService;
    private final AppointmentService appointmentService;
    private final TreatmentService treatmentService;

    public MainMenuController(EmployeeService employeeService,
                              AppointmentService appointmentService,
                              TreatmentService treatmentService) {
        this.employeeService = employeeService;
        this.appointmentService = appointmentService;
        this.treatmentService = treatmentService;
    }

    @FXML private StackPane contentArea;
    @FXML private Button mainButton;
    @FXML private Button createButton;
    @FXML private Button employeeButton;
    @FXML private Button logoutButton;

    @FXML
    public void initialize() { onMenu(); }

    @FXML
    private void onMenu() {
        contentArea.getChildren().clear();
        Label welcome = new Label("Velkommen til Hårmoni'ka booking system");
        welcome.setStyle("-fx-font-size: 21px;");
        contentArea.getChildren().add(welcome);
    }

    @FXML
    private void onCreate() { loadView("create-booking-view.fxml"); }

    @FXML
    private void onEmployee() { loadView("employee-view.fxml"); }

    @FXML
    private void onLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(
                    getClass().getResource("/com/example/haarmonikaprojekt/login-view.fxml")));
            loader.setControllerFactory(type -> {
                if (type == LoginController.class) return new LoginController(employeeService, appointmentService, treatmentService);
                try { return type.getDeclaredConstructor().newInstance(); }
                catch (Exception e) { throw new RuntimeException(e); }
            });
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(loader.load(), 900, 600));
            stage.show();
        } catch (IOException e) {
            System.err.println("Kunne ikke loade login: " + e.getMessage());
        }
    }

    private void loadView(String fxml) {
        String path = "/com/example/haarmonikaprojekt/" + fxml;
        URL resource = getClass().getResource(path);
        if (resource == null) {
            System.err.println("[FXML] Not found: " + path);
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(resource);
            loader.setControllerFactory(type -> {
                if (type == CreateBookingController.class) return new CreateBookingController(appointmentService, treatmentService);
                if (type == EmployeeController.class) return new EmployeeController(employeeService);
                try { return type.getDeclaredConstructor().newInstance(); }
                catch (Exception e) { throw new RuntimeException(e); }
            });
            contentArea.getChildren().setAll((Node) loader.load());
        } catch (IOException e) {
            System.err.println("[FXML] Could not load: " + path);
            e.printStackTrace();
        }
    }
}