package presentation.ui.components;

import presentation.ui.utils.UITheme;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class RedCard extends JPanel {
    private boolean shadow = true;
    private int borderRadius = 8;
    private Color borderColor = UITheme.BORDER_MEDIUM;
    
    public RedCard() {
        this(true);
    }
    
    public RedCard(LayoutManager layout) {
        this(true);
        setLayout(layout);
    }
    
    public RedCard(boolean withShadow) {
        this.shadow = withShadow;
        setBackground(UITheme.SURFACE);
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));
        setOpaque(false);
        setBorderRadius(12);
    }
    
    public void setBorderRadius(int radius) {
        this.borderRadius = radius;
        repaint();
    }
    
    public void setBorderColor(Color color) {
        this.borderColor = color;
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        int shadowOffset = shadow ? 3 : 0;
        
        // Draw shadow
        if (shadow) {
            for (int i = 0; i < 4; i++) {
                float alpha = (4 - i) * 0.08f;
                g2.setColor(new Color(0, 0, 0, (int)(alpha * 255)));
                g2.fill(new RoundRectangle2D.Float(i, i + 1, 
                    getWidth() - i * 2, getHeight() - i * 2, borderRadius, borderRadius));
            }
        }
        
        // Draw background
        g2.setColor(getBackground());
        g2.fill(new RoundRectangle2D.Float(shadowOffset, shadowOffset,
            getWidth() - shadowOffset * 2, getHeight() - shadowOffset * 2, 
            borderRadius, borderRadius));
        
        // Draw border
        g2.setColor(new Color(borderColor.getRed(), borderColor.getGreen(), borderColor.getBlue(), 100));
        g2.setStroke(new BasicStroke(1f));
        g2.draw(new RoundRectangle2D.Float(shadowOffset + 0.5f, shadowOffset + 0.5f,
            getWidth() - shadowOffset * 2 - 1, getHeight() - shadowOffset * 2 - 1,
            borderRadius, borderRadius));
        
        g2.dispose();
        super.paintComponent(g);
    }
    
    @Override
    public Insets getInsets() {
        Insets base = super.getInsets();
        int offset = shadow ? 6 : 0;
        return new Insets(base.top + offset, base.left + offset, 
                         base.bottom + offset, base.right + offset);
    }
}