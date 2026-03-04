package domain;

// Domain-lag: Repræsenterer en kunde med navn og email.
public class Customer {

    private final int id;
    private final String name;
    private final String email;

    public Customer(int id, String name, String email){
        this.id = id;
        this.name = name;
        this.email = email;

    }

    public String getName(){ return name; }
    public String getEmail(){ return email; }
    public int getId(){ return id; }

    @Override
    public String toString(){
        return "Customer{name='" + name + "', email='" + email + "', id=" + id + "}";
    }
}