package dao;

import database.DBConnection;
import model.Review;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewDAO {

    public boolean addReview(int freelancerId, int rating, String comment) {
        String sql = "INSERT INTO reviews (freelancer_id, rating, comment) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (conn == null) return false;

            stmt.setInt(1, freelancerId);
            stmt.setInt(2, rating);
            stmt.setString(3, comment);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // JOIN reviews + users
    public List<Review> getAllReviews() {
        List<Review> list = new ArrayList<>();
        String sql = "SELECT r.review_id, r.freelancer_id, u.name AS freelancer_name, r.rating, r.comment " +
                     "FROM reviews r " +
                     "JOIN users u ON r.freelancer_id = u.user_id " +
                     "ORDER BY r.review_id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(new Review(
                    rs.getInt("review_id"),
                    rs.getInt("freelancer_id"),
                    rs.getString("freelancer_name"),
                    rs.getInt("rating"),
                    rs.getString("comment")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Aggregate Query: AVG(rating) and COUNT(*) for a specific freelancer
    public String getFreelancerRatingSummary(int freelancerId) {
        String sql = "SELECT COALESCE(AVG(rating), 0) AS avg_rating, COUNT(*) AS total_reviews " +
                     "FROM reviews WHERE freelancer_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (conn == null) return "Rating: N/A";

            stmt.setInt(1, freelancerId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    double avg = rs.getDouble("avg_rating");
                    int count = rs.getInt("total_reviews");
                    if (count == 0) return "No ratings yet (0 reviews)";
                    return String.format("Average Rating: %.2f / 5.0 (%d review%s)", avg, count, count == 1 ? "" : "s");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Rating: N/A";
    }
}