package dao;

import database.DBConnection;
import model.Bid;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BidDAO {

    // Calls MySQL Stored Procedure PlaceBid() and triggers prevent_negative_bid if amount <= 0
    public String placeBid(int projectId, int freelancerId, double bidAmount) {
        String sql = "{CALL PlaceBid(?, ?, ?)}";
        try (Connection conn = DBConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            if (conn == null) return "Database connection failed.";

            stmt.setInt(1, projectId);
            stmt.setInt(2, freelancerId);
            stmt.setDouble(3, bidAmount);

            stmt.execute();
            return "SUCCESS";
        } catch (SQLException e) {
            return e.getMessage();
        }
    }

    // 3-table JOIN: bids + projects + users (for Client to inspect incoming bids)
    public List<Bid> getBidsForClient(int clientId) {
        List<Bid> list = new ArrayList<>();
        String sql = "SELECT b.bid_id, b.project_id, p.title AS project_title, " +
                     "b.freelancer_id, u.name AS freelancer_name, u.email AS freelancer_email, b.bid_amount " +
                     "FROM bids b " +
                     "JOIN projects p ON b.project_id = p.project_id " +
                     "JOIN users u ON b.freelancer_id = u.user_id " +
                     "WHERE p.client_id = ? " +
                     "ORDER BY b.bid_id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (conn == null) return list;

            stmt.setInt(1, clientId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new Bid(
                        rs.getInt("bid_id"),
                        rs.getInt("project_id"),
                        rs.getString("project_title"),
                        rs.getInt("freelancer_id"),
                        rs.getString("freelancer_name"),
                        rs.getString("freelancer_email"),
                        rs.getDouble("bid_amount")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 2-table JOIN: bids + projects (for Freelancer to see their submitted bids)
    public List<Bid> getBidsByFreelancer(int freelancerId) {
        List<Bid> list = new ArrayList<>();
        String sql = "SELECT b.bid_id, b.project_id, p.title AS project_title, b.bid_amount " +
                     "FROM bids b " +
                     "JOIN projects p ON b.project_id = p.project_id " +
                     "WHERE b.freelancer_id = ? " +
                     "ORDER BY b.bid_id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (conn == null) return list;

            stmt.setInt(1, freelancerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Bid bid = new Bid();
                    bid.setBidId(rs.getInt("bid_id"));
                    bid.setProjectId(rs.getInt("project_id"));
                    bid.setProjectTitle(rs.getString("project_title"));
                    bid.setFreelancerId(freelancerId);
                    bid.setBidAmount(rs.getDouble("bid_amount"));
                    list.add(bid);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}