package repo;

import domain.Treatment;
import java.util.List;

// Repository-lag (interface): Definerer kontrakt for datahåndtering af behandlinger.
public interface TreatmentRepository {
    List<Treatment> findAll();
}