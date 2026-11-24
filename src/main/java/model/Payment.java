package model;

public class Payment {
    private int paymentId;
    private int projectId;
    private String projectTitle;
    private double amount;
    private String status;

    public Payment() {}

    public Payment(int paymentId, int projectId, String projectTitle, double amount, String status) {
        this.paymentId = paymentId;
        this.projectId = projectId;
        this.projectTitle = projectTitle;
        this.amount = amount;
        this.status = status;
    }

    public int getPaymentId() { return paymentId; }
    public void setPaymentId(int paymentId) { this.paymentId = paymentId; }

    public int getProjectId() { return projectId; }
    public void setProjectId(int projectId) { this.projectId = projectId; }

    public String getProjectTitle() { return projectTitle; }
    public void setProjectTitle(String projectTitle) { this.projectTitle = projectTitle; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}