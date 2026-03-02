package infrastructure;

import domain.Appointment;
import domain.Customer;
import domain.Employee;
import repo.AppointmentRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SQLAppointmentRepository implements AppointmentRepository {

    private final SQLConnector connector;

    public SQLAppointmentRepository(SQLConnector connector) {
        this.connector = connector;
    }

    @Override
    public Appointment save(Appointment appointment) {
        return null;
    }

    {}

    @Override
    public Optional<Appointment> findById(int id) {

        String sql = "SELECT id, name, email FROM customers WHERE id = ?";
        try (Connection con = connector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Fejl ved findById()", e);
        }
    }

    @Override
    public List<Appointment> findAll() {
        String sql = "SELECT id, customer_id, employee_id, name, email, start_time, duration_minutes * FROM appointments";
        List<Appointment> appointments = new ArrayList<>();
        try (Connection con = connector.getConnection()) {
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                try (ResultSet rs = ps.executeQuery()) {

                    while (rs.next()) {
                        appointments.add(mapRow(rs));
                    }
                    return appointments;

                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Fejl ved findAll()", e);
        }
    }

    @Override
    public List<Appointment> findByCriteria(
            LocalDateTime fromInclusive,
            LocalDateTime toExclusive,
            Integer customerId,
            Integer employeeId,
            boolean includeCancelled
    ) {
        return null;
    }

    @Override
    public boolean existsOverlapForEmployee(int employeeId,
                                            LocalDateTime start,
                                            LocalDateTime end,
                                            Integer ignoreAppointmentId) {
        return false;
    }

    @Override
    public int deleteOlderThan(LocalDateTime cutoffEndTime) {
        return 0;
    }

    private Appointment mapRow(ResultSet rs) throws SQLException {
        int appointmentId = rs.getInt("id");
        int customerId = rs.getInt("name");
        int employeeId = rs.getInt("email");
        String email = rs.getString("email");
        String name = rs.getString("name");
        LocalDateTime startTime = rs.getTimestamp("start_time").toLocalDateTime();
        int durationMinutes = rs.getInt("duration");
        return new Appointment(appointmentId, customerId, employeeId, email, name, startTime,durationMinutes );
    }
}
