package presentation.ui.components;

import presentation.ui.utils.UITheme;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class RedButton extends JButton {
    private Color hoverColor;
    private Color pressColor;
    private int borderRadius = 6;
    
    public RedButton(String text) {
        super(text);
        initButton(UITheme.PRIMARY, UITheme.PRIMARY_LIGHT, UITheme.PRIMARY_DARK);
    }
    
    public RedButton(String text, Color bgColor, Color hoverBg, Color pressBg) {
        super(text);
        initButton(bgColor, hoverBg, pressBg);
    }
    
    private void initButton(Color bgColor, Color hoverBg, Color pressBg) {
        setFont(UITheme.getButtonFont().deriveFont(15f));
        setForeground(Color.WHITE);
        setBackground(bgColor);
        this.hoverColor = hoverBg;
        this.pressColor = pressBg;
        setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setFocusPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setBorderRadius(8);
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(hoverColor);
                repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(bgColor);
                repaint();
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                setBackground(pressColor);
                repaint();
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (contains(e.getPoint())) {
                    setBackground(hoverColor);
                } else {
                    setBackground(bgColor);
                }
                repaint();
            }
        });
    }
    
    public void setBorderRadius(int radius) {
        this.borderRadius = radius;
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        // Draw background
        g2.setColor(getBackground());
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), borderRadius, borderRadius));
        
        // Draw text
        g2.setColor(getForeground());
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(getText());
        int textHeight = fm.getHeight();
        int x = (getWidth() - textWidth) / 2;
        int y = (getHeight() - textHeight) / 2 + fm.getAscent();
        
        g2.setFont(getFont());
        g2.drawString(getText(), x, y);
        
        g2.dispose();
    }
    
    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(UITheme.darken(getBackground(), 0.1f));
        g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, borderRadius, borderRadius));
        g2.dispose();
    }
}