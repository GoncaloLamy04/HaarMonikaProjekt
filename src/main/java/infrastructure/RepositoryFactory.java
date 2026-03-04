package infrastructure;

import repo.*;

// Infrastructure-lag: Factory der opretter de konkrete SQL repositories med en delt SQLConnector.
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
    public TreatmentRepository createTreatmentRepository() {
        return new SQLTreatmentRepository(SQLConnector.getInstance());
    }
}