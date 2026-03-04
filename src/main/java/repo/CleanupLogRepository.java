package repo;

// Repository-lag (interface): Definerer kontrakt for logning af cleanup-resultater.
public interface CleanupLogRepository {
    void logCleanup(int deletedCount, String errorMessage);
}