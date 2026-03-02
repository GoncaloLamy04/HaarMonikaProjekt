package repo;

import domain.Employee;

import java.util.Optional;

// Persistens port til Employee

public interface EmployeeRepository {
    Optional<Employee> findByUsernameAndPassword(String username, String password);
}