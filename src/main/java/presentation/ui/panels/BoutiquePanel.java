package presentation.ui.panels;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Date;
import java.util.List;
import presentation.ui.components.*;
import presentation.ui.utils.UITheme;
import metier.GestionBoutique;
import metier.GestionProprietaire;
import dao.Boutique;
import dao.Proprietaire;
import presentation.ui.utils.AuthService;

public class BoutiquePanel extends JPanel {
    private GestionBoutique gestionBoutique;
    private GestionProprietaire gestionProprietaire;
    private RedTable boutiqueTable;
    private DefaultTableModel tableModel;
    private String[] columnNames = { "ID", "Nom", "Adresse", "Date Création", "Propriétaire" };

    public BoutiquePanel() {
        gestionBoutique = new GestionBoutique();
        gestionProprietaire = new GestionProprietaire();
        setLayout(new BorderLayout());
        setBackground(UITheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        initComponents();
        loadBoutiques();
    }

    private void initComponents() {
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel title = new JLabel("🏪 Gestion des Boutiques");
        title.setFont(UITheme.getTitleFont());
        title.setForeground(UITheme.TEXT_PRIMARY);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));

        // Primary actions
        JPanel primaryActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        primaryActions.setOpaque(false);
        RedButton addButton = new RedButton("Nouvelle");
        addButton.setPreferredSize(new Dimension(140, 38));
        addButton.addActionListener(e -> showAddDialog());
        primaryActions.add(addButton);

        // Secondary actions
        JPanel secondaryActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        secondaryActions.setOpaque(false);
        RedButton editButton = new RedButton("Modifier");
        editButton.setPreferredSize(new Dimension(120, 38));
        editButton.addActionListener(e -> editBoutique());
        RedButton deleteButton = new RedButton("Supprimer");
        deleteButton.setPreferredSize(new Dimension(130, 38));
        deleteButton.addActionListener(e -> deleteBoutique());
        RedButton refreshButton = new RedButton("Actualiser");
        refreshButton.setPreferredSize(new Dimension(130, 38));
        refreshButton.addActionListener(e -> loadBoutiques());

        secondaryActions.add(editButton);
        secondaryActions.add(Box.createRigidArea(new Dimension(8, 0)));
        secondaryActions.add(deleteButton);
        secondaryActions.add(Box.createRigidArea(new Dimension(8, 0)));
        secondaryActions.add(refreshButton);

        JPanel buttonContainer = new JPanel(new BorderLayout());
        buttonContainer.setOpaque(false);
        buttonContainer.add(primaryActions, BorderLayout.WEST);
        buttonContainer.add(secondaryActions, BorderLayout.EAST);

        headerPanel.add(title, BorderLayout.WEST);
        headerPanel.add(buttonContainer, BorderLayout.CENTER);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 24, 0));

        // Table
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        boutiqueTable = new RedTable();
        boutiqueTable.setModel(tableModel);

        JScrollPane tableScroll = new JScrollPane(boutiqueTable);
        tableScroll.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_MEDIUM, 1),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        tableScroll.setBackground(UITheme.BACKGROUND);

        // Assemble
        add(headerPanel, BorderLayout.NORTH);
        add(tableScroll, BorderLayout.CENTER);
    }

    private void loadBoutiques() {
        tableModel.setRowCount(0);
        List<Boutique> boutiques = gestionBoutique.findAll();
        if (boutiques != null) {
            for (Boutique boutique : boutiques) {
                String proprietaireName = boutique.getProprietaire() != null
                        ? boutique.getProprietaire().getNom() + " " + boutique.getProprietaire().getPrenom()
                        : "N/A";
                tableModel.addRow(new Object[] {
                        boutique.getId(),
                        boutique.getNomboutique(),
                        boutique.getAdresse(),
                        boutique.getDateDeCreation(),
                        proprietaireName
                });
            }
        }
    }

    private void showAddDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "➕ Nouvelle Boutique", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(450, 250);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel nomLabel = new JLabel("Nom:");
        JTextField nomField = new JTextField();
        JLabel adresseLabel = new JLabel("Adresse:");
        JTextField adresseField = new JTextField();

        formPanel.add(nomLabel);
        formPanel.add(nomField);
        formPanel.add(adresseLabel);
        formPanel.add(adresseField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        RedButton saveButton = new RedButton("💾 Enregistrer");
        RedButton cancelButton = new RedButton("Annuler");

        saveButton.addActionListener(e -> {
            if (nomField.getText().trim().isEmpty() || adresseField.getText().trim().isEmpty()) {
                UITheme.showStyledMessageDialog(dialog, "Tous les champs sont obligatoires", "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                if (!AuthService.isProprietaire()) {
                    UITheme.showStyledMessageDialog(dialog, "Seul un propriétaire peut créer une boutique",
                            "Accès refusé",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Proprietaire proprietaire = (Proprietaire) AuthService.getCurrentUser();

                Boutique newBoutique = Boutique.builder()
                        .nomboutique(nomField.getText().trim())
                        .adresse(adresseField.getText().trim())
                        .dateDeCreation(new Date())
                        .proprietaire(proprietaire)
                        .build();

                gestionBoutique.add(newBoutique);
                loadBoutiques();
                dialog.dispose();
            } catch (Exception ex) {
                UITheme.showStyledMessageDialog(dialog, "Erreur d'ajout: " + ex.getMessage(), "Erreur",
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

    private void editBoutique() {
        int selectedRow = boutiqueTable.getSelectedRow();
        if (selectedRow == -1) {
            UITheme.showStyledMessageDialog(this, "Veuillez sélectionner une boutique", "Aucune sélection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int boutiqueId = (int) tableModel.getValueAt(selectedRow, 0);
        Boutique boutique = gestionBoutique.findById(boutiqueId);

        if (boutique == null) {
            UITheme.showStyledMessageDialog(this, "Boutique non trouvée", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "✏️ Modifier Boutique", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(450, 250);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel nomLabel = new JLabel("Nom:");
        JTextField nomField = new JTextField(boutique.getNomboutique());
        JLabel adresseLabel = new JLabel("Adresse:");
        JTextField adresseField = new JTextField(boutique.getAdresse());

        formPanel.add(nomLabel);
        formPanel.add(nomField);
        formPanel.add(adresseLabel);
        formPanel.add(adresseField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        RedButton saveButton = new RedButton("💾 Enregistrer");
        RedButton cancelButton = new RedButton("Annuler");

        saveButton.addActionListener(e -> {
            if (nomField.getText().trim().isEmpty() || adresseField.getText().trim().isEmpty()) {
                UITheme.showStyledMessageDialog(dialog, "Tous les champs sont obligatoires", "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                boutique.setNomboutique(nomField.getText().trim());
                boutique.setAdresse(adresseField.getText().trim());
                gestionBoutique.update(boutique);
                loadBoutiques();
                dialog.dispose();
            } catch (Exception ex) {
                UITheme.showStyledMessageDialog(dialog, "Erreur de modification: " + ex.getMessage(), "Erreur",
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

    private void deleteBoutique() {
        int selectedRow = boutiqueTable.getSelectedRow();
        if (selectedRow == -1) {
            UITheme.showStyledMessageDialog(this, "Veuillez sélectionner une boutique", "Aucune sélection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int boutiqueId = (int) tableModel.getValueAt(selectedRow, 0);
        Boutique boutique = gestionBoutique.findById(boutiqueId);

        if (boutique == null) {
            UITheme.showStyledMessageDialog(this, "Boutique non trouvée", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = UITheme.showStyledConfirmDialog(this, "Supprimer la boutique: " + boutique.getNomboutique() + "?",
                "Confirmation");
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                gestionBoutique.delete(boutique);
                loadBoutiques();
            } catch (Exception ex) {
                UITheme.showStyledMessageDialog(this, "Erreur de suppression: " + ex.getMessage(), "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}