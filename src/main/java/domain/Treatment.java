package domain;

// Domain-lag: Repræsenterer en behandling med type og varighed i minutter.
public class Treatment {
    private final int id;
    private final String treatmentType;
    private final int durationMinutes;

    public Treatment(int id, String treatmentType, int durationMinutes) {
        if (treatmentType == null || treatmentType.isBlank())
            throw new IllegalArgumentException("treatmentType must not be blank");
        if (durationMinutes <= 0)
            throw new IllegalArgumentException("durationMinutes must be > 0");

        this.id = id;
        this.treatmentType = treatmentType;
        this.durationMinutes = durationMinutes;
    }

    public int getId() { return id; }
    public String getTreatmentType() { return treatmentType; }
    public int getDurationMinutes() { return durationMinutes; }

    @Override
    public String toString() {
        return treatmentType + " (" + durationMinutes + " min)";
    }
}