package service;

import domain.Appointment;
import org.junit.jupiter.api.Test;
import repo.AppointmentRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CleanupServiceTest {

    @Test
    void deleteOlderThan_callsRepo_andReturnsCount() {
        // Arrange
        FakeRepo repo = new FakeRepo();
        repo.deletedCount = 3;
        CleanupService service = new CleanupService(repo);
        LocalDateTime cutoff = LocalDateTime.of(2021, 1, 1, 0, 0);

        // Act
        int result = service.deleteOlderThan(cutoff);

        // Assert
        assertEquals(3, result);
        assertEquals(1, repo.deleteCalls);
        assertEquals(cutoff, repo.lastCutoff);
    }

    @Test
    void deleteOlderThan_throwsWhenCutoffIsNull() {
        // Arrange
        CleanupService service = new CleanupService(new FakeRepo());

        // Act & Assert
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
        public Appointment save(Appointment a) { throw new UnsupportedOperationException(); }
        @Override
        public Optional<Appointment> findById(int id) { throw new UnsupportedOperationException(); }
        @Override
        public List<Appointment> findAll() { throw new UnsupportedOperationException(); }
        @Override
        public List<Appointment> findByCriteria(LocalDateTime f, LocalDateTime t,
                                                Integer c, Integer e, boolean inc) { throw new UnsupportedOperationException(); }
        @Override
        public boolean existsOverlapForEmployee(int eId, LocalDateTime s,
                                                LocalDateTime e, Integer ignore) { throw new UnsupportedOperationException(); }
    }
}