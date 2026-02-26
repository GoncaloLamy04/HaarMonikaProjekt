package service;

import org.junit.jupiter.api.Test;
import repo.AppointmentRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CleanupServiceTest {

    @Test
    void deleteOlderThan_callsRepo_andReturnsCount() {
        FakeRepo repo = new FakeRepo();
        repo.deletedCount = 3;

        CleanupService service = new CleanupService(repo);

        LocalDateTime cutoff = LocalDateTime.of(2021, 1, 1, 0, 0);
        int result = service.deleteOlderThan(cutoff);

        assertEquals(3, result);
        assertEquals(1, repo.deleteCalls);
        assertEquals(cutoff, repo.lastCutoff);
    }

    @Test
    void deleteOlderThan_throwsWhenCutoffIsNull() {
        CleanupService service = new CleanupService(new FakeRepo());
        assertThrows(NullPointerException.class, () -> service.deleteOlderThan(null));
    }

    static class FakeRepo implements AppointmentRepository {

        int deleteCalls = 0;
        int deletedCount = 0;
        LocalDateTime lastCutoff = null;

        @Override
        public int deleteOlderThan(LocalDateTime cutoffEndTime) {
            deleteCalls++;
            lastCutoff = cutoffEndTime;
            return deletedCount;
        }

        @Override
        public List<domain.Appointment> findByCriteria(
                LocalDateTime fromInclusive,
                LocalDateTime toExclusive,
                Integer customerId,
                Integer employeeId,
                boolean includeCancelled) {
            throw new UnsupportedOperationException();
        }

        @Override public domain.Appointment save(domain.Appointment appointment) { throw new UnsupportedOperationException(); }
        @Override public java.util.Optional<domain.Appointment> findById(int id) { throw new UnsupportedOperationException(); }
        @Override public java.util.List<domain.Appointment> findAll() { throw new UnsupportedOperationException(); }
        @Override public boolean existsOverlapForEmployee(int employeeId, LocalDateTime start, LocalDateTime end, Integer ignoreAppointmentId) { throw new UnsupportedOperationException(); }
    }
}