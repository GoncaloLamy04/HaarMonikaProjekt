package service;

import domain.Appointment;
import org.junit.jupiter.api.Test;
import repo.AppointmentRepository;
import repo.CleanupLogRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CleanupServiceTest {

    @Test
    void deleteOlderThan_callsRepo_andReturnsCount() {
        FakeAppointmentRepo repo = new FakeAppointmentRepo();
        FakeLogRepo logRepo = new FakeLogRepo();
        repo.deletedCount = 3;
        CleanupService service = new CleanupService(repo, logRepo);

        int result = service.deleteOlderThan(LocalDateTime.of(2021, 1,
                1, 0, 0));

        assertEquals(3, result);
        assertEquals(1, repo.deleteCalls);
        assertEquals(1, logRepo.logCalls); // logger også
    }

    @Test
    void deleteOlderThan_throwsWhenCutoffIsNull() {
        CleanupService service = new CleanupService(new FakeAppointmentRepo(), new FakeLogRepo());
        assertThrows(NullPointerException.class, () -> service.deleteOlderThan(null));
    }

    static class FakeAppointmentRepo implements AppointmentRepository {
        int deleteCalls = 0;
        int deletedCount = 0;
        LocalDateTime lastCutoff = null;

        @Override
        public int deleteOlderThan(LocalDateTime cutoff) {
            deleteCalls++;
            lastCutoff = cutoff;
            return deletedCount;
        }

        @Override public Appointment save(Appointment a) { throw new UnsupportedOperationException(); }
        @Override public Optional<Appointment> findById(int id) { throw new UnsupportedOperationException(); }
        @Override public List<Appointment> findAll() { throw new UnsupportedOperationException(); }
        @Override public List<Appointment> findByCriteria(
                LocalDateTime f, LocalDateTime t, Integer c, Integer e, boolean inc) {
            throw new UnsupportedOperationException(); }
        @Override public boolean existsOverlapForEmployee(
                int eId, LocalDateTime s, LocalDateTime e, Integer ignore) {
            throw new UnsupportedOperationException(); }
    }

    static class FakeLogRepo implements CleanupLogRepository {
        int logCalls = 0;

        @Override
        public void logCleanup(int deletedCount, String errorMessage) {
            logCalls++;
        }
    }
}