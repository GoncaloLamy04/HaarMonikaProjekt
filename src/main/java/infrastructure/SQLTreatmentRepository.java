package infrastructure;

import domain.Treatment;
import exceptions.DataAccessException;
import repo.TreatmentRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Infrastructure-lag: SQL implementering af TreatmentRepository, henter behandlinger fra databasen.
public class SQLTreatmentRepository implements TreatmentRepository {

    private final SQLConnector connector;

    public SQLTreatmentRepository(SQLConnector connector) {
        this.connector = connector;
    }

    @Override
    public List<Treatment> findAll() {
        String sql = "SELECT id, treatment_type, duration_minutes FROM treatments";
        List<Treatment> treatments = new ArrayList<>();
        try (Connection con = connector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                treatments.add(new Treatment(
                        rs.getInt("id"),
                        rs.getString("treatment_type"),
                        rs.getInt("duration_minutes")
                ));
            }
            return treatments;
        } catch (SQLException e) {
            throw new DataAccessException("Fejl ved findAll() treatments", e);
        }
    }
}