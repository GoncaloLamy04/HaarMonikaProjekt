package domain;

import java.time.LocalDateTime;

public class Appointment {
    private int appointmentId;
    private int customerId;
    private String email;
    private String name;
    private boolean cancelled;

    private LocalDateTime startTime;



    public Appointment(String email, String name, int appointmentId , LocalDateTime startTime, int customerId){
        this.appointmentId = appointmentId;
        this.email = email;
        this.name = name;
        this.cancelled = false;
        this.startTime = startTime;
        this.customerId = customerId;

    }

    public int getAppointmentId(){return appointmentId;}
    public void setAppointmentId(int appointmentId) {this.appointmentId = appointmentId;}

    public String getEmail(){return email;}
    public void setEmail(String email){this.email = email;}

    public String getName(){return name;}
    public void setName(String name){this.name = name;}

    public int getCustomerId(){return customerId;}
    public void setCustomerId(){this.customerId = customerId;}

    public boolean isCancelled(){return cancelled;}
    public void setCancelled(boolean cancelled){this.cancelled = cancelled;}

    public LocalDateTime getStarTime(){return startTime;}
    public void setStartTime(){this.startTime = startTime;}

    @Override
    public String toString(){
        return "";
    }
}