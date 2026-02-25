package repo;

import domain.Appointment;

import java.util.List;
import java.util.Optional;


public interface AppointmentRepository {

    Appointment save (Appointment appointment);

    Optional<Appointment> findById(int id);

    List<Appointment> findAll();

    List<Appointment> findByEmployeeId(int Employee);

    void delete(int customerid);

    boolean conflictExists(int employeeId, int durationMinutes);
}
