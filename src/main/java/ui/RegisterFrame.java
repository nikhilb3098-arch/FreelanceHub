package ui;

import dao.UserDAO;
import model.User;

import javax.swing.*;
import java.awt.*;

public class RegisterFrame extends JFrame {
    private JTextField nameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JComboBox<String> roleBox;
    private final UserDAO userDAO;

    public RegisterFrame() {
        userDAO = new UserDAO();
        setTitle("FreelanceHub — Create Account");
        setSize(460, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(UITheme.BG_DARK);
        setLayout(new GridBagLayout());

        JPanel card = UITheme.createCardPanel(null);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(370, 430));

        JLabel title = new JLabel("Join FreelanceHub");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(UITheme.ACCENT_GREEN);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Create a Client or Freelancer workspace");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(UITheme.TEXT_MUTED);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        nameField = new JTextField();
        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        nameField.setAlignmentX(Component.LEFT_ALIGNMENT);

        emailField = new JTextField();
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        emailField.setAlignmentX(Component.LEFT_ALIGNMENT);

        passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);

        roleBox = new JComboBox<>(new String[]{"CLIENT", "FREELANCER"});
        roleBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        roleBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton registerBtn = UITheme.primaryButton("Register Account");
        registerBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        registerBtn.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton backBtn = UITheme.secondaryButton("Back to Sign In");
        backBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        backBtn.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(title);
        card.add(Box.createVerticalStrut(4));
        card.add(sub);
        card.add(Box.createVerticalStrut(18));
        card.add(makeLabel("Full Name"));
        card.add(Box.createVerticalStrut(4));
        card.add(nameField);
        card.add(Box.createVerticalStrut(10));
        card.add(makeLabel("Email Address"));
        card.add(Box.createVerticalStrut(4));
        card.add(emailField);
        card.add(Box.createVerticalStrut(10));
        card.add(makeLabel("Password"));
        card.add(Box.createVerticalStrut(4));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(10));
        card.add(makeLabel("Account Role"));
        card.add(Box.createVerticalStrut(4));
        card.add(roleBox);
        card.add(Box.createVerticalStrut(18));
        card.add(registerBtn);
        card.add(Box.createVerticalStrut(8));
        card.add(backBtn);

        add(card);

        registerBtn.addActionListener(e -> handleRegister());
        backBtn.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
    }

    private JLabel makeLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private void handleRegister() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String role = (String) roleBox.getSelectedItem();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (userDAO.registerUser(new User(name, email, password, role))) {
            JOptionPane.showMessageDialog(this, "Account created! Please sign in.");
            new LoginFrame().setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Registration failed — email may already exist.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}