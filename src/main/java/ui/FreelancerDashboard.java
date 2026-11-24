package ui;

import dao.*;
import model.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class FreelancerDashboard extends JFrame {
    private final User currentUser;
    private final ProjectDAO projectDAO = new ProjectDAO();
    private final BidDAO bidDAO = new BidDAO();
    private final PaymentDAO paymentDAO = new PaymentDAO();
    private final ReviewDAO reviewDAO = new ReviewDAO();

    private CardLayout cardLayout = new CardLayout();
    private JPanel contentArea = new JPanel(cardLayout);

    private JTable availableProjectsTable;
    private DefaultTableModel projectsModel;
    private JTextArea projectDetailsArea;
    private JTextField bidAmountField;
    private JLabel selectedProjectLabel;
    private int selectedProjectId = -1;

    private JTable myBidsTable;
    private DefaultTableModel myBidsModel;

    private JTable paymentsTable;
    private DefaultTableModel paymentsModel;

    private JTable reviewsTable;
    private DefaultTableModel reviewsModel;
    private JLabel ratingBadgeLabel;

    public FreelancerDashboard(User user) {
        this.currentUser = user;
        setTitle("FreelanceHub — Freelancer Studio");
        setSize(1120, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ==================== LEFT SIDEBAR ====================
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(12, 12, 12));
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(22, 18, 22, 18));

        JLabel logo = new JLabel("FREELANCEHUB");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        logo.setForeground(UITheme.ACCENT_GREEN);
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);

        ratingBadgeLabel = new JLabel(reviewDAO.getFreelancerRatingSummary(currentUser.getUserId()));
        ratingBadgeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        ratingBadgeLabel.setForeground(new Color(255, 215, 0));
        ratingBadgeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton navExplore = createNavButton("  Explore Projects");
        JButton navBids = createNavButton("  My Bids");
        JButton navEarnings = createNavButton("  Project Payments");
        JButton navReviews = createNavButton("  My Ratings");

        JLabel userInfo = new JLabel("Signed in: " + currentUser.getName() + " (#" + currentUser.getUserId() + ")");
        userInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userInfo.setForeground(UITheme.TEXT_MUTED);
        userInfo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton logoutBtn = UITheme.dangerButton("Sign Out");
        logoutBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        logoutBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoutBtn.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        sidebar.add(logo);
        sidebar.add(Box.createVerticalStrut(6));
        sidebar.add(ratingBadgeLabel);
        sidebar.add(Box.createVerticalStrut(28));
        sidebar.add(navExplore);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(navBids);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(navEarnings);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(navReviews);
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(userInfo);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(logoutBtn);

        add(sidebar, BorderLayout.WEST);

        // ==================== CONTENT VIEWS ====================
        contentArea.setBackground(UITheme.BG_DARK);
        contentArea.setBorder(new EmptyBorder(20, 20, 20, 20));

        contentArea.add(buildExploreView(), "EXPLORE");
        contentArea.add(buildMyBidsView(), "BIDS");
        contentArea.add(buildPaymentsView(), "PAYMENTS");
        contentArea.add(buildReviewsView(), "REVIEWS");

        add(contentArea, BorderLayout.CENTER);

        navExplore.addActionListener(e -> { loadAvailableProjects(); cardLayout.show(contentArea, "EXPLORE"); });
        navBids.addActionListener(e -> { loadMyBids(); cardLayout.show(contentArea, "BIDS"); });
        navEarnings.addActionListener(e -> { loadPayments(); cardLayout.show(contentArea, "PAYMENTS"); });
        navReviews.addActionListener(e -> { loadReviews(); cardLayout.show(contentArea, "REVIEWS"); });

        loadAvailableProjects();
        loadMyBids();
        loadPayments();
        loadReviews();
    }

    private JButton createNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(UITheme.TEXT_PRIMARY);
        btn.setBackground(UITheme.BG_CARD);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        return btn;
    }

    private JPanel buildExploreView() {
        JPanel panel = new JPanel(new BorderLayout(16, 16));
        JLabel heading = new JLabel("Explore Open Projects");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 22));
        panel.add(heading, BorderLayout.NORTH);

        projectsModel = new DefaultTableModel(new String[]{"ID", "Project Title", "Client", "Budget ($)"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        availableProjectsTable = new JTable(projectsModel);
        UITheme.styleTable(availableProjectsTable);

        JPanel rightCard = UITheme.createCardPanel(null);
        rightCard.setPreferredSize(new Dimension(330, 0));
        rightCard.setLayout(new BoxLayout(rightCard, BoxLayout.Y_AXIS));

        selectedProjectLabel = new JLabel("Select a Project");
        selectedProjectLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        selectedProjectLabel.setForeground(UITheme.ACCENT_GREEN);
        selectedProjectLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        projectDetailsArea = new JTextArea(8, 20);
        projectDetailsArea.setEditable(false);
        projectDetailsArea.setLineWrap(true);
        projectDetailsArea.setWrapStyleWord(true);
        projectDetailsArea.setText("Click any project row on the left to inspect its scope and place a bid.");

        bidAmountField = new JTextField();
        bidAmountField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        JButton submitBidBtn = UITheme.primaryButton("Place Bid (Stored Procedure)");
        submitBidBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        rightCard.add(selectedProjectLabel);
        rightCard.add(Box.createVerticalStrut(12));
        rightCard.add(new JScrollPane(projectDetailsArea));
        rightCard.add(Box.createVerticalStrut(14));
        rightCard.add(new JLabel("Your Bid Amount ($)"));
        rightCard.add(Box.createVerticalStrut(6));
        rightCard.add(bidAmountField);
        rightCard.add(Box.createVerticalStrut(14));
        rightCard.add(submitBidBtn);

        availableProjectsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && availableProjectsTable.getSelectedRow() != -1) {
                int r = availableProjectsTable.getSelectedRow();
                selectedProjectId = (int) projectsModel.getValueAt(r, 0);
                selectedProjectLabel.setText("#" + selectedProjectId + " — " + projectsModel.getValueAt(r, 1));
                for (Project p : projectDAO.getAllProjects()) {
                    if (p.getProjectId() == selectedProjectId) {
                        projectDetailsArea.setText("Client: " + p.getClientName() + "\nClient Budget: $" + p.getBudget() + "\n\nDescription:\n" + p.getDescription());
                        break;
                    }
                }
            }
        });

        submitBidBtn.addActionListener(e -> handlePlaceBid());

        panel.add(new JScrollPane(availableProjectsTable), BorderLayout.CENTER);
        panel.add(rightCard, BorderLayout.EAST);
        return panel;
    }

    private JPanel buildMyBidsView() {
        JPanel panel = new JPanel(new BorderLayout(16, 16));
        JLabel heading = new JLabel("My Submitted Proposals");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 22));
        panel.add(heading, BorderLayout.NORTH);

        myBidsModel = new DefaultTableModel(new String[]{"Bid ID", "Project ID", "Project Title", "My Bid ($)"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        myBidsTable = new JTable(myBidsModel);
        UITheme.styleTable(myBidsTable);
        panel.add(new JScrollPane(myBidsTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildPaymentsView() {
        JPanel panel = new JPanel(new BorderLayout(16, 16));
        JLabel heading = new JLabel("Project Payments & Escrow Status");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 22));
        panel.add(heading, BorderLayout.NORTH);

        paymentsModel = new DefaultTableModel(new String[]{"Payment ID", "Project ID", "Project Title", "Amount ($)", "Status"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        paymentsTable = new JTable(paymentsModel);
        UITheme.styleTable(paymentsTable);
        panel.add(new JScrollPane(paymentsTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildReviewsView() {
        JPanel panel = new JPanel(new BorderLayout(16, 16));
        JLabel heading = new JLabel("Client Reviews & Reputation");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 22));
        panel.add(heading, BorderLayout.NORTH);

        reviewsModel = new DefaultTableModel(new String[]{"Review ID", "Freelancer ID", "Name", "Rating", "Comment"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        reviewsTable = new JTable(reviewsModel);
        UITheme.styleTable(reviewsTable);
        panel.add(new JScrollPane(reviewsTable), BorderLayout.CENTER);
        return panel;
    }

    private void loadAvailableProjects() {
        projectsModel.setRowCount(0);
        for (Project p : projectDAO.getAllProjects()) {
            projectsModel.addRow(new Object[]{p.getProjectId(), p.getTitle(), p.getClientName(), p.getBudget()});
        }
    }

    private void loadMyBids() {
        myBidsModel.setRowCount(0);
        for (Bid b : bidDAO.getBidsByFreelancer(currentUser.getUserId())) {
            myBidsModel.addRow(new Object[]{b.getBidId(), b.getProjectId(), b.getProjectTitle(), b.getBidAmount()});
        }
    }

    private void loadPayments() {
        paymentsModel.setRowCount(0);
        for (Payment p : paymentDAO.getPaymentsForFreelancer(currentUser.getUserId())) {
            paymentsModel.addRow(new Object[]{p.getPaymentId(), p.getProjectId(), p.getProjectTitle(), p.getAmount(), p.getStatus()});
        }
    }

    private void loadReviews() {
        reviewsModel.setRowCount(0);
        for (Review r : reviewDAO.getAllReviews()) {
            if (r.getFreelancerId() == currentUser.getUserId()) {
                reviewsModel.addRow(new Object[]{
                    r.getReviewId(), r.getFreelancerId(), r.getFreelancerName(), r.getRating() + " ★", r.getComment()
                });
            }
        }
        ratingBadgeLabel.setText(reviewDAO.getFreelancerRatingSummary(currentUser.getUserId()));
    }

    private void handlePlaceBid() {
        if (selectedProjectId == -1) {
            JOptionPane.showMessageDialog(this, "Select a project first.");
            return;
        }
        try {
            double amount = Double.parseDouble(bidAmountField.getText().trim());
            String res = bidDAO.placeBid(selectedProjectId, currentUser.getUserId(), amount);
            if ("SUCCESS".equals(res)) {
                JOptionPane.showMessageDialog(this, "Bid submitted via PlaceBid()!");
                bidAmountField.setText("");
                loadMyBids();
            } else {
                JOptionPane.showMessageDialog(this, res, "MySQL Trigger Blocked Bid", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter a valid numeric bid.");
        }
    }
}