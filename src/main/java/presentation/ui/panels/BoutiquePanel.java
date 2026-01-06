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
import exception.DatabaseException;
import exception.EntityNotFoundException;

public class BoutiquePanel extends JPanel {
    private GestionBoutique gestionBoutique;
    private GestionProprietaire gestionProprietaire;
    private RedTable boutiqueTable;
    private DefaultTableModel tableModel;
    private String[] columnNames = { "ID", "Nom", "Adresse", "Date Création", "Propriétaire" };

    public BoutiquePanel() {
        try {
            System.out.println("BoutiquePanel constructor started");
            gestionBoutique = new GestionBoutique();
            System.out.println("GestionBoutique created");
            gestionProprietaire = new GestionProprietaire();
            System.out.println("GestionProprietaire created");
            setLayout(new BorderLayout());
            setBackground(UITheme.BACKGROUND);
            setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            System.out.println("Layout and background set");

            initComponents();
            System.out.println("initComponents completed");
            loadBoutiques();
            System.out.println("loadBoutiques completed");
            System.out.println("BoutiquePanel constructor completed successfully");
        } catch (DatabaseException e) {
            System.out.println("DatabaseException in BoutiquePanel constructor: " + e.getMessage());
            e.printStackTrace();
            // Don't re-throw, just show error message
            JOptionPane.showMessageDialog(this,
                    "Erreur lors de l'initialisation du panneau Boutique: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initComponents() {
        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Top row: Title and CRUD buttons
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        topRow.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0)); // Space below

        JLabel title = new JLabel("Gestion des Boutiques");
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
        refreshButton.addActionListener(e -> {
            try {
                loadBoutiques();
            } catch (DatabaseException ex) {
                JOptionPane.showMessageDialog(this, "Erreur lors de l'actualisation: " + ex.getMessage(), "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        secondaryActions.add(editButton);
        secondaryActions.add(Box.createRigidArea(new Dimension(8, 0)));
        secondaryActions.add(deleteButton);
        secondaryActions.add(Box.createRigidArea(new Dimension(8, 0)));
        secondaryActions.add(refreshButton);

        JPanel buttonContainer = new JPanel(new BorderLayout());
        buttonContainer.setOpaque(false);
        buttonContainer.add(primaryActions, BorderLayout.WEST);
        buttonContainer.add(secondaryActions, BorderLayout.EAST);

        topRow.add(title, BorderLayout.WEST);
        topRow.add(buttonContainer, BorderLayout.CENTER);

        // Add top row to header
        headerPanel.add(topRow);

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

    private void loadBoutiques() throws DatabaseException {
        tableModel.setRowCount(0);
        try {
            List<Boutique> boutiques = gestionBoutique.findAll();
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
        } catch (DatabaseException e) {
            throw e; // Re-throw to be caught by caller
        }
    }

    private void showAddDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Nouvelle Boutique", true);
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
            } catch (DatabaseException ex) {
                UITheme.showStyledMessageDialog(dialog, "Erreur de base de données: " + ex.getMessage(), "Erreur",
                        JOptionPane.ERROR_MESSAGE);
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
        try {
            Boutique boutique = gestionBoutique.findById(boutiqueId);

            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Modifier Boutique", true);
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
                } catch (DatabaseException ex) {
                    UITheme.showStyledMessageDialog(dialog, "Erreur de base de données: " + ex.getMessage(), "Erreur",
                            JOptionPane.ERROR_MESSAGE);
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

        } catch (DatabaseException ex) {
            UITheme.showStyledMessageDialog(this, "Erreur lors de la récupération de la boutique: " + ex.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteBoutique() {
        int selectedRow = boutiqueTable.getSelectedRow();
        if (selectedRow == -1) {
            UITheme.showStyledMessageDialog(this, "Veuillez sélectionner une boutique", "Aucune sélection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int boutiqueId = (int) tableModel.getValueAt(selectedRow, 0);
        try {
            Boutique boutique = gestionBoutique.findById(boutiqueId);

            int confirm = UITheme.showStyledConfirmDialog(this,
                    "Supprimer la boutique: " + boutique.getNomboutique() + "?",
                    "Confirmation");
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    gestionBoutique.delete(boutique);
                    loadBoutiques();
                } catch (DatabaseException ex) {
                    UITheme.showStyledMessageDialog(this, "Erreur de base de données: " + ex.getMessage(), "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (DatabaseException ex) {
            UITheme.showStyledMessageDialog(this, "Erreur lors de la récupération de la boutique: " + ex.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}