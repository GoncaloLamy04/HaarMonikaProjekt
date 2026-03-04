package repo;

import domain.Employee;

import java.util.List;
import java.util.Optional;

// Repository-lag (interface): Definerer kontrakt for datahåndtering af medarbejdere.
public interface EmployeeRepository {
    List<Employee> findAll();
    Optional<Employee> findByUsernameAndPassword(String username, String password);
    void create(Employee employee);
}