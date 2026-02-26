package repo;

import domain.Appointment;

import java.time.LocalDateTime;
import java.util.*;


// Test hooks til assertions
public class FakeAppointmentRepository implements AppointmentRepository {

    private final Map<Integer, Appointment> data = new HashMap<>();

    private boolean overlap = false;
    private int overlapCalls = 0;
    private int findCalls = 0;

    // Test-hooks (valgfrit men nyttigt)
    private Integer lastIgnoreAppointmentId = null;
    private int deleteOlderThanCalls = 0;
    private LocalDateTime lastCutoff = null;
    private int deleteOlderThanResult = 0;

    public void setOverlap(boolean overlap) {
        this.overlap = overlap;
    }

    public int getOverlapCalls() {
        return overlapCalls;
    }

    public int getFindCalls() {
        return findCalls;
    }

    public Integer getLastIgnoreAppointmentId() {
        return lastIgnoreAppointmentId;
    }

    public int getDeleteOlderThanCalls() {
        return deleteOlderThanCalls;
    }

    public LocalDateTime getLastCutoff() {
        return lastCutoff;
    }

    public void setDeleteOlderThanResult(int deleteOlderThanResult) {
        this.deleteOlderThanResult = deleteOlderThanResult;
    }

    @Override
    public Appointment save(Appointment appointment) {
        data.put(appointment.getAppointmentId(), appointment);
        return appointment;
    }

    @Override
    public Optional<Appointment> findById(int id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<Appointment> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public List<Appointment> findByCriteria(
            LocalDateTime fromInclusive,
            LocalDateTime toExclusive,
            Integer customerId,
            Integer employeeId,
            boolean includeCancelled) {

        // simpelt fake svar til tests
        return new ArrayList<>(data.values());
    }

    @Override
    public boolean existsOverlapForEmployee(int employeeId,
                                            LocalDateTime start,
                                            LocalDateTime end,
                                            Integer ignoreAppointmentId) {
        overlapCalls++;
        lastIgnoreAppointmentId = ignoreAppointmentId;
        return overlap;
    }

    @Override
    public int deleteOlderThan(LocalDateTime cutoffEndTime) {
        deleteOlderThanCalls++;
        lastCutoff = cutoffEndTime;

        return deleteOlderThanResult;
    }
}