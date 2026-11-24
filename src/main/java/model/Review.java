package model;

public class Review {
    private int reviewId;
    private int freelancerId;
    private String freelancerName;
    private int rating;
    private String comment;

    public Review() {}

    public Review(int reviewId, int freelancerId, String freelancerName, int rating, String comment) {
        this.reviewId = reviewId;
        this.freelancerId = freelancerId;
        this.freelancerName = freelancerName;
        this.rating = rating;
        this.comment = comment;
    }

    public int getReviewId() { return reviewId; }
    public void setReviewId(int reviewId) { this.reviewId = reviewId; }

    public int getFreelancerId() { return freelancerId; }
    public void setFreelancerId(int freelancerId) { this.freelancerId = freelancerId; }

    public String getFreelancerName() { return freelancerName; }
    public void setFreelancerName(String freelancerName) { this.freelancerName = freelancerName; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}