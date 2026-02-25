package domain;

public class Treatment {
    private String treatmentType;
    private int durationMinutes;


    public Treatment(String treatmentType, int durationMinutes){
        this.treatmentType = treatmentType;
        this.durationMinutes = durationMinutes;
    }

    public String getTreatmentType(){return treatmentType;}
    public void setTreatmentType(){this.treatmentType = treatmentType;}

    public int getDurationMinutes(){return durationMinutes;}
    public void setDurationMinutes(){this.durationMinutes = durationMinutes;}

    @Override
    public String toString(){
        return "Treatment{"+ treatmentType + durationMinutes + "}";
    }
}
