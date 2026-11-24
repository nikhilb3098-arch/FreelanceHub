package ui;

import dao.*;
import model.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ClientDashboard extends JFrame {
    private final User currentUser;
    private final ProjectDAO projectDAO = new ProjectDAO();
    private final BidDAO bidDAO = new BidDAO();
    private final PaymentDAO paymentDAO = new PaymentDAO();
    private final ReviewDAO reviewDAO = new ReviewDAO();

    private CardLayout cardLayout = new CardLayout();
    private JPanel contentArea = new JPanel(cardLayout);

    private JTextField titleField, budgetField;
    private JTextArea descArea;
    private JTable projectTable;
    private DefaultTableModel tableModel;
    private int selectedProjectId = -1;

    private JTable bidsTable;
    private DefaultTableModel bidsTableModel;

    private JTextField payProjectIdField, payAmountField;
    private JComboBox<String> payStatusBox;
    private JTable paymentsTable;
    private DefaultTableModel paymentsTableModel;

    private JTextField revFreelancerIdField;
    private JComboBox<Integer> revRatingBox;
    private JTextArea revCommentArea;
    private JTable reviewsTable;
    private DefaultTableModel reviewsTableModel;

    public ClientDashboard(User user) {
        this.currentUser = user;
        setTitle("FreelanceHub — Client Studio");
        setSize(1120, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ==================== LEFT SIDEBAR (SPOTIFY STYLE) ====================
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(12, 12, 12));
        sidebar.setPreferredSize(new Dimension(230, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(22, 18, 22, 18));

        JLabel logo = new JLabel("FREELANCEHUB");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        logo.setForeground(UITheme.ACCENT_GREEN);
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel roleBadge = new JLabel("CLIENT WORKSPACE");
        roleBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        roleBadge.setForeground(UITheme.TEXT_MUTED);
        roleBadge.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton navProjects = createNavButton("  My Projects");
        JButton navBids = createNavButton("  Bids Received");
        JButton navPayments = createNavButton("  Payments (ACID)");
        JButton navReviews = createNavButton("  Rate Freelancers");

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
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(roleBadge);
        sidebar.add(Box.createVerticalStrut(30));
        sidebar.add(navProjects);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(navBids);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(navPayments);
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

        contentArea.add(buildProjectsView(), "PROJECTS");
        contentArea.add(buildBidsView(), "BIDS");
        contentArea.add(buildPaymentsView(), "PAYMENTS");
        contentArea.add(buildReviewsView(), "REVIEWS");

        add(contentArea, BorderLayout.CENTER);

        navProjects.addActionListener(e -> { loadClientProjects(); cardLayout.show(contentArea, "PROJECTS"); });
        navBids.addActionListener(e -> { loadReceivedBids(); cardLayout.show(contentArea, "BIDS"); });
        navPayments.addActionListener(e -> { loadPayments(); cardLayout.show(contentArea, "PAYMENTS"); });
        navReviews.addActionListener(e -> { loadReviews(); cardLayout.show(contentArea, "REVIEWS"); });

        loadClientProjects();
        loadReceivedBids();
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

    private JPanel buildProjectsView() {
        JPanel panel = new JPanel(new BorderLayout(16, 16));
        JLabel heading = new JLabel("Manage Posted Projects");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 22));
        panel.add(heading, BorderLayout.NORTH);

        JPanel formCard = UITheme.createCardPanel(null);
        formCard.setPreferredSize(new Dimension(320, 0));
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));

        titleField = new JTextField();
        titleField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        budgetField = new JTextField();
        budgetField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        descArea = new JTextArea(5, 20);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);

        JButton addBtn = UITheme.primaryButton("Publish Project");
        JButton updateBtn = UITheme.secondaryButton("Update");
        JButton deleteBtn = UITheme.dangerButton("Delete");
        JButton clearBtn = UITheme.secondaryButton("Clear");

        JPanel btnGrid = new JPanel(new GridLayout(2, 2, 8, 8));
        btnGrid.setBackground(UITheme.BG_CARD);
        btnGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 84));
        btnGrid.add(addBtn);
        btnGrid.add(updateBtn);
        btnGrid.add(deleteBtn);
        btnGrid.add(clearBtn);

        formCard.add(new JLabel("Project Title"));
        formCard.add(Box.createVerticalStrut(4));
        formCard.add(titleField);
        formCard.add(Box.createVerticalStrut(12));
        formCard.add(new JLabel("Budget ($)"));
        formCard.add(Box.createVerticalStrut(4));
        formCard.add(budgetField);
        formCard.add(Box.createVerticalStrut(12));
        formCard.add(new JLabel("Description"));
        formCard.add(Box.createVerticalStrut(4));
        formCard.add(new JScrollPane(descArea));
        formCard.add(Box.createVerticalStrut(14));
        formCard.add(btnGrid);

        tableModel = new DefaultTableModel(new String[]{"ID", "Title", "Description", "Budget ($)"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        projectTable = new JTable(tableModel);
        UITheme.styleTable(projectTable);

        projectTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && projectTable.getSelectedRow() != -1) {
                int r = projectTable.getSelectedRow();
                selectedProjectId = (int) tableModel.getValueAt(r, 0);
                titleField.setText(tableModel.getValueAt(r, 1).toString());
                descArea.setText(tableModel.getValueAt(r, 2).toString());
                budgetField.setText(tableModel.getValueAt(r, 3).toString());
            }
        });

        addBtn.addActionListener(e -> handleAdd());
        updateBtn.addActionListener(e -> handleUpdate());
        deleteBtn.addActionListener(e -> handleDelete());
        clearBtn.addActionListener(e -> clearForm());

        panel.add(formCard, BorderLayout.WEST);
        panel.add(new JScrollPane(projectTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildBidsView() {
        JPanel panel = new JPanel(new BorderLayout(16, 16));
        JPanel top = new JPanel(new BorderLayout());
        JLabel heading = new JLabel("Incoming Freelancer Bids (3-Table SQL JOIN)");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 22));
        JButton payBidBtn = UITheme.primaryButton("Accept & Proceed to Payment →");
        top.add(heading, BorderLayout.WEST);
        top.add(payBidBtn, BorderLayout.EAST);
        panel.add(top, BorderLayout.NORTH);

        bidsTableModel = new DefaultTableModel(
                new String[]{"Bid ID", "Project ID", "Project Title", "Freelancer ID", "Freelancer", "Email", "Bid ($)"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        bidsTable = new JTable(bidsTableModel);
        UITheme.styleTable(bidsTable);

        payBidBtn.addActionListener(e -> {
            int row = bidsTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a bid row first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            payProjectIdField.setText(bidsTableModel.getValueAt(row, 1).toString());
            payAmountField.setText(bidsTableModel.getValueAt(row, 6).toString());
            revFreelancerIdField.setText(bidsTableModel.getValueAt(row, 3).toString());
            cardLayout.show(contentArea, "PAYMENTS");
        });

        panel.add(new JScrollPane(bidsTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildPaymentsView() {
        JPanel panel = new JPanel(new BorderLayout(16, 16));
        JLabel heading = new JLabel("Escrow & Payments (ACID Transaction)");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 22));
        panel.add(heading, BorderLayout.NORTH);

        JPanel card = UITheme.createCardPanel(null);
        card.setPreferredSize(new Dimension(320, 0));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        payProjectIdField = new JTextField();
        payProjectIdField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        payAmountField = new JTextField();
        payAmountField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        payStatusBox = new JComboBox<>(new String[]{"COMPLETED", "PENDING"});
        payStatusBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        JButton submitPay = UITheme.primaryButton("Commit Payment");
        submitPay.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        card.add(new JLabel("Project ID"));
        card.add(Box.createVerticalStrut(4));
        card.add(payProjectIdField);
        card.add(Box.createVerticalStrut(12));
        card.add(new JLabel("Payment Amount ($)"));
        card.add(Box.createVerticalStrut(4));
        card.add(payAmountField);
        card.add(Box.createVerticalStrut(12));
        card.add(new JLabel("Payment Status"));
        card.add(Box.createVerticalStrut(4));
        card.add(payStatusBox);
        card.add(Box.createVerticalStrut(20));
        card.add(submitPay);

        paymentsTableModel = new DefaultTableModel(new String[]{"Payment ID", "Project ID", "Project Title", "Amount ($)", "Status"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        paymentsTable = new JTable(paymentsTableModel);
        UITheme.styleTable(paymentsTable);

        submitPay.addActionListener(e -> handlePayment());

        panel.add(card, BorderLayout.WEST);
        panel.add(new JScrollPane(paymentsTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildReviewsView() {
        JPanel panel = new JPanel(new BorderLayout(16, 16));
        JLabel heading = new JLabel("Freelancer Ratings & Feedback");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 22));
        panel.add(heading, BorderLayout.NORTH);

        JPanel card = UITheme.createCardPanel(null);
        card.setPreferredSize(new Dimension(320, 0));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        revFreelancerIdField = new JTextField();
        revFreelancerIdField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        revRatingBox = new JComboBox<>(new Integer[]{5, 4, 3, 2, 1});
        revRatingBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        revCommentArea = new JTextArea(4, 20);
        revCommentArea.setLineWrap(true);
        revCommentArea.setWrapStyleWord(true);
        JButton submitRev = UITheme.primaryButton("Publish Review");
        submitRev.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        card.add(new JLabel("Freelancer ID"));
        card.add(Box.createVerticalStrut(4));
        card.add(revFreelancerIdField);
        card.add(Box.createVerticalStrut(12));
        card.add(new JLabel("Rating (1 - 5 Stars)"));
        card.add(Box.createVerticalStrut(4));
        card.add(revRatingBox);
        card.add(Box.createVerticalStrut(12));
        card.add(new JLabel("Feedback Comment"));
        card.add(Box.createVerticalStrut(4));
        card.add(new JScrollPane(revCommentArea));
        card.add(Box.createVerticalStrut(16));
        card.add(submitRev);

        reviewsTableModel = new DefaultTableModel(new String[]{"ID", "Freelancer ID", "Freelancer", "Rating", "Comment"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        reviewsTable = new JTable(reviewsTableModel);
        UITheme.styleTable(reviewsTable);

        submitRev.addActionListener(e -> handleReview());

        panel.add(card, BorderLayout.WEST);
        panel.add(new JScrollPane(reviewsTable), BorderLayout.CENTER);
        return panel;
    }

    private void loadClientProjects() {
        tableModel.setRowCount(0);
        for (Project p : projectDAO.getProjectsByClient(currentUser.getUserId())) {
            tableModel.addRow(new Object[]{p.getProjectId(), p.getTitle(), p.getDescription(), p.getBudget()});
        }
    }

    private void loadReceivedBids() {
        bidsTableModel.setRowCount(0);
        for (Bid b : bidDAO.getBidsForClient(currentUser.getUserId())) {
            bidsTableModel.addRow(new Object[]{
                b.getBidId(), b.getProjectId(), b.getProjectTitle(),
                b.getFreelancerId(), b.getFreelancerName(), b.getFreelancerEmail(), b.getBidAmount()
            });
        }
    }

    private void loadPayments() {
        paymentsTableModel.setRowCount(0);
        for (Payment pay : paymentDAO.getPaymentsByClient(currentUser.getUserId())) {
            paymentsTableModel.addRow(new Object[]{
                pay.getPaymentId(), pay.getProjectId(), pay.getProjectTitle(), pay.getAmount(), pay.getStatus()
            });
        }
    }

    private void loadReviews() {
        reviewsTableModel.setRowCount(0);
        for (Review r : reviewDAO.getAllReviews()) {
            reviewsTableModel.addRow(new Object[]{
                r.getReviewId(), r.getFreelancerId(), r.getFreelancerName(), r.getRating() + " ★", r.getComment()
            });
        }
    }

    private void handleAdd() {
        try {
            double budget = Double.parseDouble(budgetField.getText().trim());
            if (titleField.getText().trim().isEmpty() || descArea.getText().trim().isEmpty() || budget <= 0) {
                JOptionPane.showMessageDialog(this, "Enter valid title, description, and positive budget.");
                return;
            }
            if (projectDAO.addProject(new Project(currentUser.getUserId(), titleField.getText().trim(), descArea.getText().trim(), budget))) {
                clearForm();
                loadClientProjects();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid budget number.");
        }
    }

    private void handleUpdate() {
        if (selectedProjectId == -1) return;
        try {
            double budget = Double.parseDouble(budgetField.getText().trim());
            if (projectDAO.updateProject(new Project(selectedProjectId, currentUser.getUserId(), titleField.getText().trim(), descArea.getText().trim(), budget))) {
                clearForm();
                loadClientProjects();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid budget number.");
        }
    }

    private void handleDelete() {
        if (selectedProjectId == -1) return;
        if (projectDAO.deleteProject(selectedProjectId, currentUser.getUserId())) {
            clearForm();
            loadClientProjects();
        } else {
            JOptionPane.showMessageDialog(this, "Cannot delete project with active bids or payments.");
        }
    }

    private void handlePayment() {
        try {
            int projId = Integer.parseInt(payProjectIdField.getText().trim());
            double amount = Double.parseDouble(payAmountField.getText().trim());
            String res = paymentDAO.processPayment(projId, amount, (String) payStatusBox.getSelectedItem());
            if ("SUCCESS".equals(res)) {
                JOptionPane.showMessageDialog(this, "Payment committed via JDBC Transaction!");
                loadPayments();
            } else {
                JOptionPane.showMessageDialog(this, res, "Transaction Rolled Back", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter valid Project ID and Amount.");
        }
    }

    private void handleReview() {
        try {
            int fId = Integer.parseInt(revFreelancerIdField.getText().trim());
            int rating = (Integer) revRatingBox.getSelectedItem();
            String comment = revCommentArea.getText().trim();
            if (!comment.isEmpty() && reviewDAO.addReview(fId, rating, comment)) {
                JOptionPane.showMessageDialog(this, "Review published!");
                revCommentArea.setText("");
                loadReviews();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter valid numeric Freelancer ID.");
        }
    }

    private void clearForm() {
        selectedProjectId = -1;
        titleField.setText("");
        budgetField.setText("");
        descArea.setText("");
        projectTable.clearSelection();
    }
}