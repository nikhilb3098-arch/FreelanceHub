package ui;

import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class UITheme {
    // Spotify + Netflix Dark Palette
    public static final Color BG_DARK = new Color(18, 18, 18);         // #121212 Main background
    public static final Color BG_CARD = new Color(28, 28, 30);         // #1C1C1E Card / Sidebar
    public static final Color BG_INPUT = new Color(40, 40, 44);        // #28282C Input fields
    public static final Color ACCENT_GREEN = new Color(29, 185, 84);   // #1DB954 Spotify Green
    public static final Color ACCENT_RED = new Color(229, 9, 20);      // #E50914 Netflix Red
    public static final Color TEXT_PRIMARY = new Color(255, 255, 255); // Pure white
    public static final Color TEXT_MUTED = new Color(179, 179, 179);   // #B3B3B3 Secondary text
    public static final Color BORDER_SUBTLE = new Color(55, 55, 60);

    public static void setup() {
        try {
            FlatDarkLaf.setup();
            UIManager.put("Panel.background", BG_DARK);
            UIManager.put("OptionPane.background", BG_CARD);
            UIManager.put("OptionPane.messageForeground", TEXT_PRIMARY);
            UIManager.put("Label.foreground", TEXT_PRIMARY);
            UIManager.put("TextField.background", BG_INPUT);
            UIManager.put("TextField.foreground", TEXT_PRIMARY);
            UIManager.put("PasswordField.background", BG_INPUT);
            UIManager.put("PasswordField.foreground", TEXT_PRIMARY);
            UIManager.put("TextArea.background", BG_INPUT);
            UIManager.put("TextArea.foreground", TEXT_PRIMARY);
            UIManager.put("ComboBox.background", BG_INPUT);
            UIManager.put("ComboBox.foreground", TEXT_PRIMARY);
            UIManager.put("TabbedPane.background", BG_DARK);
            UIManager.put("TabbedPane.foreground", TEXT_PRIMARY);
            UIManager.put("TabbedPane.selectedBackground", BG_CARD);
            UIManager.put("TabbedPane.underlineColor", ACCENT_GREEN);
            UIManager.put("Button.arc", 14);
            UIManager.put("Component.arc", 10);
            UIManager.put("TextComponent.arc", 10);
            UIManager.put("Table.background", BG_CARD);
            UIManager.put("Table.foreground", TEXT_PRIMARY);
            UIManager.put("Table.selectionBackground", new Color(29, 185, 84, 70));
            UIManager.put("Table.selectionForeground", TEXT_PRIMARY);
            UIManager.put("Table.gridColor", BORDER_SUBTLE);
            UIManager.put("TableHeader.background", new Color(24, 24, 26));
            UIManager.put("TableHeader.foreground", TEXT_MUTED);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static JButton primaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(ACCENT_GREEN);
        btn.setForeground(Color.BLACK);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 18, 10, 18));
        return btn;
    }

    public static JButton dangerButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(ACCENT_RED);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 18, 10, 18));
        return btn;
    }

    public static JButton secondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(BG_INPUT);
        btn.setForeground(TEXT_PRIMARY);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 18, 10, 18));
        return btn;
    }

    public static void styleTable(JTable table) {
        table.setRowHeight(34);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setPreferredSize(new Dimension(0, 38));

        DefaultTableCellRenderer padRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean isSel, boolean hasFocus, int r, int c) {
                super.getTableCellRendererComponent(t, val, isSel, hasFocus, r, c);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                if (!isSel) {
                    setBackground(r % 2 == 0 ? BG_CARD : new Color(33, 33, 36));
                }
                return this;
            }
        };
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(padRenderer);
        }
    }

    public static JPanel createCardPanel(String title) {
        JPanel card = new JPanel();
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_SUBTLE, 1, true),
                new EmptyBorder(16, 16, 16, 16)
        ));
        if (title != null && !title.isEmpty()) {
            JLabel lbl = new JLabel(title);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
            lbl.setForeground(TEXT_PRIMARY);
        }
        return card;
    }
}