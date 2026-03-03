package com.example.haarmonikaprojekt;

import infrastructure.RepositoryFactory;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import service.AppointmentService;
import service.CleanupService;
import service.CustomerService;
import service.EmployeeService;
import service.TreatmentService;
import ui.LoginController;

import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HaarmonikaApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        RepositoryFactory factory = new RepositoryFactory();
        EmployeeService employeeService = new EmployeeService(factory.createEmployeeRepository());
        AppointmentService appointmentService = new AppointmentService(factory.createAppointmentRepository());
        TreatmentService treatmentService = new TreatmentService(factory.createTreatmentRepository());
        CustomerService customerService = new CustomerService(factory.createCustomerRepository());
        CleanupService cleanupService = new CleanupService(
                factory.createAppointmentRepository(),
                factory.createCleanupLogRepository()
        );

        // Kør cleanup ved opstart og derefter hver 30. dag
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            LocalDateTime cutoff = LocalDateTime.now().minusYears(5);
            cleanupService.deleteOlderThan(cutoff);
        }, 0, 30, TimeUnit.DAYS);

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/example/haarmonikaprojekt/login-view.fxml")
        );

        loader.setControllerFactory(type -> {
            if (type == LoginController.class) return new LoginController(
                    employeeService, appointmentService, treatmentService, customerService);
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