package domain;

import java.util.Objects;

public class Treatment {
    private final String treatmentType;
    private final int durationMinutes;

    public Treatment(String treatmentType, int durationMinutes) {
        if (treatmentType == null || treatmentType.isBlank())
            throw new IllegalArgumentException("treatmentType must not be blank");
        if (durationMinutes <= 0)
            throw new IllegalArgumentException("durationMinutes must be > 0");

        this.treatmentType = treatmentType;
        this.durationMinutes = durationMinutes;
    }

    public String getTreatmentType() { return treatmentType; }
    public int getDurationMinutes() { return durationMinutes; }

    @Override
    public String toString() {
        return treatmentType + " (" + durationMinutes + " min)";
    }
}