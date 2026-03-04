package service;

import domain.Employee;
import exceptions.ValidationException;
import org.junit.jupiter.api.Test;
import repo.EmployeeRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeServiceTest {

    @Test
    void login_returnsEmployee_whenCredentialsMatch() {
        // Arrange
        FakeRepo repo = new FakeRepo();
        repo.resultEmployee = new Employee(1, "Monika", "m@salon.dk", "ADMIN");
        EmployeeService service = new EmployeeService(repo);

        // Act
        Employee e = service.login("monika", "secret");

        // Assert
        assertNotNull(e);
        assertEquals(1, repo.calls);
    }

    @Test
    void login_throws_whenInvalidCredentials() {
        FakeRepo repo = new FakeRepo();
        repo.resultEmployee = null;
        EmployeeService service = new EmployeeService(repo);

        assertThrows(ValidationException.class, () -> service.login("monika", "wrong"));
        assertEquals(1, repo.calls);
    }

    @Test
    void login_throws_whenUsernameBlank() {
        EmployeeService service = new EmployeeService(new FakeRepo());

        assertThrows(ValidationException.class, () -> service.login(" ", "x"));
    }

    @Test
    void login_throws_whenPasswordBlank() {
        EmployeeService service = new EmployeeService(new FakeRepo());

        assertThrows(ValidationException.class, () -> service.login("x", " "));
    }

    static class FakeRepo implements EmployeeRepository {
        int calls = 0;
        Employee resultEmployee = null;

        @Override
        public Optional<Employee> findByUsernameAndPassword(String username, String password) {
            calls++;
            return Optional.ofNullable(resultEmployee);
        }

        @Override
        public void create(Employee employee) {
            // Ikke brugt i disse tests
        }

        @Override
        public List<Employee> findAll() {
            return List.of(); // Ikke brugt i disse tests
        }
    }
}