package domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

// Domain-lag: Repræsenterer en booking med kunde, medarbejder, tidspunkt og behandlinger.
public class Appointment {

    private final int appointmentId;
    private final int customerId;
    private final int employeeId;
    private final String email;
    private final String name;
    private final LocalDateTime startTime;
    private final int durationMinutes;

    private String employeeName = "";

    private final List<Treatment> treatments;

    private boolean cancelled;

    // Eksisterende ctor (så dine tests stadig virker)
    public Appointment(int appointmentId, int customerId, int employeeId,
                       String email, String name,
                       LocalDateTime startTime, int durationMinutes) {
        this(appointmentId, customerId, employeeId, email, name, startTime, durationMinutes, List.of());
    }

    // Ny actor som UI/Service kan bruge
    public Appointment(int appointmentId, int customerId, int employeeId,
                       String email, String name,
                       LocalDateTime startTime, int durationMinutes,
                       List<Treatment> treatments) {

        if (appointmentId < 0) throw new IllegalArgumentException("appointmentId must be >= 0");
        if (customerId <= 0) throw new IllegalArgumentException("customerId must be > 0");
        if (employeeId <= 0) throw new IllegalArgumentException("employeeId must be > 0");
        if (email == null || email.isBlank()) throw new IllegalArgumentException("email must not be blank");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("name must not be blank");
        this.startTime = Objects.requireNonNull(startTime, "startTime");
        if (durationMinutes <= 0) throw new IllegalArgumentException("durationMinutes must be > 0");

        this.appointmentId = appointmentId;
        this.customerId = customerId;
        this.employeeId = employeeId;
        this.email = email;
        this.name = name;
        this.durationMinutes = durationMinutes;

        this.treatments = List.copyOf(Objects.requireNonNull(treatments, "treatments"));
        this.cancelled = false;
    }

    public AppointmentStatus getStatus() {
        if (cancelled) return AppointmentStatus.CANCELLED;
        if (getEndTime().isBefore(LocalDateTime.now())) return AppointmentStatus.EXPIRED;
        return AppointmentStatus.ACTIVE;
    }

    public int getAppointmentId() { return appointmentId; }
    public int getCustomerId() { return customerId; }
    public int getEmployeeId() { return employeeId; }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public LocalDateTime getStartTime() { return startTime; }
    public int getDurationMinutes() { return durationMinutes; }

    public List<Treatment> getTreatments() { return treatments; }

    public LocalDateTime getEndTime() {
        return startTime.plusMinutes(durationMinutes);
    }

    public boolean isCancelled() { return cancelled; }

    public void cancel() {
        this.cancelled = true;
    }

    // Bruges kun af SQLAppointmentRepository til at genskabe DB-tilstand
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
}