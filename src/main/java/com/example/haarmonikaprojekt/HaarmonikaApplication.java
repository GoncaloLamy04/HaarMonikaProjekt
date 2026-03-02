package com.example.haarmonikaprojekt;

import infrastructure.RepositoryFactory;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import service.EmployeeService;
import ui.LoginController;

public class HaarmonikaApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        // Factory + Services
        RepositoryFactory factory = new RepositoryFactory();
        EmployeeService employeeService =
                new EmployeeService(factory.createEmployeeRepository());

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/example/haarmonikaprojekt/login-view.fxml")
        );

        // Inject service i controller
        loader.setControllerFactory(type -> {
            if (type == LoginController.class) {
                return new LoginController(employeeService);
            }
            try {
                return type.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        Scene scene = new Scene(loader.load());
        stage.setTitle("Haarmonika");
        stage.setScene(scene);
        stage.show();
    }
}