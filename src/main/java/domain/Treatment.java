package domain;

public class Treatment {
    private int time;
    private String treatmentType;

    public Treatment(int time, String treatmentType){
        this.time = time;
        this.treatmentType = treatmentType;
    }

    public int getTime(){return time;}
    public void setTime(){this.time = time;}

    public String getTreatmentType(){return treatmentType;}
    public void setTreatmentType(){this.treatmentType = treatmentType;}


    @Override
    public String toString(){
        return "Treatment{" + treatmentType + "," + time + "}";
    }
}
