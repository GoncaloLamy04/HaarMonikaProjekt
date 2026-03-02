package ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import service.EmployeeService;

import java.io.IOException;
import java.util.Objects;

public class LoginController {

    private EmployeeService employeeService;

    public LoginController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @FXML private TextField userTxt;
    @FXML private PasswordField passTxt;
    @FXML private Label labelException;
    @FXML private Button loginButton;
    @FXML private TextField passTxtVisible;
    @FXML private CheckBox showPassword;

    @FXML
    public void onLogin(ActionEvent event) throws IOException {
        try {
            String password = showPassword.isSelected() ? passTxtVisible.getText() : passTxt.getText();
            employeeService.login(userTxt.getText(), password);

            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource(
                    "/com/example/haarmonikaprojekt/main-menu-view.fxml")));
            loader.setControllerFactory(type -> {
                if (type == MainMenuController.class) return new MainMenuController(employeeService);
                try { return type.getDeclaredConstructor().newInstance(); }
                catch (Exception e) { throw new RuntimeException(e); }
            });
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.show();

        } catch (IllegalArgumentException e) {
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