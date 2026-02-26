package service;

import domain.Employee;
import repo.EmployeeRepository;

import java.util.Objects;

public class EmployeeService {

    private final EmployeeRepository repo;

    public EmployeeService(EmployeeRepository repo) {
        this.repo = Objects.requireNonNull(repo, "repo");
    }

    public Employee login(String username, String password) {
        if (username == null || username.isBlank()) throw new IllegalArgumentException("username must not be blank");
        if (password == null || password.isBlank()) throw new IllegalArgumentException("password must not be blank");

        return repo.findByUsernameAndPassword(username, password)
                .orElseThrow(() -> new IllegalArgumentException("Invalid login"));
    }
}