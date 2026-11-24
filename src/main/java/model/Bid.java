package model;

public class Bid {
    private int bidId;
    private int projectId;
    private String projectTitle;
    private int freelancerId;
    private String freelancerName;
    private String freelancerEmail;
    private double bidAmount;

    public Bid() {}

    public Bid(int bidId, int projectId, String projectTitle, int freelancerId, String freelancerName, String freelancerEmail, double bidAmount) {
        this.bidId = bidId;
        this.projectId = projectId;
        this.projectTitle = projectTitle;
        this.freelancerId = freelancerId;
        this.freelancerName = freelancerName;
        this.freelancerEmail = freelancerEmail;
        this.bidAmount = bidAmount;
    }

    public int getBidId() { return bidId; }
    public void setBidId(int bidId) { this.bidId = bidId; }

    public int getProjectId() { return projectId; }
    public void setProjectId(int projectId) { this.projectId = projectId; }

    public String getProjectTitle() { return projectTitle; }
    public void setProjectTitle(String projectTitle) { this.projectTitle = projectTitle; }

    public int getFreelancerId() { return freelancerId; }
    public void setFreelancerId(int freelancerId) { this.freelancerId = freelancerId; }

    public String getFreelancerName() { return freelancerName; }
    public void setFreelancerName(String freelancerName) { this.freelancerName = freelancerName; }

    public String getFreelancerEmail() { return freelancerEmail; }
    public void setFreelancerEmail(String freelancerEmail) { this.freelancerEmail = freelancerEmail; }

    public double getBidAmount() { return bidAmount; }
    public void setBidAmount(double bidAmount) { this.bidAmount = bidAmount; }
}