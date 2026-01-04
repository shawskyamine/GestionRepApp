package presentation.ui.panels;

import java.util.Date;

public class Transaction {
    private Date date;
    private String description;
    private String type;
    private double amount;

    public Transaction(Date date, String description, String type, double amount) {
        this.date = date;
        this.description = description;
        this.type = type;
        this.amount = amount;
    }

    // Getters
    public Date getDate() { return date; }
    public String getDescription() { return description; }
    public String getType() { return type; }
    public double getAmount() { return amount; }
}
