package dao;

import database.DBConnection;
import model.Payment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAO {

    // ACID Transaction: Validates project & amount, inserts payment record, commits atomically
    public String processPayment(int projectId, double amount, String status) {
        String checkProjectSql = "SELECT project_id FROM projects WHERE project_id = ?";
        String insertPaymentSql = "INSERT INTO payments (project_id, amount, status) VALUES (?, ?, ?)";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            if (conn == null) return "Database connection failed.";

            // Start Transaction
            conn.setAutoCommit(false);

            // Step 1: Verify project exists
            try (PreparedStatement checkStmt = conn.prepareStatement(checkProjectSql)) {
                checkStmt.setInt(1, projectId);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return "Transaction Rolled Back: Project ID does not exist.";
                    }
                }
            }

            // Step 2: Validate positive payment amount
            if (amount <= 0) {
                conn.rollback();
                return "Transaction Rolled Back: Payment amount must be greater than 0.";
            }

            // Step 3: Insert payment record
            try (PreparedStatement insertStmt = conn.prepareStatement(insertPaymentSql)) {
                insertStmt.setInt(1, projectId);
                insertStmt.setDouble(2, amount);
                insertStmt.setString(3, status);
                insertStmt.executeUpdate();
            }

            // Commit Transaction
            conn.commit();
            return "SUCCESS";

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return "Transaction Rolled Back due to error: " + e.getMessage();
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // JOIN payments + projects for a specific client
    public List<Payment> getPaymentsByClient(int clientId) {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT pay.payment_id, pay.project_id, p.title AS project_title, pay.amount, pay.status " +
                     "FROM payments pay " +
                     "JOIN projects p ON pay.project_id = p.project_id " +
                     "WHERE p.client_id = ? " +
                     "ORDER BY pay.payment_id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (conn == null) return list;

            stmt.setInt(1, clientId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new Payment(
                        rs.getInt("payment_id"),
                        rs.getInt("project_id"),
                        rs.getString("project_title"),
                        rs.getDouble("amount"),
                        rs.getString("status")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // JOIN payments + projects + bids so a Freelancer sees payments on projects they bid on
    public List<Payment> getPaymentsForFreelancer(int freelancerId) {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT DISTINCT pay.payment_id, pay.project_id, p.title AS project_title, pay.amount, pay.status " +
                     "FROM payments pay " +
                     "JOIN projects p ON pay.project_id = p.project_id " +
                     "JOIN bids b ON p.project_id = b.project_id " +
                     "WHERE b.freelancer_id = ? " +
                     "ORDER BY pay.payment_id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (conn == null) return list;

            stmt.setInt(1, freelancerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new Payment(
                        rs.getInt("payment_id"),
                        rs.getInt("project_id"),
                        rs.getString("project_title"),
                        rs.getDouble("amount"),
                        rs.getString("status")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}