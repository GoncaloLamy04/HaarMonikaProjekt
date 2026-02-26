package repo;

import domain.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository {
    List<Customer> findAll();
    Optional<Customer> findByEmail(String email);
    Optional<Customer> findById(int id);
    Customer save(Customer customer);
    void delete(int id);
}
