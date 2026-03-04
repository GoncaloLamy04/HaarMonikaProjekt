package ui;

import domain.Employee;
import exceptions.ValidationException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import service.AppointmentService;
import service.CustomerService;
import service.EmployeeService;
import service.TreatmentService;

import java.io.IOException;
import java.util.Objects;

// UI-lag: Håndterer login-skærmen, validerer brugernavn og password og navigerer til hovedmenuen.
public class LoginController {

    private final EmployeeService employeeService;
    private final AppointmentService appointmentService;
    private final TreatmentService treatmentService;
    private final CustomerService customerService;

    public LoginController(EmployeeService employeeService,
                           AppointmentService appointmentService,
                           TreatmentService treatmentService,
                           CustomerService customerService) {
        this.employeeService = employeeService;
        this.appointmentService = appointmentService;
        this.treatmentService = treatmentService;
        this.customerService = customerService;
    }

    @FXML private TextField userTxt;
    @FXML private PasswordField passTxt;
    @FXML private Label labelException;
    @FXML private Button loginButton;
    @FXML private TextField passTxtVisible;
    @FXML private CheckBox showPassword;

    @FXML
    public void initialize() {
        userTxt.setOnKeyPressed(e -> {
            if (e.getCode() == javafx.scene.input.KeyCode.ENTER) loginButton.fire();
        });
        passTxt.setOnKeyPressed(e -> {
            if (e.getCode() == javafx.scene.input.KeyCode.ENTER) loginButton.fire();
        });
        passTxtVisible.setOnKeyPressed(e -> {
            if (e.getCode() == javafx.scene.input.KeyCode.ENTER) loginButton.fire();
        });
    }

    @FXML
    public void onLogin(ActionEvent event) throws IOException {
        try {
            String password = showPassword.isSelected() ? passTxtVisible.getText() : passTxt.getText();
            Employee loggedIn = employeeService.login(userTxt.getText(), password);

            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource(
                    "/com/example/haarmonikaprojekt/main-menu-view.fxml")));
            loader.setControllerFactory(type -> {
                if (type == MainMenuController.class)
                    return new MainMenuController(
                            employeeService, appointmentService, treatmentService, customerService, loggedIn);
                try { return type.getDeclaredConstructor().newInstance();
                } catch (Exception e) { throw new RuntimeException(e);}
            });
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(loader.load(), 900, 600));
            stage.show();

        } catch (ValidationException e) {
            labelException.setText("Forkert brugernavn eller adgangskode");
        }
    }

    @FXML
    private void togglePassword() {
        if (showPassword.isSelected()) {
            passTxtVisible.setText(passTxt.getText());
            passTxtVisible.setManaged(true);
            passTxtVisible.setVisible(true);
            passTxt.setManaged(false);
            passTxt.setVisible(false);
        } else {
            passTxt.setText(passTxtVisible.getText());
            passTxt.setManaged(true);
            passTxt.setVisible(true);
            passTxtVisible.setManaged(false);
            passTxtVisible.setVisible(false);
        }
    }
}