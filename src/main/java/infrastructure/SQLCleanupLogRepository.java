package infrastructure;

import repo.CleanupLogRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SQLCleanupLogRepository implements CleanupLogRepository {

    private final SQLConnector connector;

    public SQLCleanupLogRepository(SQLConnector connector) {
        this.connector = connector;
    }

    @Override
    public void logCleanup(int deletedCount, String errorMessage) {
        String sql = "INSERT INTO cleanup_log (deleted_count, error_message) VALUES (?, ?)";
        try (Connection con = connector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, deletedCount);
            ps.setString(2, errorMessage);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Fejl ved logCleanup()", e);
        }
    }
}