package infrastructure;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLCleanupLogRepository {

    private final SQLConnector connector;

    public SQLCleanupLogRepository(SQLConnector connector) {
        this.connector = connector;
    }

    public void logCleanup(int deletedCount, String errorMessage) {
        String sql = "DELETE FROM customers WHERE deleted = ?";

        try {Connection con = connector.getConnection(); {
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
        }
    } catch (SQLException e) {
        throw  new RuntimeException("SQL Error: " + e.getMessage());}
    }
}
