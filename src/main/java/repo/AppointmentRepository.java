package repo;

import domain.Appointment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

// Persistens port til Appointment (DB-implementering senere).
public interface AppointmentRepository {

    Appointment save(Appointment appointment);

    Optional<Appointment> findById(int id);

    List<Appointment> findAll();

    List<Appointment> findByCriteria(
            LocalDateTime fromInclusive,
            LocalDateTime toExclusive,
            Integer customerId,
            Integer employeeId,
            boolean includeCancelled
    );

    boolean existsOverlapForEmployee(int employeeId,
                                     LocalDateTime start,
                                     LocalDateTime end,
                                     Integer ignoreAppointmentId);

    int deleteOlderThan(LocalDateTime cutoffEndTime);
}