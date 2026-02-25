package domain;

public class Employee {
    private int id_e;
    private String name;
    private String email;
    private String role;

    public Employee(int id_e, String name, String email, String role){
        this.id_e = id_e;
        this.name = name;
        this.email = email;
        this.role = role;
    }
    public String getName(){return name;}
    public void setName(){this.name = name;}

    public String getEmail(){return email;}
    public void setEmail(){this.email = email;}

    public int getId_e(){return id_e;}
    public void setId_e(){this.id_e = id_e;}

    public String getRole(){return role;}
    public void setRole(){this.role = role;}

}