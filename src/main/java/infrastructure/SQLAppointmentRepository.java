package infrastructure;

import domain.Appointment;
import domain.Treatment;
import exceptions.DataAccessException;
import repo.AppointmentRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Infrastructure-lag: SQL implementering af AppointmentRepository, håndterer INSERT/UPDATE via
// save(), dynamisk filtrering via findByCriteria() og overlap-tjek direkte i SQL.
public class SQLAppointmentRepository implements AppointmentRepository {

    private final SQLConnector connector;

    private static final String BASE_SELECT =
            "SELECT a.id, a.customer_id, a.employee_id, e.name as employee_name, " +
                    "a.name, a.email, a.start_time, a.duration_minutes, a.cancelled " +
                    "FROM appointments a JOIN employees e ON a.employee_id = e.id";

    public SQLAppointmentRepository(SQLConnector connector) {
        this.connector = connector;
    }

    @Override
    public Appointment save(Appointment appointment) {
        if (appointment.getAppointmentId() == 0) {
            String sql = "INSERT INTO appointments (customer_id, employee_id, name, email, start_time, duration_minutes, cancelled) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (Connection con = connector.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                bindAppointmentParams(ps, appointment);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        int id = keys.getInt(1);
                        saveTreatments(con, id, appointment.getTreatments());
                        return new Appointment(id, appointment.getCustomerId(),
                                appointment.getEmployeeId(), appointment.getEmail(),
                                appointment.getName(), appointment.getStartTime(),
                                appointment.getDurationMinutes(), appointment.getTreatments());
                    }
                }
            } catch (SQLException e) {
                throw new DataAccessException("Fejl ved save() INSERT", e);
            }
        } else {
            String sql = "UPDATE appointments SET customer_id=?, employee_id=?, name=?, email=?, start_time=?, duration_minutes=?, cancelled=? WHERE id=?";
            try (Connection con = connector.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                bindAppointmentParams(ps, appointment);
                ps.setInt(8, appointment.getAppointmentId());
                ps.executeUpdate();
                deleteTreatments(con, appointment.getAppointmentId());
                saveTreatments(con, appointment.getAppointmentId(), appointment.getTreatments());
            } catch (SQLException e) {
                throw new DataAccessException("Fejl ved save() UPDATE", e);
            }
        }
        return appointment;
    }

    private void bindAppointmentParams(PreparedStatement ps, Appointment appointment) throws SQLException {
        ps.setInt(1, appointment.getCustomerId());
        ps.setInt(2, appointment.getEmployeeId());
        ps.setString(3, appointment.getName());
        ps.setString(4, appointment.getEmail());
        ps.setTimestamp(5, Timestamp.valueOf(appointment.getStartTime()));
        ps.setInt(6, appointment.getDurationMinutes());
        ps.setBoolean(7, appointment.isCancelled());
    }

    @Override
    public Optional<Appointment> findById(int id) {
        String sql = BASE_SELECT + " WHERE a.id = ?";
        try (Connection con = connector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Fejl ved findById()", e);
        }
    }

    @Override
    public List<Appointment> findAll() {
        List<Appointment> appointments = new ArrayList<>();
        try (Connection con = connector.getConnection();
             PreparedStatement ps = con.prepareStatement(BASE_SELECT);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) appointments.add(mapRow(rs));
            return appointments;
        } catch (SQLException e) {
            throw new DataAccessException("Fejl ved findAll()", e);
        }
    }

    @Override
    public List<Appointment> findByCriteria(
            LocalDateTime fromInclusive,
            LocalDateTime toExclusive,
            Integer customerId,
            Integer employeeId,
            boolean includeCancelled) {

        StringBuilder sql = new StringBuilder(BASE_SELECT +
                " WHERE a.start_time >= ? AND a.start_time < ?");
        if (customerId != null) sql.append(" AND a.customer_id = ?");
        if (employeeId != null) sql.append(" AND a.employee_id = ?");
        if (!includeCancelled) sql.append(" AND a.cancelled = false");

        List<Appointment> result = new ArrayList<>();
        try (Connection con = connector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            int i = 1;
            ps.setTimestamp(i++, Timestamp.valueOf(fromInclusive));
            ps.setTimestamp(i++, Timestamp.valueOf(toExclusive));
            if (customerId != null) ps.setInt(i++, customerId);
            if (employeeId != null) ps.setInt(i, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapRow(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new DataAccessException("Fejl ved findByCriteria()", e);
        }
    }

    @Override
    public List<Appointment> searchByCustomerName(String name) {
        String sql = BASE_SELECT + " WHERE a.name LIKE ? AND a.cancelled = false";
        List<Appointment> results = new ArrayList<>();
        try (Connection con = connector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + name + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) results.add(mapRow(rs));
            }
            return results;
        } catch (SQLException e) {
            throw new DataAccessException("Fejl ved searchByCustomerName()", e);
        }
    }

    @Override
    public boolean existsOverlapForEmployee(int employeeId, LocalDateTime start,
                                            LocalDateTime end, Integer ignoreAppointmentId) {
        return existsOverlap("employee_id", employeeId, start, end, ignoreAppointmentId);
    }

    @Override
    public boolean existsOverlapForCustomer(int customerId, LocalDateTime start,
                                            LocalDateTime end, Integer ignoreAppointmentId) {
        return existsOverlap("customer_id", customerId, start, end, ignoreAppointmentId);
    }

    private boolean existsOverlap(String column, int id, LocalDateTime start,
                                  LocalDateTime end, Integer ignoreAppointmentId) {
        String sql = "SELECT COUNT(*) FROM appointments WHERE " + column + " = ? AND cancelled = false " +
                "AND start_time < ? AND DATE_ADD(start_time, INTERVAL duration_minutes MINUTE) > ?" +
                (ignoreAppointmentId != null ? " AND id != ?" : "");
        try (Connection con = connector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setTimestamp(2, Timestamp.valueOf(end));
            ps.setTimestamp(3, Timestamp.valueOf(start));
            if (ignoreAppointmentId != null) ps.setInt(4, ignoreAppointmentId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Fejl ved existsOverlap()", e);
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
            throw new DataAccessException("Fejl ved deleteOlderThan()", e);
        }
    }

    private Appointment mapRow(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        Appointment a = new Appointment(
                id,
                rs.getInt("customer_id"),
                rs.getInt("employee_id"),
                rs.getString("email"),
                rs.getString("name"),
                rs.getTimestamp("start_time").toLocalDateTime(),
                rs.getInt("duration_minutes"),
                findTreatmentsForAppointment(id)
        );
        a.setEmployeeName(rs.getString("employee_name"));
        if (rs.getBoolean("cancelled")) a.setCancelled(true);
        return a;
    }

    private List<Treatment> findTreatmentsForAppointment(int appointmentId) {
        String sql = "SELECT t.id, t.treatment_type, t.duration_minutes " +
                "FROM treatments t " +
                "JOIN appointment_treatments at ON t.id = at.treatment_id " +
                "WHERE at.appointment_id = ?";
        List<Treatment> treatments = new ArrayList<>();
        try (Connection con = connector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, appointmentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    treatments.add(new Treatment(
                            rs.getInt("id"),
                            rs.getString("treatment_type"),
                            rs.getInt("duration_minutes")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Fejl ved findTreatmentsForAppointment()", e);
        }
        return treatments;
    }

    private void saveTreatments(Connection con, int appointmentId, List<Treatment> treatments) throws SQLException {
        String sql = "INSERT INTO appointment_treatments (appointment_id, treatment_id) VALUES (?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (Treatment t : treatments) {
                ps.setInt(1, appointmentId);
                ps.setInt(2, t.getId());
                ps.executeUpdate();
            }
        }
    }

    private void deleteTreatments(Connection con, int appointmentId) throws SQLException {
        String sql = "DELETE FROM appointment_treatments WHERE appointment_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, appointmentId);
            ps.executeUpdate();
        }
    }
}