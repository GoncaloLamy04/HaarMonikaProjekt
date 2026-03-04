package service;

import domain.Employee;
import exceptions.ValidationException;
import repo.EmployeeRepository;

import java.util.List;
import java.util.Objects;

// Service-lag: Håndterer forretningslogik for medarbejdere (login og opslag).
public class EmployeeService {

    private final EmployeeRepository repo;

    public EmployeeService(EmployeeRepository repo) {
        this.repo = Objects.requireNonNull(repo, "repo");
    }

    public Employee login(String username, String password) {
        if (username == null || username.isBlank()) throw new ValidationException("Brugernavn må ikke være tomt");
        if (password == null || password.isBlank()) throw new ValidationException("Adgangskode må ikke være tom");

        return repo.findByUsernameAndPassword(username, password)
                .orElseThrow(() -> new ValidationException("Forkert brugernavn eller adgangskode"));
    }

    public List<Employee> findAll() {
        return repo.findAll();
    }

    public void createEmployee(String name,
                               String email,
                               String username,
                               String password,
                               String role) {

        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name must not be blank");
        if (email == null || email.isBlank()) throw new IllegalArgumentException("Email must not be blank");
        if (username == null || username.isBlank()) throw new IllegalArgumentException("Username must not be blank");
        if (password == null || password.isBlank()) throw new IllegalArgumentException("Password must not be blank");
        if (role == null || role.isBlank()) throw new IllegalArgumentException("Role must not be blank");

        Employee employee = new Employee(
                0,
                name,
                email,
                role
        );

        repo.create(employee);
    }
}

