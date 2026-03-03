package com.example.haarmonikaprojekt;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ui.LoginController;

import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HaarmonikaApplication extends Application {

    private ScheduledExecutorService scheduler;

    @Override
    public void start(Stage stage) throws Exception {

        AppFactory factory = new AppFactory();

        // Kør cleanup ved opstart og derefter hver 30. dag
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            LocalDateTime cutoff = LocalDateTime.now().minusYears(5);
            factory.getCleanupService().deleteOlderThan(cutoff);
        }, 0, 30, TimeUnit.DAYS);

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/example/haarmonikaprojekt/login-view.fxml")
        );

        loader.setControllerFactory(type -> {
            if (type == LoginController.class) return new LoginController(
                    factory.getEmployeeService(),
                    factory.getAppointmentService(),
                    factory.getTreatmentService(),
                    factory.getCustomerService());
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

    @Override
    public void stop() {
        if (scheduler != null) scheduler.shutdown();
    }
}