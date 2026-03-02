package infrastructure;

import repo.EmployeeRepository;
import repo.AppointmentRepository;
import repo.CustomerRepository;
import repo.CleanupLogRepository;

public class RepositoryFactory {

    private final SQLConnector connector;

    public RepositoryFactory() {
        this.connector = SQLConnector.getInstance();
    }

    public EmployeeRepository createEmployeeRepository() {
        return new SQLEmployeeRepository(connector);
    }

    public AppointmentRepository createAppointmentRepository() {
        return new SQLAppointmentRepository(connector);
    }

    public CustomerRepository createCustomerRepository() {
        return new SQLCustomerRepository(connector);
    }

    public CleanupLogRepository createCleanupLogRepository() {
        return new SQLCleanupLogRepository(connector);
    }
}