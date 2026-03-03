package service;

import domain.Appointment;
import exceptions.BookingConflictException;
import exceptions.ValidationException;
import repo.AppointmentRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class AppointmentService {

    private final AppointmentRepository repo;

    public AppointmentService(AppointmentRepository repo) {
        this.repo = Objects.requireNonNull(repo, "repo");
    }

    // UC2 – Opret booking
    public Appointment create(Appointment appointment) {
        Objects.requireNonNull(appointment, "appointment");
        validateForCreateOrUpdate(appointment);

        boolean employeeOverlap = repo.existsOverlapForEmployee(
                appointment.getEmployeeId(),
                appointment.getStartTime(),
                appointment.getEndTime(),
                null
        );
        if (employeeOverlap) throw new BookingConflictException(
                "Medarbejderen er optaget på det valgte tidspunkt");

        boolean customerOverlap = repo.existsOverlapForCustomer(
                appointment.getCustomerId(),
                appointment.getStartTime(),
                appointment.getEndTime(),
                null
        );
        if (customerOverlap) throw new BookingConflictException(
                "Kunden har allerede en booking på det valgte tidspunkt");

        return repo.save(appointment);
    }

    // UC4 – Ret booking
    public Appointment update(Appointment appointment) {
        Objects.requireNonNull(appointment, "appointment");
        if (appointment.getAppointmentId() <= 0)
            throw new ValidationException("Booking-id er ugyldigt");

        validateForCreateOrUpdate(appointment);

        boolean employeeOverlap = repo.existsOverlapForEmployee(
                appointment.getEmployeeId(),
                appointment.getStartTime(),
                appointment.getEndTime(),
                appointment.getAppointmentId()
        );
        if (employeeOverlap) throw new BookingConflictException(
                "Medarbejderen er optaget på det valgte tidspunkt");

        boolean customerOverlap = repo.existsOverlapForCustomer(
                appointment.getCustomerId(),
                appointment.getStartTime(),
                appointment.getEndTime(),
                appointment.getAppointmentId()
        );
        if (customerOverlap) throw new BookingConflictException(
                "Kunden har allerede en booking på det valgte tidspunkt");

        return repo.save(appointment);
    }

    // UC5 – Aflys booking
    public void cancel(int appointmentId) {
        if (appointmentId <= 0)
            throw new ValidationException("Booking-id er ugyldigt");

        Appointment a = repo.findById(appointmentId)
                .orElseThrow(() -> new ValidationException("Booking blev ikke fundet"));

        a.cancel();
        repo.save(a);
    }

    // UC3 – Find/vis bookinger
    public List<Appointment> findByCriteria(
            LocalDateTime fromInclusive,
            LocalDateTime toExclusive,
            Integer customerId,
            Integer employeeId,
            boolean includeCancelled
    ) {
        if (fromInclusive == null) throw new ValidationException("Fra-dato må ikke være tom");
        if (toExclusive == null) throw new ValidationException("Til-dato må ikke være tom");
        if (!toExclusive.isAfter(fromInclusive))
            throw new ValidationException("Til-dato skal være efter fra-dato");

        return repo.findByCriteria(fromInclusive, toExclusive, customerId, employeeId, includeCancelled);
    }

    public List<String> findBookedTimesForEmployee(int employeeId, LocalDate date) {
        LocalDateTime from = date.atStartOfDay();
        LocalDateTime to = date.plusDays(1).atStartOfDay();
        return repo.findByCriteria(from, to, null, employeeId, false)
                .stream()
                .map(a -> a.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")))
                .toList();
    }

    // UC3 – Søg på kundenavn
    public List<Appointment> searchByCustomerName(String name) {
        if (name == null || name.isBlank())
            throw new ValidationException("Søgeord må ikke være tomt");
        return repo.searchByCustomerName(name);
    }

    // Henter kun aktive (ikke-aflyste) bookinger
    public List<Appointment> findActive() {
        return repo.findByCriteria(
                LocalDate.of(2000, 1, 1).atStartOfDay(),
                LocalDate.of(2100, 1, 1).atStartOfDay(),
                null, null, false
        );
    }

    public List<Appointment> findAll() {
        return repo.findAll();
    }

    private void validateForCreateOrUpdate(Appointment a) {
        if (a.getCustomerId() <= 0) throw new ValidationException("Kunde-id er ugyldigt");
        if (a.getEmployeeId() <= 0) throw new ValidationException("Medarbejder-id er ugyldigt");
        if (a.getStartTime() == null) throw new ValidationException("Tidspunkt skal vælges");
        if (a.getDurationMinutes() <= 0) throw new ValidationException("Varighed skal være større end 0");
        if (a.getEmail() == null || a.getEmail().isBlank())
            throw new ValidationException("Email må ikke være tom");

        String[] parts = a.getEmail().split("@");
        if (parts.length != 2 || parts[0].isBlank() || !parts[1].contains(".") || parts[1].endsWith("."))
            throw new ValidationException("Email er ikke gyldig – fx test@mail.com eller navn@firma.dk");
        if (a.getName() == null || a.getName().isBlank())
            throw new ValidationException("Navn må ikke være tomt");
    }
}