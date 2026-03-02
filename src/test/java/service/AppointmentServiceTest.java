package service;

import domain.Appointment;
import org.junit.jupiter.api.Test;
import repo.AppointmentRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AppointmentServiceTest {

    @Test
    void create_throwsWhenOverlap() {
        // Arrange
        FakeRepo repo = new FakeRepo();
        repo.overlap = true;
        AppointmentService service = new AppointmentService(repo);
        Appointment a = new Appointment(
                1, 10, 5, "a@a.dk", "Anna",
                LocalDateTime.of(2026, 2, 26, 10, 0), 30);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> service.create(a));
        assertEquals(1, repo.overlapCalls);
        assertEquals(0, repo.saveCalls);
        assertNull(repo.lastIgnoreAppointmentId);
    }

    @Test
    void create_callsSaveWhenNoOverlap() {
        // Arrange
        FakeRepo repo = new FakeRepo();
        repo.overlap = false;
        AppointmentService service = new AppointmentService(repo);
        Appointment a = new Appointment(
                2, 11, 6, "b@b.dk", "Bo",
                LocalDateTime.of(2026, 2, 26, 11, 0), 30);

        // Act
        Appointment saved = service.create(a);

        // Assert
        assertSame(a, saved);
        assertEquals(1, repo.saveCalls);
        assertEquals(1, repo.overlapCalls);
        assertNull(repo.lastIgnoreAppointmentId);
    }

    @Test
    void cancel_setsCancelledTrue_andSaves() {
        // Arrange
        FakeRepo repo = new FakeRepo();
        Appointment a = new Appointment(
                7, 12, 9, "c@c.dk", "Cecilie",
                LocalDateTime.of(2026, 2, 26, 12, 0), 45);
        repo.toFind = a;
        AppointmentService service = new AppointmentService(repo);

        // Act
        service.cancel(7);

        // Assert
        assertTrue(a.isCancelled());
        assertEquals(1, repo.saveCalls);
    }

    @Test
    void cancel_throwsIfNotFound() {
        // Arrange
        FakeRepo repo = new FakeRepo();
        repo.toFind = null;
        AppointmentService service = new AppointmentService(repo);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> service.cancel(123));
        assertEquals(0, repo.saveCalls);
    }

    @Test
    void cancel_throwsWhenIdIsZero() {
        // Arrange
        FakeRepo repo = new FakeRepo();
        AppointmentService service = new AppointmentService(repo);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> service.cancel(0));
    }

    @Test
    void update_passesIgnoreAppointmentId() {
        // Arrange
        FakeRepo repo = new FakeRepo();
        repo.overlap = false;
        AppointmentService service = new AppointmentService(repo);
        Appointment a = new Appointment(
                7, 12, 9, "c@c.dk", "Cecilie",
                LocalDateTime.of(2026, 2, 26, 12, 0), 45);

        // Act
        service.update(a);

        // Assert
        assertEquals(1, repo.overlapCalls);
        assertEquals(1, repo.saveCalls);
        assertEquals(Integer.valueOf(7), repo.lastIgnoreAppointmentId);
    }

    @Test
    void update_throwsWhenOverlap_andDoesNotSave() {
        // Arrange
        FakeRepo repo = new FakeRepo();
        repo.overlap = true;
        AppointmentService service = new AppointmentService(repo);
        Appointment a = new Appointment(
                7, 12, 9, "c@c.dk", "Cecilie",
                LocalDateTime.of(2026, 2, 26, 12, 0), 45);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> service.update(a));
        assertEquals(1, repo.overlapCalls);
        assertEquals(0, repo.saveCalls);
        assertEquals(Integer.valueOf(7), repo.lastIgnoreAppointmentId);
    }

    @Test
    void update_throwsWhenAppointmentIdIsZero() {
        // Arrange
        FakeRepo repo = new FakeRepo();
        AppointmentService service = new AppointmentService(repo);
        Appointment a = new Appointment(
                0, 12, 9, "c@c.dk", "Cecilie",
                LocalDateTime.of(2026, 2, 26, 12, 0), 45);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> service.update(a));
    }

    @Test
    void create_throwsWhenDurationIsZero() {
        // Arrange & Act & Assert — domain constructor validerer
        assertThrows(IllegalArgumentException.class, () -> new Appointment(
                1, 10, 5, "a@a.dk", "Anna",
                LocalDateTime.of(2026, 2, 26, 10, 0), 0));
    }

    @Test
    void create_throwsWhenStartTimeIsNull() {
        // Arrange & Act & Assert — domain constructor validerer
        assertThrows(NullPointerException.class, () -> new Appointment(
                1, 10, 5, "a@a.dk", "Anna", null, 30));
    }

    @Test
    void create_throwsWhenTreatmentsIsNull() {
        // Arrange
        FakeRepo repo = new FakeRepo();
        AppointmentService service = new AppointmentService(repo);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> new Appointment(
                1, 10, 5, "a@a.dk", "Anna",
                LocalDateTime.of(2026, 2, 26, 10, 0), 30, null));
    }

    @Test
    void findByCriteria_throwsWhenInvalidRange() {
        // Arrange
        FakeRepo repo = new FakeRepo();
        AppointmentService service = new AppointmentService(repo);
        LocalDateTime from = LocalDateTime.of(2026, 2, 26, 10, 0);
        LocalDateTime to = LocalDateTime.of(2026, 2, 26, 10, 0);

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> service.findByCriteria(from, to, null, null, false));
    }

    @Test
    void findByCriteria_callsRepo() {
        // Arrange
        FakeRepo repo = new FakeRepo();
        AppointmentService service = new AppointmentService(repo);
        LocalDateTime from = LocalDateTime.of(2026, 2, 26, 0, 0);
        LocalDateTime to = LocalDateTime.of(2026, 2, 27, 0, 0);

        // Act
        service.findByCriteria(from, to, null, null, false);

        // Assert
        assertEquals(1, repo.findCalls);
    }

    static class FakeRepo implements AppointmentRepository {

        boolean overlap = false;
        int saveCalls = 0;
        int overlapCalls = 0;
        int findCalls = 0;
        Integer lastIgnoreAppointmentId = null;
        Appointment toFind = null;

        @Override
        public Appointment save(Appointment appointment) {
            saveCalls++;
            return appointment;
        }

        @Override
        public Optional<Appointment> findById(int id) {
            return Optional.ofNullable(toFind);
        }

        @Override
        public List<Appointment> findAll() { return List.of(); }

        @Override
        public List<Appointment> findByCriteria(LocalDateTime f, LocalDateTime t,
                                                Integer c, Integer e, boolean inc) {
            findCalls++;
            return List.of();
        }

        @Override
        public boolean existsOverlapForEmployee(int employeeId, LocalDateTime start,
                                                LocalDateTime end, Integer ignoreAppointmentId) {
            overlapCalls++;
            lastIgnoreAppointmentId = ignoreAppointmentId;
            return overlap;
        }

        @Override
        public int deleteOlderThan(LocalDateTime cutoffEndTime) { return 0; }
    }
}