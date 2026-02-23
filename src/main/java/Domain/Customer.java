package Domain;

public class Customer {
    private String name;
    private String email;
    private int id;

    public Customer(String email, String name, int id){
        this.name = name;
        this.email = email;
        this.id = id;
    }

    public String getName(){return name;}
    public void setName(){this.name = name;}

    public String getEmail(){return email;}
    public void setEmail(){this.email = email;}

    public int getId(){return id;}
    public void setId(){this.id = id;}


    @Override
    public String toString(){
        return  "Customer{name='" + name + "', email='" + email + "', id=" + id + "}";
    }

}
