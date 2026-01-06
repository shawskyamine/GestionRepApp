package presentation.ui.components;

import presentation.ui.utils.UITheme;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

public class RedTable extends JTable {
    private int hoveredRow = -1;

    public RedTable() {
        super();
        setRowHeight(60); // Using theme-consistent height
        setShowHorizontalLines(true);
        setShowVerticalLines(false);
        setGridColor(UITheme.BORDER_MEDIUM);
        setIntercellSpacing(new Dimension(0, 0));
        setFont(UITheme.getBodyFont());
        setForeground(UITheme.TEXT_PRIMARY);
        setBackground(UITheme.SURFACE);
        setSelectionBackground(UITheme.withAlpha(UITheme.PRIMARY, 30));
        setSelectionForeground(UITheme.TEXT_PRIMARY); // Fixed: using theme color
        setFillsViewportHeight(true);

        // Configure table header
        JTableHeader header = getTableHeader();
        header.setBackground(UITheme.BG_TERTIARY);
        header.setForeground(Color.WHITE); // Pure white for maximum visibility
        header.setFont(UITheme.getLabelFont().deriveFont(14f)); // Fixed: using theme font
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(header.getWidth(), 60));

        // Header renderer
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBackground(UITheme.BG_TERTIARY);
                c.setForeground(Color.WHITE); // Pure white for maximum visibility
                c.setFont(UITheme.getLabelFont().deriveFont(14f));
                ((JLabel) c).setHorizontalAlignment(SwingConstants.LEFT);
                ((JLabel) c).setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
                // Ensure text is set
                if (value != null) {
                    ((JLabel) c).setText(value.toString());
                }
                return c;
            }
        };

        // Apply header renderer to all columns
        applyHeaderRenderer(headerRenderer);

        // Also apply when model changes
        addPropertyChangeListener("model", evt -> {
            SwingUtilities.invokeLater(() -> applyHeaderRenderer(headerRenderer));
        });

        // Main cell renderer
        setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
                setFont(UITheme.getBodyFont().deriveFont(14f)); // Fixed: using theme font

                // Background logic
                if (isSelected) {
                    setBackground(UITheme.withAlpha(UITheme.PRIMARY, 30));
                    setForeground(Color.WHITE); // White text on selected background
                } else if (row == hoveredRow) {
                    setBackground(UITheme.SURFACE_HOVER);
                    setForeground(Color.WHITE); // White text on hover
                } else {
                    // Alternate row colors
                    setBackground(row % 2 == 0 ? UITheme.SURFACE : UITheme.SURFACE_ELEVATED);
                    setForeground(Color.WHITE); // White text for maximum visibility
                }

                // Column-specific formatting
                if (column == 0) {
                    // First column (often ID or index)
                    setHorizontalAlignment(SwingConstants.CENTER);
                    setFont(UITheme.getLabelFont().deriveFont(13f));
                    setForeground(UITheme.PRIMARY_LIGHT); // Light red for ID column
                } else {
                    // Regular data columns
                    setHorizontalAlignment(SwingConstants.LEFT);
                    setFont(UITheme.getBodyFont().deriveFont(14f));
                    // Foreground already set to white above
                }

                return this;
            }
        });

        // Hover effect listeners
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = rowAtPoint(e.getPoint());
                if (row != hoveredRow) {
                    hoveredRow = row;
                    repaint();
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                hoveredRow = -1;
                repaint();
            }
        });
    }

    private void applyHeaderRenderer(DefaultTableCellRenderer renderer) {
        if (getModel() != null && getColumnModel().getColumnCount() > 0) {
            for (int i = 0; i < getColumnModel().getColumnCount(); i++) {
                getColumnModel().getColumn(i).setHeaderRenderer(renderer);
            }
            // Force header repaint
            getTableHeader().repaint();
        }
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    // Optional: Add method to set column alignments
    public void setColumnAlignment(int column, int alignment) {
        TableColumn tableColumn = getColumnModel().getColumn(column);
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(alignment);
        tableColumn.setCellRenderer(renderer);
    }

    // Optional: Add method for numeric columns
    public void configureNumericColumn(int column) {
        TableColumn tableColumn = getColumnModel().getColumn(column);
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                setHorizontalAlignment(SwingConstants.RIGHT);
                setFont(UITheme.getBodyFont());
                // Keep the same background/foreground logic
                if (isSelected) {
                    setBackground(UITheme.withAlpha(UITheme.PRIMARY, 30));
                    setForeground(Color.WHITE); // White text on selected
                } else if (row == hoveredRow) {
                    setBackground(UITheme.SURFACE_HOVER);
                    setForeground(Color.WHITE); // White text on hover
                } else {
                    setBackground(row % 2 == 0 ? UITheme.SURFACE : UITheme.SURFACE_ELEVATED);
                    setForeground(Color.WHITE); // White text for visibility
                }
                return this;
            }
        };
        tableColumn.setCellRenderer(rightRenderer);
    }
}