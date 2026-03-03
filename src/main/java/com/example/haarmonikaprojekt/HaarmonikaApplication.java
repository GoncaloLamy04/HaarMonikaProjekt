package com.example.haarmonikaprojekt;

import infrastructure.RepositoryFactory;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import service.AppointmentService;
import service.EmployeeService;
import service.TreatmentService;
import ui.LoginController;
import ui.MainMenuController;

public class HaarmonikaApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        RepositoryFactory factory = new RepositoryFactory();
        EmployeeService employeeService = new EmployeeService(factory.createEmployeeRepository());
        AppointmentService appointmentService = new AppointmentService(factory.createAppointmentRepository());
        TreatmentService treatmentService = new TreatmentService(factory.createTreatmentRepository());

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/example/haarmonikaprojekt/login-view.fxml")
        );

        loader.setControllerFactory(type -> {
            if (type == LoginController.class) return new LoginController(employeeService, appointmentService, treatmentService);
            if (type == MainMenuController.class) return new MainMenuController(employeeService, appointmentService, treatmentService);
            try { return type.getDeclaredConstructor().newInstance(); }
            catch (Exception e) { throw new RuntimeException(e); }
        });

        Scene scene = new Scene(loader.load(), 900, 600);
        stage.setMinWidth(800);
        stage.setMinHeight(500);
        stage.setTitle("Haarmonika");
        stage.setScene(scene);
        stage.show();
    }
}