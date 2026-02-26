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
        FakeRepo repo = new FakeRepo();
        repo.overlap = true;

        AppointmentService service = new AppointmentService(repo);

        Appointment a = new Appointment(
                1, 10, 5, "a@a.dk", "Anna",
                LocalDateTime.of(2026, 2, 26, 10, 0),
                30
        );

        assertThrows(IllegalStateException.class, () -> service.create(a));
        assertEquals(1, repo.overlapCalls);
        assertEquals(0, repo.saveCalls);
        assertNull(repo.lastIgnoreAppointmentId); // create ignorer ikke noget
    }

    @Test
    void create_callsSaveWhenNoOverlap() {
        FakeRepo repo = new FakeRepo();
        repo.overlap = false;

        AppointmentService service = new AppointmentService(repo);

        Appointment a = new Appointment(
                2, 11, 6, "b@b.dk", "Bo",
                LocalDateTime.of(2026, 2, 26, 11, 0),
                30
        );

        Appointment saved = service.create(a);

        assertSame(a, saved);
        assertEquals(1, repo.saveCalls);
        assertEquals(1, repo.overlapCalls);
        assertNull(repo.lastIgnoreAppointmentId); // create ignorer ikke noget
    }

    @Test
    void cancel_setsCancelledTrue_andSaves() {
        FakeRepo repo = new FakeRepo();
        Appointment a = new Appointment(
                7, 12, 9, "c@c.dk", "Cecilie",
                LocalDateTime.of(2026, 2, 26, 12, 0),
                45
        );
        repo.toFind = a;

        AppointmentService service = new AppointmentService(repo);

        service.cancel(7);

        assertTrue(a.isCancelled());
        assertEquals(1, repo.saveCalls);
    }

    @Test
    void cancel_throwsIfNotFound() {
        FakeRepo repo = new FakeRepo();
        repo.toFind = null;

        AppointmentService service = new AppointmentService(repo);

        assertThrows(IllegalArgumentException.class, () -> service.cancel(123));
        assertEquals(0, repo.saveCalls);
    }

    @Test
    void update_passesIgnoreAppointmentId() {
        FakeRepo repo = new FakeRepo();
        repo.overlap = false;

        AppointmentService service = new AppointmentService(repo);

        Appointment a = new Appointment(
                7, 12, 9, "c@c.dk", "Cecilie",
                LocalDateTime.of(2026, 2, 26, 12, 0),
                45
        );

        service.update(a);

        assertEquals(1, repo.overlapCalls);
        assertEquals(1, repo.saveCalls);
        assertEquals(Integer.valueOf(7), repo.lastIgnoreAppointmentId); // update ignorer sig selv
    }

    @Test
    void update_throwsWhenOverlap_andDoesNotSave() {
        FakeRepo repo = new FakeRepo();
        repo.overlap = true;

        AppointmentService service = new AppointmentService(repo);

        Appointment a = new Appointment(
                7, 12, 9, "c@c.dk", "Cecilie",
                LocalDateTime.of(2026, 2, 26, 12, 0),
                45
        );

        assertThrows(IllegalStateException.class, () -> service.update(a));
        assertEquals(1, repo.overlapCalls);
        assertEquals(0, repo.saveCalls);
        assertEquals(Integer.valueOf(7), repo.lastIgnoreAppointmentId);
    }

    @Test
    void update_throwsWhenAppointmentIdIsZero() {
        FakeRepo repo = new FakeRepo();
        AppointmentService service = new AppointmentService(repo);

        Appointment a = new Appointment(
                0, 12, 9, "c@c.dk", "Cecilie",
                LocalDateTime.of(2026, 2, 26, 12, 0),
                45
        );

        assertThrows(IllegalArgumentException.class, () -> service.update(a));
    }

    @Test
    void create_throwsWhenDurationIsZero() {
        FakeRepo repo = new FakeRepo();
        AppointmentService service = new AppointmentService(repo);

        Appointment a = new Appointment(
                1, 10, 5, "a@a.dk", "Anna",
                LocalDateTime.of(2026, 2, 26, 10, 0),
                0
        );

        assertThrows(IllegalArgumentException.class, () -> service.create(a));
    }

    @Test
    void create_throwsWhenStartTimeIsNull() {
        FakeRepo repo = new FakeRepo();
        AppointmentService service = new AppointmentService(repo);

        Appointment a = new Appointment(
                1, 10, 5, "a@a.dk", "Anna",
                null,
                30
        );

        assertThrows(IllegalArgumentException.class, () -> service.create(a));
    }

    @Test
    void create_throwsWhenTreatmentsIsNull() {
        FakeRepo repo = new FakeRepo();
        AppointmentService service = new AppointmentService(repo);

        assertThrows(NullPointerException.class, () -> new Appointment(
                1, 10, 5, "a@a.dk", "Anna",
                LocalDateTime.of(2026, 2, 26, 10, 0),
                30,
                null
        ));
    }

    @Test
    void findByCriteria_throwsWhenInvalidRange() {
        FakeRepo repo = new FakeRepo();
        AppointmentService service = new AppointmentService(repo);

        LocalDateTime from = LocalDateTime.of(2026, 2, 26, 10, 0);
        LocalDateTime to = LocalDateTime.of(2026, 2, 26, 10, 0);

        assertThrows(IllegalArgumentException.class,
                () -> service.findByCriteria(from, to, null, null, false));
    }

    @Test
    void findByCriteria_callsRepo() {
        FakeRepo repo = new FakeRepo();
        AppointmentService service = new AppointmentService(repo);

        LocalDateTime from = LocalDateTime.of(2026, 2, 26, 0, 0);
        LocalDateTime to = LocalDateTime.of(2026, 2, 27, 0, 0);

        service.findByCriteria(from, to, null, null, false);

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
        public List<Appointment> findAll() {
            return List.of();
        }

        @Override
        public List<Appointment> findByCriteria(LocalDateTime fromInclusive,
                                                LocalDateTime toExclusive,
                                                Integer customerId,
                                                Integer employeeId,
                                                boolean includeCancelled) {
            findCalls++;
            return List.of();
        }

        @Override
        public boolean existsOverlapForEmployee(
                int employeeId,
                LocalDateTime start,
                LocalDateTime end,
                Integer ignoreAppointmentId
        ) {
            overlapCalls++;
            lastIgnoreAppointmentId = ignoreAppointmentId;
            return overlap;
        }

        @Override
        public int deleteOlderThan(LocalDateTime cutoffEndTime) {
            return 0;
        }
    }
}