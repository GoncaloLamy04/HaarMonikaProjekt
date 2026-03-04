package ui;

import domain.Employee;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import service.AppointmentService;
import service.CustomerService;
import service.EmployeeService;
import service.TreatmentService;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

// UI-lag: Håndterer hovedmenuen, navigerer til booking- og medarbejder-siden samt logout.
public class MainMenuController {

    private final EmployeeService employeeService;
    private final AppointmentService appointmentService;
    private final TreatmentService treatmentService;
    private final CustomerService customerService;
    private final Employee loggedInEmployee;

    public MainMenuController(EmployeeService employeeService,
                              AppointmentService appointmentService,
                              TreatmentService treatmentService,
                              CustomerService customerService,
                              Employee loggedInEmployee) { // RETTET: var EmployeeService
        this.employeeService = employeeService;
        this.appointmentService = appointmentService;
        this.treatmentService = treatmentService;
        this.customerService = customerService;
        this.loggedInEmployee = loggedInEmployee;
    }

    @FXML private StackPane contentArea;
    @FXML private Button logoutButton;

    @FXML
    public void initialize() { onMenu(); }

    @FXML
    private void onMenu() {
        contentArea.getChildren().clear();

        javafx.scene.layout.VBox vbox = new javafx.scene.layout.VBox(10);
        vbox.setAlignment(javafx.geometry.Pos.CENTER);

        Label welcome = new Label("Velkommen til Hårmoni'ka booking system");
        welcome.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label employeeLabel = new Label("Logget ind som: " + loggedInEmployee.getName());
        employeeLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #555555;");

        Label dateLabel = new Label("Dato: " + LocalDate.now()
                .format(DateTimeFormatter.ofPattern("dd. MMMM yyyy",
                        Locale.forLanguageTag("da"))));
        dateLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #777777;");

        vbox.getChildren().addAll(welcome, employeeLabel, dateLabel);
        contentArea.getChildren().add(vbox);
    }

    @FXML
    private void onCreate() { loadView("booking-view.fxml"); }

    @FXML
    private void onEmployee() { loadView("employee-view.fxml"); }

    @FXML
    private void onLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(
                    getClass().getResource("/com/example/haarmonikaprojekt/login-view.fxml")));
            loader.setControllerFactory(type -> {
                if (type == LoginController.class)
                    return new LoginController(employeeService,
                            appointmentService, treatmentService, customerService); // RETTET: customerService tilføjet
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
            FXMLLoader loader = getFxmlLoader(resource);
            contentArea.getChildren().setAll((Node) loader.load());
        } catch (IOException e) {
            System.err.println("[FXML] Could not load: " + path + " – " + e.getMessage());
        }
    }

    private FXMLLoader getFxmlLoader(URL resource) {
        FXMLLoader loader = new FXMLLoader(resource);
        loader.setControllerFactory(type -> {
            if (type == BookingController.class)
                return new BookingController(appointmentService, treatmentService,
                        customerService, employeeService, loggedInEmployee);
            if (type == EmployeeController.class)
                return new EmployeeController(employeeService);
            try { return type.getDeclaredConstructor().newInstance(); }
            catch (Exception e) { throw new RuntimeException(e); }
        });
        return loader;
    }
}