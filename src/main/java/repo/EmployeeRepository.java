package repo;

import domain.Employee;

import java.util.Optional;

public interface EmployeeRepository {
    Optional<Employee> findByUsernameAndPassword(String username, String password);
}