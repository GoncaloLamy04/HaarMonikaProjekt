package ui;

import domain.*;
import exceptions.BookingConflictException;
import exceptions.DataAccessException;
import exceptions.ValidationException;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.ListCell;
import javafx.scene.input.KeyCode;
import javafx.util.StringConverter;
import service.AppointmentService;
import service.CustomerService;
import service.EmployeeService;
import service.TreatmentService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

// UI-lag: Håndterer al booking-funktionalitet (opret, ret, aflys, søg og filtrer bookinger).
public class BookingController {

    private final AppointmentService appointmentService;
    private final TreatmentService treatmentService;
    private final CustomerService customerService;
    private final EmployeeService employeeService;
    private final Employee loggedInEmployee;
    private Appointment selectedForEdit = null;

    public BookingController(AppointmentService appointmentService,
                             TreatmentService treatmentService,
                             CustomerService customerService,
                             EmployeeService employeeService,
                             Employee loggedInEmployee) {
        this.appointmentService = appointmentService;
        this.treatmentService = treatmentService;
        this.customerService = customerService;
        this.employeeService = employeeService;
        this.loggedInEmployee = loggedInEmployee;
    }

    @FXML private DatePicker timeDate;
    @FXML private Label labelException;
    @FXML private TextField emailTxt;
    @FXML private TextField nameTxt;
    @FXML private ComboBox<String> timeCombo;
    @FXML private ComboBox<Treatment> treatmentCombo;

    // Filter felter
    @FXML private DatePicker filterFrom;
    @FXML private DatePicker filterTo;
    @FXML private ComboBox<Employee> filterEmployee;
    @FXML private CheckBox filterCancelled;
    @FXML private CheckBox filterExpired;

    @FXML private TableView<Appointment> appointmentTable;
    @FXML private TableColumn<Appointment, Integer> colId;
    @FXML private TableColumn<Appointment, String> colName;
    @FXML private TableColumn<Appointment, String> colEmail;
    @FXML private TableColumn<Appointment, String> colTreatment;
    @FXML private TableColumn<Appointment, String> colTime;
    @FXML private TableColumn<Appointment, String> colStatus;
    @FXML private TableColumn<Appointment, String> colEmployee;

    @FXML private Button createButton;
    @FXML private Button deleteButton;
    @FXML private Button changeButton;
    @FXML private TextField searchField;

    private final ObservableList<Appointment> appointments = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        treatmentCombo.setItems(FXCollections.observableArrayList(treatmentService.findAll()));
        filterEmployee.setItems(FXCollections.observableArrayList(employeeService.findAll()));

        ObservableList<String> tider = FXCollections.observableArrayList();
        for (int t = 8; t <= 17; t++) {
            tider.add(String.format("%02d:00", t));
            tider.add(String.format("%02d:30", t));
        }
        timeCombo.setItems(tider);

        timeCombo.setItems(tider);

        // Grayer optagne tider ud når dato vælges
        timeDate.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) return;
            List<String> booked = appointmentService.findBookedTimesForEmployee(
                    loggedInEmployee.getId(), newVal);
            timeCombo.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) { setText(null); setDisable(false); }
                    else {
                        setText(item);
                        setDisable(booked.contains(item));
                        setStyle(booked.contains(item) ? "-fx-opacity: 0.4;" : "");
                    }
                }
            });
        });

        // Forhindrer crash hvis bruger skriver ugyldig tekst i DatePicker
        StringConverter<LocalDate> dateConverter = new StringConverter<>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

            @Override
            public String toString(LocalDate date) {
                return date != null ? date.format(formatter) : "";
            }

            @Override
            public LocalDate fromString(String text) {
                if (text == null || text.isBlank()) return null;
                try {
                    return LocalDate.parse(text, formatter);
                } catch (Exception e) {
                    return null;
                }
            }
        };
        timeDate.setConverter(dateConverter);
        filterFrom.setConverter(dateConverter);
        filterTo.setConverter(dateConverter);

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
        colStatus.setCellValueFactory(data ->
                new SimpleStringProperty(switch (data.getValue().getStatus()) {
                    case ACTIVE -> "Aktiv";
                    case CANCELLED -> "Aflyst";
                    case EXPIRED -> "Udløbet";
                }));
        colEmployee.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getEmployeeName()));

        appointmentTable.setItems(appointments);

        appointmentTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Appointment item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setStyle("");
                } else {
                    switch (item.getStatus()) {
                        case CANCELLED -> setStyle("-fx-background-color: #ffcccc;");
                        case EXPIRED -> setStyle("-fx-background-color: #eeeeee;");
                        case ACTIVE -> setStyle(getIndex() % 2 == 0 ? "-fx-background-color: #f0f4ff;" : "");
                    }
                }
            }
        });

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
        appointments.setAll(appointmentService.findActive());
    }

    @FXML
    private void onFilter() {
        LocalDate from = filterFrom.getValue();
        LocalDate to = filterTo.getValue();
        Employee selectedEmployee = filterEmployee.getValue();
        boolean includeCancelled = filterCancelled.isSelected();
        boolean includeExpired = filterExpired.isSelected();

        LocalDateTime fromDt = from != null
                ? from.atStartOfDay()
                : LocalDate.of(2000, 1, 1).atStartOfDay();
        LocalDateTime toDt = to != null
                ? to.plusDays(1).atStartOfDay()
                : LocalDate.of(2100, 1, 1).atStartOfDay();

        if (fromDt.isAfter(toDt) || fromDt.isEqual(toDt)) {
            labelException.setText("Fejl: Fra-dato skal være før til-dato");
            return;
        }

        Integer employeeId = selectedEmployee != null ? selectedEmployee.getId() : null;

        try {
            List<Appointment> results = appointmentService.findByStatus(
                    includeCancelled, includeExpired, fromDt, toDt, employeeId);
            appointments.setAll(results);
            labelException.setText("");
        } catch (ValidationException e) {
            labelException.setText("Fejl: " + e.getMessage());
        } catch (DataAccessException e) {
            labelException.setText("Der opstod en databasefejl – prøv igen");
            System.err.println("DB fejl i onFilter: " + e.getMessage());
        } catch (Exception e) {
            labelException.setText("Der opstod en uventet fejl – prøv igen");
            System.err.println("Uventet fejl i onFilter: " + e.getMessage());
        }
    }

    @FXML
    private void clearFilter() {
        filterFrom.setValue(null);
        filterTo.setValue(null);
        filterEmployee.setValue(null);
        filterCancelled.setSelected(false);
        filterExpired.setSelected(false);
        loadAppointments();
        labelException.setText("");
    }

    @FXML
    private void onCreateBooking() {
        try {
            if (timeDate.getValue() == null) {
                labelException.setText("Fejl: Vælg en gyldig dato");
                return;
            }

            Customer customer = customerService.findOrCreate(
                    emailTxt.getText(), nameTxt.getText());

            int duration = treatmentCombo.getValue().getDurationMinutes();
            LocalTime time = LocalTime.parse(timeCombo.getValue(), DateTimeFormatter.ofPattern("HH:mm"));
            LocalDateTime startTime = LocalDateTime.of(timeDate.getValue(), time);
            Treatment selectedTreatment = treatmentCombo.getValue();

            if (selectedForEdit != null) {
                Appointment updated = new Appointment(
                        selectedForEdit.getAppointmentId(),
                        customer.getId(), loggedInEmployee.getId(),
                        emailTxt.getText(), nameTxt.getText(),
                        startTime, duration, List.of(selectedTreatment)
                );
                appointmentService.update(updated);
                selectedForEdit = null;
                createButton.setText("Opret booking");
            } else {
                Appointment a = new Appointment(
                        0, customer.getId(), loggedInEmployee.getId(),
                        emailTxt.getText(), nameTxt.getText(),
                        startTime, duration, List.of(selectedTreatment)
                );
                appointmentService.create(a);
            }
            loadAppointments();
            clearFields();
            labelException.setText("");

        } catch (BookingConflictException | ValidationException e) {
            labelException.setText("Fejl: " + e.getMessage());
        } catch (DataAccessException e) {
            labelException.setText("Der opstod en databasefejl – prøv igen");
            System.err.println("DB fejl i onCreateBooking: " + e.getMessage());
        } catch (Exception e) {
            labelException.setText("Der opstod en uventet fejl – prøv igen");
            System.err.println("Uventet fejl i onCreateBooking: " + e.getMessage());
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
        } catch (ValidationException e) {
            labelException.setText("Fejl: " + e.getMessage());
        } catch (DataAccessException e) {
            labelException.setText("Der opstod en databasefejl – prøv igen");
            System.err.println("DB fejl i onDeleteBooking: " + e.getMessage());
        } catch (Exception e) {
            labelException.setText("Der opstod en uventet fejl – prøv igen");
            System.err.println("Uventet fejl i onDeleteBooking: " + e.getMessage());
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
        timeDate.getEditor().clear();
        treatmentCombo.setValue(null);
        selectedForEdit = null;
        createButton.setText("Opret booking");
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText();
        if (!query.isEmpty()) {
            appointments.setAll(appointmentService.searchByCustomerName(query));
            appointmentTable.setItems(appointments);
        } else {
            labelException.setText("Søgeord må ikke være tomt!");
        }
    }

    @FXML
    private void clearSearch() {
        searchField.clear();
        loadAppointments();
    }
}