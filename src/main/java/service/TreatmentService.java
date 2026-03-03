package service;

import domain.Treatment;
import repo.TreatmentRepository;

import java.util.List;
import java.util.Objects;

public class TreatmentService {

    private final TreatmentRepository repo;

    public TreatmentService(TreatmentRepository repo) {
        this.repo = Objects.requireNonNull(repo, "repo");
    }

    public List<Treatment> findAll() {
        return repo.findAll();
    }
}