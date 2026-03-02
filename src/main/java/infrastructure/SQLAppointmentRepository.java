package infrastructure;

import domain.Appointment;
import repo.AppointmentRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Infrastructure-lag: SQL-implementering af AppointmentRepository.
// Håndterer al databasekommunikation for bookinger.
public class SQLAppointmentRepository implements AppointmentRepository {

    private final SQLConnector connector;

    public SQLAppointmentRepository(SQLConnector connector) {
        this.connector = connector;
    }

    @Override
    public Appointment save(Appointment appointment) {
        if (appointment.getAppointmentId() == 0) {
            // INSERT
            String sql = "INSERT INTO appointments (customer_id, employee_id, name, email, start_time, duration_minutes, cancelled) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (Connection con = connector.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, appointment.getCustomerId());
                ps.setInt(2, appointment.getEmployeeId());
                ps.setString(3, appointment.getName());
                ps.setString(4, appointment.getEmail());
                ps.setTimestamp(5, Timestamp.valueOf(appointment.getStartTime()));
                ps.setInt(6, appointment.getDurationMinutes());
                ps.setBoolean(7, appointment.isCancelled());
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        int id = keys.getInt(1);
                        return new Appointment(id, appointment.getCustomerId(),
                                appointment.getEmployeeId(), appointment.getEmail(),
                                appointment.getName(), appointment.getStartTime(),
                                appointment.getDurationMinutes(), appointment.getTreatments());
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("Fejl ved save() INSERT", e);
            }
        } else {
            // UPDATE
            String sql = "UPDATE appointments SET customer_id=?, employee_id=?, name=?, email=?, start_time=?, duration_minutes=?, cancelled=? WHERE id=?";
            try (Connection con = connector.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, appointment.getCustomerId());
                ps.setInt(2, appointment.getEmployeeId());
                ps.setString(3, appointment.getName());
                ps.setString(4, appointment.getEmail());
                ps.setTimestamp(5, Timestamp.valueOf(appointment.getStartTime()));
                ps.setInt(6, appointment.getDurationMinutes());
                ps.setBoolean(7, appointment.isCancelled());
                ps.setInt(8, appointment.getAppointmentId());
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Fejl ved save() UPDATE", e);
            }
        }
        return appointment;
    }

    @Override
    public Optional<Appointment> findById(int id) {
        String sql = "SELECT id, customer_id, employee_id, name, email, start_time, duration_minutes, cancelled FROM appointments WHERE id = ?";
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
        String sql = "SELECT id, customer_id, employee_id, name, email, start_time, duration_minutes, cancelled FROM appointments";
        List<Appointment> appointments = new ArrayList<>();
        try (Connection con = connector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                appointments.add(mapRow(rs));
            }
            return appointments;
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
            boolean includeCancelled) {

        StringBuilder sql = new StringBuilder(
                "SELECT id, customer_id, employee_id, name, email, start_time, duration_minutes, cancelled FROM appointments WHERE start_time >= ? AND start_time < ?");
        if (customerId != null) sql.append(" AND customer_id = ?");
        if (employeeId != null) sql.append(" AND employee_id = ?");
        if (!includeCancelled)  sql.append(" AND cancelled = false");

        List<Appointment> result = new ArrayList<>();
        try (Connection con = connector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            int i = 1;
            ps.setTimestamp(i++, Timestamp.valueOf(fromInclusive));
            ps.setTimestamp(i++, Timestamp.valueOf(toExclusive));
            if (customerId != null) ps.setInt(i++, customerId);
            if (employeeId != null) ps.setInt(i++, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapRow(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Fejl ved findByCriteria()", e);
        }
    }

    @Override
    public boolean existsOverlapForEmployee(int employeeId,
                                            LocalDateTime start,
                                            LocalDateTime end,
                                            Integer ignoreAppointmentId) {
        String sql = "SELECT COUNT(*) FROM appointments WHERE employee_id = ? AND cancelled = false AND start_time < ? AND DATE_ADD(start_time, INTERVAL duration_minutes MINUTE) > ?" +
                (ignoreAppointmentId != null ? " AND id != ?" : "");
        try (Connection con = connector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            ps.setTimestamp(2, Timestamp.valueOf(end));
            ps.setTimestamp(3, Timestamp.valueOf(start));
            if (ignoreAppointmentId != null) ps.setInt(4, ignoreAppointmentId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Fejl ved existsOverlapForEmployee()", e);
        }
    }

    @Override
    public int deleteOlderThan(LocalDateTime cutoffEndTime) {
        String sql = "DELETE FROM appointments WHERE DATE_ADD(start_time, INTERVAL duration_minutes MINUTE) < ?";
        try (Connection con = connector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(cutoffEndTime));
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Fejl ved deleteOlderThan()", e);
        }
    }

    private Appointment mapRow(ResultSet rs) throws SQLException {
        Appointment a = new Appointment(
                rs.getInt("id"),
                rs.getInt("customer_id"),
                rs.getInt("employee_id"),
                rs.getString("email"),
                rs.getString("name"),
                rs.getTimestamp("start_time").toLocalDateTime(),
                rs.getInt("duration_minutes")
        );
        if (rs.getBoolean("cancelled")) a.setCancelled(true);
        return a;
    }
}