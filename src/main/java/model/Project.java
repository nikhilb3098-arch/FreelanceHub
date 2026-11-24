package model;

public class Project {
    private int projectId;
    private int clientId;
    private String clientName; // Useful when displaying joined data for freelancers
    private String title;
    private String description;
    private double budget;

    public Project() {}

    public Project(int projectId, int clientId, String title, String description, double budget) {
        this.projectId = projectId;
        this.clientId = clientId;
        this.title = title;
        this.description = description;
        this.budget = budget;
    }

    public Project(int clientId, String title, String description, double budget) {
        this.clientId = clientId;
        this.title = title;
        this.description = description;
        this.budget = budget;
    }

    public int getProjectId() { return projectId; }
    public void setProjectId(int projectId) { this.projectId = projectId; }

    public int getClientId() { return clientId; }
    public void setClientId(int clientId) { this.clientId = clientId; }

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getBudget() { return budget; }
    public void setBudget(double budget) { this.budget = budget; }
}