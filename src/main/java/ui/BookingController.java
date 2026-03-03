package ui;

import domain.Appointment;
import domain.Treatment;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import service.AppointmentService;
import service.TreatmentService;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BookingController {

    private final AppointmentService appointmentService;
    private final TreatmentService treatmentService;
    private Appointment selectedForEdit = null;

    public BookingController(AppointmentService appointmentService, TreatmentService treatmentService) {
        this.appointmentService = appointmentService;
        this.treatmentService = treatmentService;
    }

    @FXML private DatePicker timeDate;
    @FXML private Label labelException;
    @FXML private TextField emailTxt;
    @FXML private TextField nameTxt;
    @FXML private TextField durationTxt;
    @FXML private ComboBox<String> timeCombo;
    @FXML private ComboBox<Treatment> treatmentCombo;

    @FXML private TableView<Appointment> appointmentTable;
    @FXML private TableColumn<Appointment, Integer> colId;
    @FXML private TableColumn<Appointment, String> colName;
    @FXML private TableColumn<Appointment, String> colEmail;
    @FXML private TableColumn<Appointment, String> colTreatment;
    @FXML private TableColumn<Appointment, String> colTime;

    @FXML private Button createButton;
    @FXML private Button deleteButton;
    @FXML private Button changeButton;
    @FXML private TextField searchField;

    private final ObservableList<Appointment> appointments = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        treatmentCombo.setItems(FXCollections.observableArrayList(treatmentService.findAll()));

        ObservableList<String> tider = FXCollections.observableArrayList();
        for (int t = 8; t <= 17; t++) {
            tider.add(String.format("%02d:00", t));
            tider.add(String.format("%02d:30", t));
        }
        timeCombo.setItems(tider);

        colId.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colTreatment.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getTreatments().isEmpty()
                        ? "-"
                        : data.getValue().getTreatments().getFirst().toString()));
        colTime.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getStartTime().format(
                        DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))));

        appointmentTable.setItems(appointments);
        loadAppointments();

        createButton.disableProperty().bind(
                nameTxt.textProperty().isEmpty()
                        .or(emailTxt.textProperty().isEmpty())
                        .or(timeCombo.valueProperty().isNull())
                        .or(timeDate.valueProperty().isNull())
                        .or(treatmentCombo.valueProperty().isNull())
        );

        changeButton.disableProperty().bind(
                appointmentTable.getSelectionModel().selectedItemProperty().isNull()
        );
        deleteButton.disableProperty().bind(
                appointmentTable.getSelectionModel().selectedItemProperty().isNull()
        );

        searchField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) handleSearch();
        });
    }

    private void loadAppointments() {
        List<Appointment> all = appointmentService.findAll()
                .stream()
                .filter(a -> !a.isCancelled())
                .toList();
        appointments.setAll(all);
    }

    @FXML
    private void onCreateBooking() {
        if (!emailTxt.getText().contains("@") || !emailTxt.getText().contains(".")) {
            labelException.setText("Email skal indeholde @ og . fx test@test.dk");
            return;
        }

        if (selectedForEdit == null) {
            boolean emailExists = appointmentService.findAll().stream()
                    .filter(a -> !a.isCancelled())
                    .anyMatch(a -> a.getEmail().equalsIgnoreCase(emailTxt.getText()));
            if (emailExists) {
                labelException.setText("En aktiv booking med denne email findes allerede");
                return;
            }
        }

        try {
            int duration = treatmentCombo.getValue().getDurationMinutes();
            LocalTime time = LocalTime.parse(timeCombo.getValue(), DateTimeFormatter.ofPattern("HH:mm"));
            LocalDateTime startTime = LocalDateTime.of(timeDate.getValue(), time);
            Treatment selectedTreatment = treatmentCombo.getValue();

            if (selectedForEdit != null) {
                Appointment updated = new Appointment(
                        selectedForEdit.getAppointmentId(), 1, 1,
                        emailTxt.getText(),
                        nameTxt.getText(),
                        startTime,
                        duration,
                        List.of(selectedTreatment)
                );
                appointmentService.update(updated);
                selectedForEdit = null;
                createButton.setText("Opret booking");
            } else {
                Appointment a = new Appointment(
                        0, 1, 1,
                        emailTxt.getText(),
                        nameTxt.getText(),
                        startTime,
                        duration,
                        List.of(selectedTreatment)
                );
                appointmentService.create(a);
            }
            loadAppointments();
            clearFields();
            labelException.setText("");
        } catch (Exception e) {
            labelException.setText("Fejl: " + e.getMessage());
        }
    }

    @FXML
    private void onDeleteBooking() {
        Appointment selected = appointmentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            labelException.setText("Vælg en booking først");
            return;
        }
        try {
            appointmentService.cancel(selected.getAppointmentId());
            loadAppointments();
            labelException.setText("");
        } catch (Exception e) {
            labelException.setText("Fejl: " + e.getMessage());
        }
    }

    @FXML
    private void onChangeBooking() {
        Appointment selected = appointmentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            labelException.setText("Vælg en booking først");
            return;
        }
        selectedForEdit = selected;
        nameTxt.setText(selected.getName());
        emailTxt.setText(selected.getEmail());
        timeCombo.setValue(selected.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        timeDate.setValue(selected.getStartTime().toLocalDate());
        if (!selected.getTreatments().isEmpty()) {
            treatmentCombo.setValue(selected.getTreatments().getFirst());
        }
        createButton.setText("Gem ændringer");
        labelException.setText("");
    }

    private void clearFields() {
        nameTxt.clear();
        emailTxt.clear();
        timeCombo.setValue(null);
        timeDate.setValue(null);
        treatmentCombo.setValue(null);
        selectedForEdit = null;
        createButton.setText("Opret booking");
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText();
        List<Appointment> results = appointmentService.searchByCustomerName(query)
                .stream()
                .filter(a -> !a.isCancelled())
                .toList();
        appointments.setAll(results);
        appointmentTable.setItems(appointments);
    }

    @FXML
    private void clearSearch() {
        searchField.clear();
        loadAppointments();
    }
}