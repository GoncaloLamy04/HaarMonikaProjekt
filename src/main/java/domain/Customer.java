package domain;

public class Customer {

    private final String name;
    private final String email;
    private final int id;

    public Customer(String email, String name, int id){
        this.name = name;
        this.email = email;
        this.id = id;
    }

    public String getName(){ return name; }
    public String getEmail(){ return email; }
    public int getId(){ return id; }

    @Override
    public String toString(){
        return "Customer{name='" + name + "', email='" + email + "', id=" + id + "}";
    }
}