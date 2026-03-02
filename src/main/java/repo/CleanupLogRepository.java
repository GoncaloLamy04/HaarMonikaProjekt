package repo;

// Fjerner bookings automatisk for et given interval
public interface CleanupLogRepository {
    void logCleanup(int deletedCount, String errorMessage);
}