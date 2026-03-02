package infrastructure;

import domain.Customer;
import repo.CustomerRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SQLCustomerRepository implements CustomerRepository {

    private final SQLConnector connector;

    public SQLCustomerRepository(SQLConnector connector) {
        this.connector = connector;
    }

    @Override
    public List<Customer> findAll() {
        String sql = "SELECT id, name, email FROM customers";
        List<Customer> customers = new ArrayList<>();

        try (Connection con = connector.getConnection()) {
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                try (ResultSet rs = ps.executeQuery()) {

                    while (rs.next()) {
                        customers.add(mapRow(rs));
                    }
                    return customers;

                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Fejl ved findAll()", e);
        }
    }

    @Override
    public Optional<Customer> findByEmail(String email) {
        String sql = "SELECT id, name, email FROM customers WHERE email = ?";

        try (Connection con = connector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Fejl ved findByEmail()", e);
        }
    }

    @Override
    public Optional<Customer> findById(int id) {
        String sql = "SELECT id, name, email FROM customers WHERE id = ?";

        try (Connection con = connector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Fejl ved findById()", e);
        }
    }

    @Override
    public Customer save(Customer customer) {
        String sql = "INSERT INTO customers (name, email) VALUES (?, ?)";

        try (Connection con = connector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, customer.getName());
            ps.setString(2, customer.getEmail());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int generatedId = keys.getInt(1);
                    return new Customer(generatedId,
                            customer.getName(),
                            customer.getEmail());
                }
                throw new SQLException("No ID returned");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Fejl ved save()", e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM customers WHERE id = ?";

        try (Connection con = connector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Fejl ved delete()", e);
        }
    }

    private Customer mapRow(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String email = rs.getString("email");
        return new Customer(id, name, email);
    }
}
