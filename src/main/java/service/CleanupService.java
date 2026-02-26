package service;

import repo.AppointmentRepository;

import java.time.LocalDateTime;
import java.util.Objects;

public class CleanupService {

    private final AppointmentRepository repo;

    public CleanupService(AppointmentRepository repo) {
        this.repo = Objects.requireNonNull(repo, "repo");
    }

    // UC6: slet aftaler ældre end cutoff
    public int deleteOlderThan(LocalDateTime cutoffEndTime) {
        Objects.requireNonNull(cutoffEndTime, "cutoffEndTime");
        return repo.deleteOlderThan(cutoffEndTime);
    }
}