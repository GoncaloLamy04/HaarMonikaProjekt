package domain;

public class Treatment {
    private int id;
    private String treatmentType;

    public Treatment(int time, String treatmentType){
        this.id = id;
        this.treatmentType = treatmentType;
    }

    public int getTime(){return id;}
    public void setTime(){this.id = id;}

    public String getTreatmentType(){return treatmentType;}
    public void setTreatmentType(){this.treatmentType = treatmentType;}


    @Override
    public String toString(){
        return "Treatment{" + treatmentType + "," + id + "}";
    }
}
