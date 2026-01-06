package presentation.ui.components;

import presentation.ui.utils.UITheme;
import javax.swing.*;
import java.awt.*;

public class BoutiqueBadge extends JPanel {
    private String boutiqueName;
    private JLabel nameLabel;
    private JLabel contextLabel;

    public BoutiqueBadge(String boutiqueName) {
        this.boutiqueName = boutiqueName != null ? boutiqueName : "Aucune boutique";
        initComponents();
    }

    private void initComponents() {
        setOpaque(true);
        setBackground(UITheme.SURFACE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_LIGHT, 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));
        setPreferredSize(new Dimension(220, 55));
        setMaximumSize(new Dimension(250, 55));
        setLayout(new BorderLayout(10, 0));

        // Left icon panel
        JPanel iconPanel = createIconPanel();

        // Center text panel
        JPanel textPanel = createTextPanel();

        add(iconPanel, BorderLayout.WEST);
        add(textPanel, BorderLayout.CENTER);
    }

    private JPanel createIconPanel() {
        JPanel iconPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int size = 24; // Smaller icon
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;

                // Draw building/store icon
                g2.setColor(UITheme.PRIMARY);
                g2.setStroke(new BasicStroke(1.5f));

                // Building shape
                g2.drawRect(x, y, size, size);

                // Door
                g2.fillRect(x + size / 2 - 3, y + size / 2, 6, size / 2);

                // Windows
                g2.setColor(UITheme.PRIMARY_LIGHT);
                g2.fillRect(x + 6, y + 6, 4, 4);
                g2.fillRect(x + size - 10, y + 6, 4, 4);

                g2.dispose();
            }
        };
        iconPanel.setPreferredSize(new Dimension(40, 40)); // Smaller panel
        iconPanel.setOpaque(false);
        return iconPanel;
    }

    private JPanel createTextPanel() {
        JPanel textPanel = new JPanel(new GridBagLayout());
        textPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Boutique name
        nameLabel = new JLabel(boutiqueName);
        nameLabel.setFont(UITheme.getHeadingFont().deriveFont(Font.BOLD, 14f)); // Smaller font
        nameLabel.setForeground(UITheme.TEXT_PRIMARY);
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        textPanel.add(nameLabel, gbc);

        // Context/subtitle
        gbc.gridy = 1;
        gbc.insets = new Insets(1, 0, 0, 0); // Smaller spacing
        contextLabel = new JLabel("Boutique active");
        contextLabel.setFont(UITheme.getSmallFont().deriveFont(11f)); // Smaller font
        contextLabel.setForeground(UITheme.TEXT_SECONDARY);
        contextLabel.setHorizontalAlignment(SwingConstants.CENTER);
        textPanel.add(contextLabel, gbc);

        return textPanel;
    }

    public void setBoutiqueName(String name) {
        this.boutiqueName = name != null ? name : "Aucune boutique";
        if (nameLabel != null) {
            nameLabel.setText(this.boutiqueName);
        }
        repaint();
        revalidate();
    }

    public void setContextText(String text) {
        if (contextLabel != null) {
            contextLabel.setText(text);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Add subtle rounded corners
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw rounded corners (smaller radius)
        int arc = 8;
        Shape roundedRect = new java.awt.geom.RoundRectangle2D.Float(
                0, 0, getWidth(), getHeight(), arc, arc);

        // Set background with rounded corners
        g2.setColor(getBackground());
        g2.fill(roundedRect);

        // Draw border
        g2.setColor(UITheme.BORDER_MEDIUM);
        g2.setStroke(new BasicStroke(1f));
        g2.draw(roundedRect);

        g2.dispose();
    }

    // Optional: Minimal hover effect
    public void addHoverEffect() {
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                setBackground(UITheme.BG_SECONDARY);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                setBackground(UITheme.SURFACE);
            }
        });
    }

    // For even more compact display (single line)
    public void setSingleLineMode(boolean singleLine) {
        if (singleLine) {
            contextLabel.setVisible(false);
            setPreferredSize(new Dimension(220, 40));
            setMaximumSize(new Dimension(250, 40));
        } else {
            contextLabel.setVisible(true);
            setPreferredSize(new Dimension(220, 55));
            setMaximumSize(new Dimension(250, 55));
        }
        revalidate();
        repaint();
    }
}