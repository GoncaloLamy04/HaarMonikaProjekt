package domain;

public class Appointment {
    private String customerid;
    private String email;
    private String name;
    private int Employee;

    public Appointment(String customerid, String email, String name, int Employee){
        this.customerid = customerid;
        this.email = email;
        this.name = name;
        this.Employee = Employee;
    }

    public String getCustomerid(){return customerid;}
    public void setCustomerid(){this.customerid = customerid;}

    public String getemail(){return email;}
    public void setEmail(){this.email = email;}

    public String getname(){return name;}
    public void setname(){this.name = name;}

    public int getEmployee(){return Employee;}
    public void setEmployee(){this.Employee = Employee;}
}