package presentation.ui.panels;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import dao.Appareil;
import metier.GestionAppareil;
import presentation.ui.components.*;
import presentation.ui.utils.UITheme;

public class AppareilPanel extends JPanel {
    private GestionAppareil gestionAppareil;
    private RedTable table;
    private DefaultTableModel tableModel;

    public AppareilPanel() {
        gestionAppareil = new GestionAppareil();
        setLayout(new BorderLayout());
        setBackground(UITheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));
        
        initComponents();
        loadAppareils();
    }
    
    private void initComponents() {
        JLabel title = new JLabel("📱 Gestion des Appareils");
        title.setFont(UITheme.getTitleFont());
        title.setForeground(UITheme.TEXT_PRIMARY);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 24, 0));
        
        JPanel content = new JPanel(new BorderLayout());
        content.setOpaque(false);
        
        String[] columns = {"ID", "Marque", "Modèle", "IMEI", "Couleur", "Type"};
        
        table = new RedTable();
        tableModel = new DefaultTableModel(null, columns);
        table.setModel(tableModel);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));
        RedButton addBtn = new RedButton("➕ Ajouter");
        addBtn.setPreferredSize(new Dimension(140, 38));
        RedButton assignBtn = new RedButton("🔧 Assigner");
        assignBtn.setPreferredSize(new Dimension(140, 38));
        buttonPanel.add(addBtn);
        buttonPanel.add(Box.createRigidArea(new Dimension(8, 0)));
        buttonPanel.add(assignBtn);
        
        content.add(buttonPanel, BorderLayout.NORTH);
        content.add(new JScrollPane(table), BorderLayout.CENTER);
        
        add(title, BorderLayout.NORTH);
        add(content, BorderLayout.CENTER);
    }

    private void loadAppareils() {
        tableModel.setRowCount(0);
        List<Appareil> appareils = gestionAppareil.findAll();
        for (Appareil appareil : appareils) {
            tableModel.addRow(new Object[]{
                appareil.getId(),
                appareil.getMarque(),
                appareil.getModele(),
                appareil.getImei(),
                appareil.getCouleur(),
                appareil.getTypeAppareil()
            });
        }
    }
}