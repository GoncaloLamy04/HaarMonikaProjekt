package ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import service.EmployeeService;

// UI-lag: Håndterer medarbejder-siden, viser og opretter medarbejdere.
public class EmployeeController {

    private EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // Text / TextFields
    @FXML
    private TextField nameTxt;
    @FXML
    private TextField emailTxt;
    @FXML
    private TextField userTxt;
    @FXML
    private TextField passwordTxt;
    @FXML
    private TextField roleTxt;

    // Button
    @FXML
    private Button createButton;
    @FXML
    private Button changeButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button employeeButton;

    // Collumn
    @FXML
    private TableColumn colId;
    @FXML
    private TableColumn colName;
    @FXML
    private TableColumn colEmail;
    @FXML
    private TableColumn colTreatment;

    @FXML
    private void onCreate() {
    }
}
