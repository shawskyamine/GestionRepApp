package presentation.ui.utils;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import presentation.ui.components.RedButton;

public class UITheme {
    // ===== ENTERPRISE PROFESSIONAL COLOR PALETTE =====
    // Primary Brand Colors - Sophisticated Burgundy/Wine
    public static final Color PRIMARY = new Color(185, 28, 28); // Red-700 - Professional burgundy
    public static final Color PRIMARY_DARK = new Color(153, 27, 27); // Red-800
    public static final Color PRIMARY_LIGHT = new Color(220, 38, 38); // Red-600
    public static final Color PRIMARY_ACCENT = new Color(239, 68, 68); // Red-500

    // Secondary Colors - Professional Teal
    public static final Color SECONDARY = new Color(15, 118, 110); // Teal-700
    public static final Color SECONDARY_DARK = new Color(13, 94, 88); // Teal-800
    public static final Color SECONDARY_LIGHT = new Color(20, 184, 166); // Teal-500

    // Semantic Colors - Refined and Professional
    public static final Color SUCCESS = new Color(22, 163, 74); // Green-700
    public static final Color WARNING = new Color(217, 119, 6); // Amber-700
    public static final Color ERROR = new Color(220, 38, 38); // Red-600
    public static final Color INFO = new Color(29, 78, 216); // Blue-700

    // Background System - Professional Charcoal
    public static final Color BG_PRIMARY = new Color(17, 24, 39); // Gray-900
    public static final Color BG_SECONDARY = new Color(31, 41, 55); // Gray-800
    public static final Color BG_TERTIARY = new Color(55, 65, 81); // Gray-700
    public static final Color BG_OVERLAY = new Color(17, 24, 39, 250);

    // Surface Colors - Professional Surfaces
    public static final Color SURFACE = new Color(37, 47, 63); // Gray-750
    public static final Color SURFACE_ELEVATED = new Color(55, 65, 81); // Gray-700
    public static final Color SURFACE_HOVER = new Color(75, 85, 99); // Gray-600

    // Sidebar Colors
    public static final Color SIDEBAR_BG = new Color(17, 24, 39); // Gray-900
    public static final Color SIDEBAR_HOVER = new Color(55, 65, 81); // Gray-700
    public static final Color SIDEBAR_ACTIVE = PRIMARY;
    public static final Color SIDEBAR_TEXT = new Color(240, 240, 240); // Very light gray for better readability
    public static final Color SIDEBAR_TEXT_ACTIVE = Color.WHITE;

    // Text Colors - High Contrast for Readability
    public static final Color TEXT_PRIMARY = Color.WHITE; // Pure white for maximum contrast
    public static final Color TEXT_SECONDARY = new Color(240, 240, 240); // Very light gray, almost white
    public static final Color TEXT_TERTIARY = new Color(200, 200, 200); // Light gray, still very readable
    public static final Color TEXT_WHITE = Color.WHITE;
    public static final Color TEXT_MUTED = new Color(180, 180, 180); // Light gray for muted text

    // Border System - Subtle and Professional
    public static final Color BORDER_LIGHT = new Color(75, 85, 99); // Gray-600
    public static final Color BORDER_MEDIUM = new Color(55, 65, 81); // Gray-700
    public static final Color BORDER_DARK = new Color(31, 41, 55); // Gray-800
    public static final Color BORDER_FOCUS = PRIMARY;

    // Shadow Colors - Subtle shadows for dark theme
    public static final Color SHADOW_LIGHT = new Color(0, 0, 0, 20);
    public static final Color SHADOW_MEDIUM = new Color(0, 0, 0, 40);
    public static final Color SHADOW_HEAVY = new Color(0, 0, 0, 60);

    // ===== LEGACY COMPATIBILITY =====
    public static final Color BACKGROUND = BG_SECONDARY;
    public static final Color CARD_BG = SURFACE;
    public static final Color HOVER_BG = SURFACE_HOVER;
    public static final Color TEXT_DARK = TEXT_PRIMARY;
    public static final Color TEXT_LIGHT = TEXT_TERTIARY;
    public static final Color BORDER_COLOR = BORDER_LIGHT;

    public static void applyTheme() {
        try {
            // Set modern look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            // ===== GLOBAL SETTINGS =====
            UIManager.put("Panel.background", BG_SECONDARY);
            UIManager.put("Panel.foreground", TEXT_PRIMARY);

            // ===== BUTTON STYLING =====
            UIManager.put("Button.background", PRIMARY);
            UIManager.put("Button.foreground", TEXT_WHITE);
            UIManager.put("Button.font", getButtonFont());
            UIManager.put("Button.select", PRIMARY_DARK);
            UIManager.put("Button.focus", new ColorUIResource(new Color(0, 0, 0, 0)));
            UIManager.put("Button.border", BorderFactory.createEmptyBorder(10, 20, 10, 20));
            UIManager.put("Button.margin", new Insets(6, 12, 6, 12));

            // ===== TEXT FIELD STYLING =====
            UIManager.put("TextField.font", getBodyFont());
            UIManager.put("TextField.background", SURFACE);
            UIManager.put("TextField.foreground", TEXT_PRIMARY);
            UIManager.put("TextField.caretForeground", PRIMARY);
            UIManager.put("TextField.selectionBackground", withAlpha(PRIMARY, 35));
            UIManager.put("TextField.selectionForeground", TEXT_PRIMARY);
            UIManager.put("TextField.border", createModernBorder(BORDER_MEDIUM, 8));
            UIManager.put("TextField.margin", new Insets(8, 12, 8, 12));

            // ===== PASSWORD FIELD STYLING =====
            UIManager.put("PasswordField.font", getBodyFont());
            UIManager.put("PasswordField.background", SURFACE);
            UIManager.put("PasswordField.foreground", TEXT_PRIMARY);
            UIManager.put("PasswordField.caretForeground", PRIMARY);
            UIManager.put("PasswordField.selectionBackground", withAlpha(PRIMARY, 35));
            UIManager.put("PasswordField.selectionForeground", TEXT_PRIMARY);
            UIManager.put("PasswordField.border", createModernBorder(BORDER_MEDIUM, 8));
            UIManager.put("PasswordField.margin", new Insets(8, 12, 8, 12));

            // ===== LABEL STYLING =====
            UIManager.put("Label.font", getBodyFont());
            UIManager.put("Label.foreground", TEXT_PRIMARY);
            UIManager.put("Label.background", new Color(0, 0, 0, 0));

            // ===== COMBOBOX STYLING =====
            UIManager.put("ComboBox.font", getBodyFont());
            UIManager.put("ComboBox.background", SURFACE);
            UIManager.put("ComboBox.foreground", TEXT_PRIMARY);
            UIManager.put("ComboBox.selectionBackground", PRIMARY);
            UIManager.put("ComboBox.selectionForeground", TEXT_WHITE);
            UIManager.put("ComboBox.border", createModernBorder(BORDER_MEDIUM, 8));

            // ===== TABLE STYLING =====
            UIManager.put("Table.font", getBodyFont());
            UIManager.put("Table.foreground", TEXT_PRIMARY);
            UIManager.put("Table.background", SURFACE);
            UIManager.put("Table.gridColor", BORDER_DARK);
            UIManager.put("Table.selectionBackground", withAlpha(PRIMARY, 25));
            UIManager.put("Table.selectionForeground", TEXT_PRIMARY);
            UIManager.put("Table.focusCellHighlightBorder", BorderFactory.createEmptyBorder());
            UIManager.put("Table.rowHeight", 48);

            // ===== TABLE HEADER STYLING =====
            UIManager.put("TableHeader.font", getLabelFont());
            UIManager.put("TableHeader.background", BG_TERTIARY);
            UIManager.put("TableHeader.foreground", TEXT_SECONDARY);
            UIManager.put("TableHeader.border", BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 0, BORDER_MEDIUM),
                    BorderFactory.createEmptyBorder(12, 16, 12, 16)));

            // ===== SCROLLPANE STYLING =====
            UIManager.put("ScrollPane.border", BorderFactory.createEmptyBorder());
            UIManager.put("ScrollPane.background", BG_SECONDARY);
            UIManager.put("ScrollBar.thumb", BORDER_MEDIUM);
            UIManager.put("ScrollBar.track", BG_SECONDARY);
            UIManager.put("ScrollBar.width", Integer.valueOf(14));
            UIManager.put("ScrollBar.thumbHighlight", BORDER_LIGHT);
            UIManager.put("ScrollBar.thumbDarkShadow", BORDER_DARK);

            // ===== DIALOG STYLING =====
            UIManager.put("OptionPane.background", BG_SECONDARY);
            UIManager.put("OptionPane.messageForeground", Color.WHITE);
            UIManager.put("OptionPane.messageFont", getBodyFont());
            UIManager.put("OptionPane.buttonFont", getButtonFont());

            // Button styling in OptionPane
            UIManager.put("OptionPane.buttonAreaBorder", BorderFactory.createEmptyBorder(12, 12, 12, 12));
            UIManager.put("Button.defaultButtonFollowsFocus", Boolean.TRUE);

            // Style buttons in dialogs
            UIManager.put("OptionPane.buttonBackground", PRIMARY);
            UIManager.put("OptionPane.buttonForeground", Color.WHITE);
            UIManager.put("OptionPane.buttonBorder", BorderFactory.createEmptyBorder(10, 20, 10, 20));

            // Panel background in OptionPane
            UIManager.put("Panel.background", BG_SECONDARY);

            // ===== TITLED BORDER STYLING =====
            UIManager.put("TitledBorder.font", getSubtitleFont());
            UIManager.put("TitledBorder.titleColor", TEXT_PRIMARY);
            UIManager.put("TitledBorder.border", BorderFactory.createLineBorder(BORDER_MEDIUM));

            // ===== TOOLTIP STYLING =====
            UIManager.put("ToolTip.background", BG_OVERLAY);
            UIManager.put("ToolTip.foreground", TEXT_WHITE);
            UIManager.put("ToolTip.font", getSmallFont());
            UIManager.put("ToolTip.border", BorderFactory.createEmptyBorder(8, 12, 8, 12));

            // ===== ADDITIONAL COMPONENTS =====
            UIManager.put("CheckBox.font", getBodyFont());
            UIManager.put("CheckBox.foreground", TEXT_PRIMARY);
            UIManager.put("RadioButton.font", getBodyFont());
            UIManager.put("RadioButton.foreground", TEXT_PRIMARY);
            UIManager.put("ToggleButton.font", getButtonFont());
            UIManager.put("MenuBar.font", getBodyFont());
            UIManager.put("Menu.font", getBodyFont());
            UIManager.put("MenuItem.font", getBodyFont());

            // ===== CUSTOM PROPERTIES =====
            UIManager.put("TabbedPane.selectedTabPadInsets", new Insets(8, 16, 8, 16));
            UIManager.put("TabbedPane.tabInsets", new Insets(8, 16, 8, 16));
            UIManager.put("TabbedPane.font", getLabelFont());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===== ENTERPRISE FONT SYSTEM =====
    private static final String FONT_FAMILY = "Segoe UI, -apple-system, BlinkMacSystemFont, Roboto, Helvetica Neue, Arial, sans-serif";

    // Font Utilities - Modern Typography Scale
    public static Font getDisplayFont() {
        return new Font(FONT_FAMILY.split(",")[0].trim(), Font.BOLD, 36);
    }

    public static Font getTitleFont() {
        return new Font(FONT_FAMILY.split(",")[0].trim(), Font.BOLD, 28);
    }

    public static Font getSubtitleFont() {
        return new Font(FONT_FAMILY.split(",")[0].trim(), Font.BOLD, 20);
    }

    public static Font getHeadingFont() {
        return new Font(FONT_FAMILY.split(",")[0].trim(), Font.BOLD, 18);
    }

    public static Font getLabelFont() {
        return new Font(FONT_FAMILY.split(",")[0].trim(), Font.BOLD, 14);
    }

    public static Font getBodyFont() {
        return new Font(FONT_FAMILY.split(",")[0].trim(), Font.PLAIN, 14);
    }

    public static Font getButtonFont() {
        return new Font(FONT_FAMILY.split(",")[0].trim(), Font.BOLD, 14);
    }

    public static Font getSmallFont() {
        return new Font(FONT_FAMILY.split(",")[0].trim(), Font.PLAIN, 12);
    }

    public static Font getCaptionFont() {
        return new Font(FONT_FAMILY.split(",")[0].trim(), Font.PLAIN, 11);
    }

    // Color Utilities - Helpful Methods
    public static Color withAlpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

    public static Color lighten(Color color, float factor) {
        int r = Math.min(255, (int) (color.getRed() + (255 - color.getRed()) * factor));
        int g = Math.min(255, (int) (color.getGreen() + (255 - color.getGreen()) * factor));
        int b = Math.min(255, (int) (color.getBlue() + (255 - color.getBlue()) * factor));
        return new Color(r, g, b);
    }

    public static Color darken(Color color, float factor) {
        int r = Math.max(0, (int) (color.getRed() * (1 - factor)));
        int g = Math.max(0, (int) (color.getGreen() * (1 - factor)));
        int b = Math.max(0, (int) (color.getBlue() * (1 - factor)));
        return new Color(r, g, b);
    }

    // Helper method for creating custom rounded borders
    public static Border createRoundedBorder(Color color, int thickness, int radius) {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, thickness),
                BorderFactory.createEmptyBorder(radius, radius, radius, radius));
    }

    // Helper method for creating modern rounded borders (with default thickness)
    public static Border createModernBorder(Color color, int radius) {
        // Default thickness of 1 pixel for modern borders
        int thickness = 1;
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, thickness),
                BorderFactory.createEmptyBorder(radius, radius, radius, radius));
    }

    // Helper method for creating loading dialog
    public static JDialog createLoadingDialog(Component parent, String message) {
        JDialog loadingDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent), "Chargement...", true);
        loadingDialog.setLayout(new BorderLayout());
        loadingDialog.setSize(300, 100);
        loadingDialog.setLocationRelativeTo(parent);
        loadingDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        JPanel panel = new JPanel(new FlowLayout());
        panel.setBackground(BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setPreferredSize(new Dimension(200, 20));

        JLabel label = new JLabel(message);
        label.setFont(getBodyFont());
        label.setForeground(Color.WHITE);

        panel.add(label);
        panel.add(progressBar);

        loadingDialog.add(panel, BorderLayout.CENTER);
        return loadingDialog;
    }

    // Helper method for creating styled confirmation dialog
    public static int showStyledConfirmDialog(Component parent, String message, String title) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent), title, true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(450, 200);
        dialog.setLocationRelativeTo(parent);
        dialog.getContentPane().setBackground(BG_SECONDARY);

        // Message panel
        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setBackground(BG_SECONDARY);
        messagePanel.setBorder(BorderFactory.createEmptyBorder(28, 32, 20, 32));

        JLabel messageLabel = new JLabel(
                "<html><div style='text-align: center;'>" + message.replace("\n", "<br>") + "</div></html>");
        messageLabel.setFont(getBodyFont().deriveFont(15f));
        messageLabel.setForeground(Color.WHITE);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        messagePanel.add(messageLabel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 0));
        buttonPanel.setBackground(BG_SECONDARY);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(16, 32, 28, 32));

        final int[] result = { JOptionPane.NO_OPTION };

        RedButton yesButton = new RedButton("Oui");
        yesButton.setPreferredSize(new Dimension(120, 48));
        yesButton.addActionListener(e -> {
            result[0] = JOptionPane.YES_OPTION;
            dialog.dispose();
        });

        RedButton noButton = new RedButton("Non");
        noButton.setPreferredSize(new Dimension(120, 48));
        noButton.addActionListener(e -> {
            result[0] = JOptionPane.NO_OPTION;
            dialog.dispose();
        });

        buttonPanel.add(yesButton);
        buttonPanel.add(noButton);

        dialog.add(messagePanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);

        return result[0];
    }

    // Helper method for creating styled message dialog
    public static void showStyledMessageDialog(Component parent, String message, String title, int messageType) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent), title, true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(450, 200);
        dialog.setLocationRelativeTo(parent);
        dialog.getContentPane().setBackground(BG_SECONDARY);

        // Message panel with icon
        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setBackground(BG_SECONDARY);
        messagePanel.setBorder(BorderFactory.createEmptyBorder(28, 32, 20, 32));

        // Icon based on message type
        String iconText = "";
        Color iconColor = PRIMARY;
        if (messageType == JOptionPane.ERROR_MESSAGE) {
            iconText = "";
            iconColor = ERROR;
        } else if (messageType == JOptionPane.WARNING_MESSAGE) {
            iconText = "";
            iconColor = WARNING;
        } else if (messageType == JOptionPane.INFORMATION_MESSAGE) {
            iconText = "";
            iconColor = INFO;
        } else {
            iconText = "";
            iconColor = PRIMARY;
        }

        JLabel iconLabel = new JLabel(iconText);
        iconLabel.setFont(new Font(iconLabel.getFont().getName(), Font.PLAIN, 36));
        iconLabel.setForeground(iconColor);
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setPreferredSize(new Dimension(50, 50));

        JLabel messageLabel = new JLabel(
                "<html><div style='text-align: center;'>" + message.replace("\n", "<br>") + "</div></html>");
        messageLabel.setFont(getBodyFont().deriveFont(15f));
        messageLabel.setForeground(Color.WHITE);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel contentPanel = new JPanel(new BorderLayout(20, 0));
        contentPanel.setBackground(BG_SECONDARY);
        contentPanel.add(iconLabel, BorderLayout.WEST);
        contentPanel.add(messageLabel, BorderLayout.CENTER);

        messagePanel.add(contentPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        buttonPanel.setBackground(BG_SECONDARY);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(16, 32, 28, 32));

        RedButton okButton = new RedButton("OK");
        okButton.setPreferredSize(new Dimension(120, 48));
        okButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(okButton);

        dialog.add(messagePanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
}