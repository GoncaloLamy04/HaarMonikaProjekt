package com.example.haarmonikaprojekt;

import infrastructure.RepositoryFactory;
import service.*;

// App-lag: Opretter og kobler alle services og repositories, holder HaarmonikaApplication ren (SRP).
public class AppFactory {

    private final EmployeeService employeeService;
    private final AppointmentService appointmentService;
    private final TreatmentService treatmentService;
    private final CustomerService customerService;
    private final CleanupService cleanupService;

    public AppFactory() {
        RepositoryFactory repos = new RepositoryFactory();
        this.employeeService = new EmployeeService(repos.createEmployeeRepository());
        this.appointmentService = new AppointmentService(repos.createAppointmentRepository());
        this.treatmentService = new TreatmentService(repos.createTreatmentRepository());
        this.customerService = new CustomerService(repos.createCustomerRepository());
        this.cleanupService = new CleanupService(
                repos.createAppointmentRepository(),
                repos.createCleanupLogRepository()
        );
    }

    public EmployeeService getEmployeeService() { return employeeService; }
    public AppointmentService getAppointmentService() { return appointmentService; }
    public TreatmentService getTreatmentService() { return treatmentService; }
    public CustomerService getCustomerService() { return customerService; }
    public CleanupService getCleanupService() { return cleanupService; }
}