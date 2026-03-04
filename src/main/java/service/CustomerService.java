package service;

// Service-lag: Håndterer forretningslogik for kunder (finder eller opretter kunde baseret på email).
import domain.Customer;
import repo.CustomerRepository;
import java.util.Objects;

public class CustomerService {

    private final CustomerRepository repo;

    public CustomerService(CustomerRepository repo) {
        this.repo = Objects.requireNonNull(repo, "repo");
    }

    // Slår kunden op på email – opretter dem hvis de ikke findes
    public Customer findOrCreate(String email, String name) {
        return repo.findByEmail(email)
                .orElseGet(() -> repo.save(new Customer(0, name, email)));
    }
}