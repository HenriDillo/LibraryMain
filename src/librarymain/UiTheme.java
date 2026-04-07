package librarymain;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.LayoutManager;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.JTableHeader;

public final class UiTheme {
    public static final Color BACKGROUND = new Color(245, 247, 250);
    public static final Color PANEL_BACKGROUND = Color.WHITE;
    public static final Color PRIMARY = new Color(37, 99, 235);
    public static final Color TEXT_PRIMARY = new Color(31, 41, 55);
    public static final Color TEXT_MUTED = new Color(107, 114, 128);
    public static final Color BORDER = new Color(209, 213, 219);

    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 13);

    private UiTheme() {
    }

    public static void installLookAndFeel() {
        UIManager.put("OptionPane.messageFont", FONT_BODY);
        UIManager.put("OptionPane.buttonFont", FONT_BUTTON);
    }

    public static JPanel createPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(PANEL_BACKGROUND);
        return panel;
    }

    public static JPanel createPanel(BorderLayout layout) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(PANEL_BACKGROUND);
        return panel;
    }

    public static JPanel createPanel(LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(PANEL_BACKGROUND);
        return panel;
    }

    public static Border cardBorder() {
        return BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            BorderFactory.createEmptyBorder(16, 16, 16, 16)
        );
    }

    public static void styleInput(JTextField field) {
        field.setFont(FONT_BODY);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, 34));
    }

    public static void styleButton(JButton button, boolean primary) {
        button.setFont(FONT_BUTTON);
        button.setFocusPainted(false);
        button.setMargin(new Insets(8, 16, 8, 16));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(primary ? PRIMARY : BORDER),
            BorderFactory.createEmptyBorder(5, 6, 5, 6)
        ));
        button.setBackground(primary ? PRIMARY : Color.WHITE);
        button.setForeground(primary ? Color.WHITE : TEXT_PRIMARY);
    }

    public static void styleTable(JTable table, JScrollPane scrollPane) {
        table.setFont(FONT_BODY);
        table.setRowHeight(28);
        table.setGridColor(new Color(229, 231, 235));
        table.setSelectionBackground(new Color(219, 234, 254));
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);

        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_BUTTON);
        header.setBackground(new Color(243, 244, 246));
        header.setForeground(TEXT_PRIMARY);

        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER));
        scrollPane.getViewport().setBackground(Color.WHITE);
    }

    public static void styleContainer(Component component) {
        if (component instanceof JComponent) {
            ((JComponent) component).setFont(FONT_BODY);
            ((JComponent) component).setForeground(TEXT_PRIMARY);
        }
    }
}
