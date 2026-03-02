package service;

import domain.Employee;
import org.junit.jupiter.api.Test;
import repo.EmployeeRepository;

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
        // Arrange
        FakeRepo repo = new FakeRepo();
        repo.resultEmployee = null;
        EmployeeService service = new EmployeeService(repo);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> service.login("monika", "wrong"));
        assertEquals(1, repo.calls);
    }

    @Test
    void login_throws_whenUsernameBlank() {
        // Arrange
        EmployeeService service = new EmployeeService(new FakeRepo());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> service.login(" ", "x"));
    }

    @Test
    void login_throws_whenPasswordBlank() {
        // Arrange
        EmployeeService service = new EmployeeService(new FakeRepo());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> service.login("x", " "));
    }

    static class FakeRepo implements EmployeeRepository {

        int calls = 0;
        Employee resultEmployee = null;

        @Override
        public Optional<Employee> findByUsernameAndPassword(String username, String password) {
            calls++;
            return Optional.ofNullable(resultEmployee);
        }
    }
}