package presentation.ui.panels;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import dao.Appareil;
import metier.GestionAppareil;
import presentation.ui.components.*;
import presentation.ui.utils.UITheme;
import exception.DatabaseException;

public class AppareilPanel extends JPanel {
    private GestionAppareil gestionAppareil;
    private RedTable table;
    private DefaultTableModel tableModel;
    private String[] columnNames = { "ID", "Marque", "Modèle", "IMEI", "Couleur", "Type" };

    public AppareilPanel() {
        gestionAppareil = new GestionAppareil();
        setLayout(new BorderLayout());
        setBackground(UITheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        initComponents();
        loadAppareils();
    }

    private void initComponents() {
        // Header - changed to vertical layout
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Top row: Title and CRUD buttons
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        topRow.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0)); // Space below

        JLabel title = new JLabel("Gestion des Appareils");
        title.setFont(UITheme.getTitleFont());
        title.setForeground(UITheme.TEXT_PRIMARY);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));

        // Primary actions
        JPanel primaryActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        primaryActions.setOpaque(false);
        RedButton addBtn = new RedButton("Ajouter");
        addBtn.setPreferredSize(new Dimension(140, 38));
        addBtn.addActionListener(e -> showAddDialog());
        primaryActions.add(addBtn);

        // Secondary actions
        JPanel secondaryActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        secondaryActions.setOpaque(false);
        RedButton editBtn = new RedButton("Modifier");
        editBtn.setPreferredSize(new Dimension(120, 38));
        editBtn.addActionListener(e -> editAppareil());
        RedButton deleteBtn = new RedButton("Supprimer");
        deleteBtn.setPreferredSize(new Dimension(120, 38));
        deleteBtn.addActionListener(e -> deleteAppareil());
        RedButton refreshBtn = new RedButton("Actualiser");
        refreshBtn.setPreferredSize(new Dimension(120, 38));
        refreshBtn.addActionListener(e -> loadAppareils());

        secondaryActions.add(editBtn);
        secondaryActions.add(Box.createRigidArea(new Dimension(8, 0)));
        secondaryActions.add(deleteBtn);
        secondaryActions.add(Box.createRigidArea(new Dimension(8, 0)));
        secondaryActions.add(refreshBtn);

        JPanel buttonContainer = new JPanel(new BorderLayout());
        buttonContainer.setOpaque(false);
        buttonContainer.add(primaryActions, BorderLayout.WEST);
        buttonContainer.add(secondaryActions, BorderLayout.EAST);

        topRow.add(title, BorderLayout.WEST);
        topRow.add(buttonContainer, BorderLayout.CENTER);

        // Add top row to header
        headerPanel.add(topRow);

        table = new RedTable();
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table.setModel(tableModel);

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_MEDIUM, 1),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        tableScroll.setBackground(UITheme.BACKGROUND);

        // Assemble
        add(headerPanel, BorderLayout.NORTH);
        add(tableScroll, BorderLayout.CENTER);
    }

    private void loadAppareils() {
        tableModel.setRowCount(0);
        try {
            List<Appareil> appareils = gestionAppareil.findAll();
            for (Appareil appareil : appareils) {
                tableModel.addRow(new Object[] {
                        appareil.getId(),
                        appareil.getMarque(),
                        appareil.getModele(),
                        appareil.getImei(),
                        appareil.getCouleur(),
                        appareil.getTypeAppareil()
                });
            }
        } catch (DatabaseException e) {
            UITheme.showStyledMessageDialog(this, "Erreur de chargement des appareils: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Nouvel Appareil", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 300);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel marqueLabel = new JLabel("Marque:");
        JTextField marqueField = new JTextField();

        JLabel modeleLabel = new JLabel("Modèle:");
        JTextField modeleField = new JTextField();

        JLabel imeiLabel = new JLabel("IMEI:");
        JTextField imeiField = new JTextField();

        JLabel couleurLabel = new JLabel("Couleur:");
        JTextField couleurField = new JTextField();

        JLabel typeLabel = new JLabel("Type d'appareil:");
        JTextField typeField = new JTextField();

        formPanel.add(marqueLabel);
        formPanel.add(marqueField);
        formPanel.add(modeleLabel);
        formPanel.add(modeleField);
        formPanel.add(imeiLabel);
        formPanel.add(imeiField);
        formPanel.add(couleurLabel);
        formPanel.add(couleurField);
        formPanel.add(typeLabel);
        formPanel.add(typeField);
        // Add empty row for spacing
        formPanel.add(new JLabel());
        formPanel.add(new JLabel());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        RedButton saveButton = new RedButton("Enregistrer");
        RedButton cancelButton = new RedButton("Annuler");

        saveButton.addActionListener(e -> {
            if (marqueField.getText().trim().isEmpty() ||
                    modeleField.getText().trim().isEmpty() ||
                    imeiField.getText().trim().isEmpty()) {
                UITheme.showStyledMessageDialog(dialog, "Marque, Modèle et IMEI sont obligatoires", "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                Appareil newAppareil = Appareil.builder()
                        .marque(marqueField.getText().trim())
                        .modele(modeleField.getText().trim())
                        .imei(imeiField.getText().trim())
                        .couleur(couleurField.getText().trim())
                        .typeAppareil(typeField.getText().trim())
                        .build();

                gestionAppareil.add(newAppareil);
                loadAppareils();
                dialog.dispose();
            } catch (DatabaseException ex) {
                UITheme.showStyledMessageDialog(dialog, "Erreur d'ajout: " + ex.getMessage(), "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                UITheme.showStyledMessageDialog(dialog, "Erreur inattendue: " + ex.getMessage(), "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void editAppareil() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            UITheme.showStyledMessageDialog(this, "Veuillez sélectionner un appareil", "Aucune sélection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int appareilId = (int) tableModel.getValueAt(selectedRow, 0);
        try {
            Appareil appareil = gestionAppareil.findById(appareilId);

            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Modifier Appareil", true);
            dialog.setLayout(new BorderLayout());
            dialog.setSize(500, 300);
            dialog.setLocationRelativeTo(this);

            JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
            formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            JLabel marqueLabel = new JLabel("Marque:");
            JTextField marqueField = new JTextField(appareil.getMarque());

            JLabel modeleLabel = new JLabel("Modèle:");
            JTextField modeleField = new JTextField(appareil.getModele());

            JLabel imeiLabel = new JLabel("IMEI:");
            JTextField imeiField = new JTextField(appareil.getImei());

            JLabel couleurLabel = new JLabel("Couleur:");
            JTextField couleurField = new JTextField(appareil.getCouleur());

            JLabel typeLabel = new JLabel("Type d'appareil:");
            JTextField typeField = new JTextField(appareil.getTypeAppareil());

            formPanel.add(marqueLabel);
            formPanel.add(marqueField);
            formPanel.add(modeleLabel);
            formPanel.add(modeleField);
            formPanel.add(imeiLabel);
            formPanel.add(imeiField);
            formPanel.add(couleurLabel);
            formPanel.add(couleurField);
            formPanel.add(typeLabel);
            formPanel.add(typeField);
            formPanel.add(new JLabel());
            formPanel.add(new JLabel());

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            RedButton saveButton = new RedButton("Enregistrer");
            RedButton cancelButton = new RedButton("Annuler");

            saveButton.addActionListener(e -> {
                if (marqueField.getText().trim().isEmpty() ||
                        modeleField.getText().trim().isEmpty() ||
                        imeiField.getText().trim().isEmpty()) {
                    UITheme.showStyledMessageDialog(dialog, "Marque, Modèle et IMEI sont obligatoires", "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    appareil.setMarque(marqueField.getText().trim());
                    appareil.setModele(modeleField.getText().trim());
                    appareil.setImei(imeiField.getText().trim());
                    appareil.setCouleur(couleurField.getText().trim());
                    appareil.setTypeAppareil(typeField.getText().trim());

                    gestionAppareil.update(appareil);
                    loadAppareils();
                    dialog.dispose();
                } catch (DatabaseException ex) {
                    UITheme.showStyledMessageDialog(dialog, "Erreur de modification: " + ex.getMessage(), "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    UITheme.showStyledMessageDialog(dialog, "Erreur inattendue: " + ex.getMessage(), "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                }
            });

            cancelButton.addActionListener(e -> dialog.dispose());

            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);

            dialog.add(formPanel, BorderLayout.CENTER);
            dialog.add(buttonPanel, BorderLayout.SOUTH);
            dialog.setVisible(true);

        } catch (DatabaseException ex) {
            UITheme.showStyledMessageDialog(this, "Erreur lors de la recherche: " + ex.getMessage(), "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteAppareil() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            UITheme.showStyledMessageDialog(this, "Veuillez sélectionner un appareil", "Aucune sélection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int appareilId = (int) tableModel.getValueAt(selectedRow, 0);
        try {
            Appareil appareil = gestionAppareil.findById(appareilId);

            int confirm = UITheme.showStyledConfirmDialog(this,
                    "Supprimer l'appareil: " + appareil.getMarque() + " " + appareil.getModele() + "?",
                    "Confirmation");
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    gestionAppareil.delete(appareil);
                    loadAppareils();
                } catch (DatabaseException ex) {
                    UITheme.showStyledMessageDialog(this, "Erreur de suppression: " + ex.getMessage(), "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (DatabaseException ex) {
            UITheme.showStyledMessageDialog(this, "Erreur lors de la recherche: " + ex.getMessage(), "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}