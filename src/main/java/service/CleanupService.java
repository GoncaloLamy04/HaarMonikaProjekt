package service;

import repo.AppointmentRepository;
import repo.CleanupLogRepository;

import java.time.LocalDateTime;
import java.util.Objects;

// Service-lag: UC6, Håndterer automatisk sletning af bookinger ældre end 5 år.
// Logger antal slettede rækker ved succes og fejlbesked ved fejl, uden at systemet crasher.
public class CleanupService {

    private final AppointmentRepository appointmentRepo;
    private final CleanupLogRepository logRepo;

    public CleanupService(AppointmentRepository appointmentRepo, CleanupLogRepository logRepo) {
        this.appointmentRepo = Objects.requireNonNull(appointmentRepo, "appointmentRepo");
        this.logRepo = Objects.requireNonNull(logRepo, "logRepo");
    }

    public int deleteOlderThan(LocalDateTime cutoffEndTime) {
        Objects.requireNonNull(cutoffEndTime, "cutoffEndTime");
        try {
            int deleted = appointmentRepo.deleteOlderThan(cutoffEndTime);
            logRepo.logCleanup(deleted, null);
            return deleted;
        } catch (Exception e) {
            logRepo.logCleanup(0, e.getMessage());
            throw e;
        }
    }
}