package main;

import ui.LoginFrame;
import ui.UITheme;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        UITheme.setup();
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}