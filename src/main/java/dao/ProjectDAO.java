package dao;

import database.DBConnection;
import model.Project;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProjectDAO {

    // CREATE
    public boolean addProject(Project project) {
        String sql = "INSERT INTO projects (client_id, title, description, budget) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (conn == null) return false;

            stmt.setInt(1, project.getClientId());
            stmt.setString(2, project.getTitle());
            stmt.setString(3, project.getDescription());
            stmt.setDouble(4, project.getBudget());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // READ (Client's own projects)
    public List<Project> getProjectsByClient(int clientId) {
        List<Project> list = new ArrayList<>();
        String sql = "SELECT * FROM projects WHERE client_id = ? ORDER BY project_id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (conn == null) return list;

            stmt.setInt(1, clientId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new Project(
                        rs.getInt("project_id"),
                        rs.getInt("client_id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getDouble("budget")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // READ ALL (With JOIN on users table so Freelancers see Client names)
    public List<Project> getAllProjects() {
        List<Project> list = new ArrayList<>();
        String sql = "SELECT p.project_id, p.client_id, u.name AS client_name, p.title, p.description, p.budget " +
                     "FROM projects p JOIN users u ON p.client_id = u.user_id ORDER BY p.project_id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Project p = new Project(
                    rs.getInt("project_id"),
                    rs.getInt("client_id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getDouble("budget")
                );
                p.setClientName(rs.getString("client_name"));
                list.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // UPDATE
    public boolean updateProject(Project project) {
        String sql = "UPDATE projects SET title = ?, description = ?, budget = ? WHERE project_id = ? AND client_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (conn == null) return false;

            stmt.setString(1, project.getTitle());
            stmt.setString(2, project.getDescription());
            stmt.setDouble(3, project.getBudget());
            stmt.setInt(4, project.getProjectId());
            stmt.setInt(5, project.getClientId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // DELETE
    public boolean deleteProject(int projectId, int clientId) {
        String sql = "DELETE FROM projects WHERE project_id = ? AND client_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (conn == null) return false;

            stmt.setInt(1, projectId);
            stmt.setInt(2, clientId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}