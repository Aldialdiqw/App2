package memberships;

import java.io.Serializable;

public class MembershipInfo implements Serializable {
    private int id;
    private String name;
    private String companyName;
    private double price;
    private String paymentDate;

    public MembershipInfo(int id, String name, String companyName, double price, String paymentDate) {
        this.id = id;
        this.name = name;
        this.companyName = companyName;
        this.price = price;
        this.paymentDate = paymentDate;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public  double getPrice() { return price; }
    public void setPrice( double price) { this.price = price; }
    public String getPaymentDate() { return paymentDate; }
    public void setPaymentDate(String paymentDate) { this.paymentDate = paymentDate; }
}

