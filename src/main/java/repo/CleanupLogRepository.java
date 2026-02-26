package repo;

public interface CleanupLogRepository {
    void logCleanup(int deletedCount, String errorMessage);
}