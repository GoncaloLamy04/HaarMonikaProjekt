package domain;

public class Treatment {
    private String treatmentType;

    public Treatment(String treatmentType){
        this.treatmentType = treatmentType;
    }

    public String getTreatmentType(){return treatmentType;}
    public void setTreatmentType(){this.treatmentType = treatmentType;}

    @Override
    public String toString(){
        return "Treatment{"+ treatmentType +"}";
    }
}
