package infrastructure;

import domain.Employee;
import exceptions.DataAccessException;
import repo.EmployeeRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Infrastructure-lag: SQL implementering af EmployeeRepository, håndterer login og medarbejder-opslag.
public class SQLEmployeeRepository implements EmployeeRepository {

    private final SQLConnector connector;

    public SQLEmployeeRepository(SQLConnector connector) {
        this.connector = connector;
    }

    @Override
    public List<Employee> findAll() {
        String sql = "SELECT id, name, email, role FROM employees";
        List<Employee> employees = new ArrayList<>();
        try (Connection con = connector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                employees.add(new Employee(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("role")
                ));
            }
            return employees;
        } catch (SQLException e) {
            throw new DataAccessException("Fejl ved findAll() employees", e);
        }
    }

    @Override
    public Optional<Employee> findByUsernameAndPassword(String username, String password) {

        String sql = "SELECT * FROM employees WHERE username = ? AND password = ?";

        try (Connection con = connector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(new Employee(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("role")
                ));
            }

            return Optional.empty();

        } catch (SQLException e) {
            throw new DataAccessException("DB fejl ved login", e);
        }
    }

    @Override
    public void create(Employee employee) {

        String sql = "INSERT INTO employees (name, email, role) VALUES (?, ?, ?)";

        try (Connection con = connector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, employee.getName());
            ps.setString(2, employee.getEmail());
            ps.setString(3, employee.getRole());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException("DB fejl ved oprettelse af medarbejder", e);
        }
    }
}