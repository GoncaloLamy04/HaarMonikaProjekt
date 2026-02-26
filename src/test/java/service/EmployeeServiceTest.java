package service;

import domain.Employee;
import org.junit.jupiter.api.Test;
import repo.EmployeeRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeServiceTest {

    @Test
    void login_returnsEmployee_whenCredentialsMatch() {
        FakeRepo repo = new FakeRepo();
        repo.resultEmployee = new Employee(1, "Monika", "m@salon.dk", "ADMIN");

        EmployeeService service = new EmployeeService(repo);

        Employee e = service.login("monika", "secret");

        assertNotNull(e);
        assertEquals(1, repo.calls);
    }

    @Test
    void login_throws_whenInvalidCredentials() {
        FakeRepo repo = new FakeRepo();
        repo.resultEmployee = null; // giver Optional.empty()

        EmployeeService service = new EmployeeService(repo);

        assertThrows(IllegalArgumentException.class, () -> service.login("monika", "wrong"));
        assertEquals(1, repo.calls);
    }

    @Test
    void login_throws_whenUsernameBlank() {
        EmployeeService service = new EmployeeService(new FakeRepo());
        assertThrows(IllegalArgumentException.class, () -> service.login(" ", "x"));
    }

    @Test
    void login_throws_whenPasswordBlank() {
        EmployeeService service = new EmployeeService(new FakeRepo());
        assertThrows(IllegalArgumentException.class, () -> service.login("x", " "));
    }

    static class FakeRepo implements EmployeeRepository {
        int calls = 0;

        // Ikke Optional som field (Ellers får vi intelliJ advarsel, men her forsvinder den IntelliJ-advarsel så)
        Employee resultEmployee = null;

        @Override
        public Optional<Employee> findByUsernameAndPassword(String username, String password) {
            calls++;
            return Optional.ofNullable(resultEmployee);
        }
    }
}