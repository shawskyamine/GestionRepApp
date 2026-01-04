package presentation.ui.components;

import presentation.ui.utils.UITheme;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class RedNavButton extends JButton {
    private boolean isActive = false;
    private Color hoverColor = UITheme.SIDEBAR_HOVER;
    private Color activeColor = UITheme.SIDEBAR_ACTIVE;
    private Color inactiveColor = UITheme.SIDEBAR_BG;
    
    public RedNavButton(String text) {
        super(text);
        initButton();
    }
    
    public RedNavButton(String text, Icon icon) {
        super(text, icon);
        initButton();
        setIconTextGap(12);
    }
    
    private void initButton() {
        setAlignmentX(Component.LEFT_ALIGNMENT);
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        setPreferredSize(new Dimension(200, 50));
        setMinimumSize(new Dimension(120, 50));
        setFont(UITheme.getBodyFont().deriveFont(15f));
        setForeground(Color.WHITE);
        setBackground(inactiveColor);
        setBorder(BorderFactory.createEmptyBorder(14, 24, 14, 24));
        setHorizontalAlignment(SwingConstants.LEFT);
        setFocusPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setContentAreaFilled(false);
        setOpaque(false);
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!isActive) {
                    setBackground(hoverColor);
                    setForeground(Color.WHITE);
                    repaint();
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (!isActive) {
                    setBackground(inactiveColor);
                    setForeground(Color.WHITE);
                    repaint();
                }
            }
        });
    }
    
    public void setActive(boolean active) {
        this.isActive = active;
        if (active) {
            setBackground(activeColor);
            setForeground(UITheme.SIDEBAR_TEXT_ACTIVE);
            setFont(UITheme.getBodyFont().deriveFont(Font.BOLD));
        } else {
            setBackground(inactiveColor);
            setForeground(UITheme.SIDEBAR_TEXT);
            setFont(UITheme.getBodyFont().deriveFont(Font.PLAIN));
        }
        repaint();
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        // Draw background
        g2.setColor(getBackground());
        g2.fill(new RoundRectangle2D.Float(4, 2, getWidth() - 8, getHeight() - 4, 6, 6));
        
        // Draw active indicator
        if (isActive) {
            g2.setColor(UITheme.PRIMARY);
            g2.fillRoundRect(0, getHeight() / 2 - 12, 3, 24, 2, 2);
        }
        
        g2.dispose();
        
        // Paint text and icon
        super.paintComponent(g);
    }
}