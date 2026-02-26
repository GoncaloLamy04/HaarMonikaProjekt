package service;

import domain.Appointment;
import repo.AppointmentRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

// Forretningsregler for bookinger: valider input, undgå overlap, deleger persistence til repo.
public class AppointmentService {

    private final AppointmentRepository repo;

    public AppointmentService(AppointmentRepository repo) {
        this.repo = Objects.requireNonNull(repo, "repo");
    }

    // UC2 – Opret booking
    public Appointment create(Appointment appointment) {
        Objects.requireNonNull(appointment, "appointment");
        validateForCreateOrUpdate(appointment);

        boolean overlap = repo.existsOverlapForEmployee(
                appointment.getEmployeeId(),
                appointment.getStartTime(),
                appointment.getEndTime(),
                null
        );

        if (overlap) throw new IllegalStateException("Overlap detected");
        return repo.save(appointment);
    }

    // UC4 – Ret booking
    public Appointment update(Appointment appointment) {
        Objects.requireNonNull(appointment, "appointment");
        if (appointment.getAppointmentId() <= 0) {
            throw new IllegalArgumentException("appointmentId must be > 0");
        }

        validateForCreateOrUpdate(appointment);

        boolean overlap = repo.existsOverlapForEmployee(
                appointment.getEmployeeId(),
                appointment.getStartTime(),
                appointment.getEndTime(),
                appointment.getAppointmentId()
        );

        if (overlap) throw new IllegalStateException("Overlap detected");
        return repo.save(appointment);
    }

    // UC5 – Aflys booking
    public void cancel(int appointmentId) {
        if (appointmentId <= 0) {
            throw new IllegalArgumentException("appointmentId must be > 0");
        }

        Appointment a = repo.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));

        a.cancel();
        repo.save(a);
    }

    // UC3 – Find/vis bookinger (filter/søg)
    public List<Appointment> findByCriteria(
            LocalDateTime fromInclusive,
            LocalDateTime toExclusive,
            Integer customerId,
            Integer employeeId,
            boolean includeCancelled
    ) {
        if (fromInclusive == null) throw new IllegalArgumentException("fromInclusive must not be null");
        if (toExclusive == null) throw new IllegalArgumentException("toExclusive must not be null");
        if (!toExclusive.isAfter(fromInclusive)) throw new IllegalArgumentException("toExclusive must be after fromInclusive");

        return repo.findByCriteria(fromInclusive, toExclusive, customerId, employeeId, includeCancelled);
    }

    public List<Appointment> findAll() {
        return repo.findAll();
    }

    // Samlet input-validering for create/update
    private void validateForCreateOrUpdate(Appointment a) {
        if (a.getCustomerId() <= 0) throw new IllegalArgumentException("customerId must be > 0");
        if (a.getEmployeeId() <= 0) throw new IllegalArgumentException("employeeId must be > 0");
        if (a.getStartTime() == null) throw new IllegalArgumentException("startTime must not be null");
        if (a.getDurationMinutes() <= 0) throw new IllegalArgumentException("durationMinutes must be > 0");
        if (a.getEmail() == null || a.getEmail().isBlank()) throw new IllegalArgumentException("email must not be blank");
        if (a.getName() == null || a.getName().isBlank()) throw new IllegalArgumentException("name must not be blank");
        // treatments valideres i domain ctor (Objects.requireNonNull)
    }
}