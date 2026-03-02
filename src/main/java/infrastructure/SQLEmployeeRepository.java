package infrastructure;

import domain.Employee;
import repo.EmployeeRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class SQLEmployeeRepository implements EmployeeRepository {

    private final SQLConnector connector;

    public SQLEmployeeRepository(SQLConnector connector) {
        this.connector = connector;
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
            throw new RuntimeException("DB fejl ved login", e);
        }
    }

    // Test klasse, kommer ikke med i commit
    public static void main(String[] args) {
        SQLConnector connector = SQLConnector.getInstance(
        );

        SQLEmployeeRepository repo = new SQLEmployeeRepository(connector);

        Optional<Employee> result = repo.findByUsernameAndPassword("anna", "password123");

        if (result.isPresent()) {
            System.out.println("Login virker! Fandt: " + result.get().getName());
        } else {
            System.out.println("Ingen medarbejder fundet — tjek brugernavn/adgangskode");
        }
    }
}