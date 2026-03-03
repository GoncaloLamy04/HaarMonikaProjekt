package repo;

import domain.Employee;

import java.util.List;
import java.util.Optional;

// Persistens port til Employee

public interface EmployeeRepository {
    List<Employee> findAll();
    Optional<Employee> findByUsernameAndPassword(String username, String password);
    void create(Employee employee);
}