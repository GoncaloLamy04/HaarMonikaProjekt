package ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.w3c.dom.Text;

public class CreateBookingController {

    // Label and Text/Textfields
    @FXML
    private DatePicker timeDate;
    @FXML
    private Label labelException;
    @FXML
    private TextField emailTxt;
    @FXML
    private TextField nameTxt;
    @FXML
    private TextField treatmentTxt;
    @FXML
    private TextField durationTxt;


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
    private TableColumn colTime;
    @FXML
    private TableColumn colDuration;

    // Button
    @FXML
    private Button createButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button changeButton;




}
