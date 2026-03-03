package repo;

import domain.Treatment;
import java.util.List;

public interface TreatmentRepository {
    List<Treatment> findAll();
}