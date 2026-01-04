package presentation.ui.panels;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import presentation.ui.components.*;
import presentation.ui.utils.UITheme;
import metier.GestionReparation;
import metier.GestionReparateur;
import metier.GestionAppareil;
import metier.GestionPiece;
import dao.Reparation;
import dao.Reparateur;
import dao.Appareil;
import dao.Piece;
import dao.Proprietaire;
import presentation.ui.utils.AuthService;

public class ReparationPanel extends JPanel {
    private GestionReparation gestionReparation;
    private GestionReparateur gestionReparateur;
    private GestionAppareil gestionAppareil;
    private GestionPiece gestionPiece;
    private RedTable reparationTable;
    private DefaultTableModel tableModel;
    private String[] columnNames = { "ID", "Code", "Statut", "Cause", "Date Création", "Réparateur", "Prix Total" };

    public ReparationPanel() {
        gestionReparation = new GestionReparation();
        gestionReparateur = new GestionReparateur();
        gestionAppareil = new GestionAppareil();
        gestionPiece = new GestionPiece();
        setLayout(new BorderLayout());
        setBackground(UITheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        initComponents();
        loadReparations();
    }

    private void initComponents() {
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel title = new JLabel("Gestion des Réparations");
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
        editButton.addActionListener(e -> editReparation());
        RedButton deleteButton = new RedButton("Supprimer");
        deleteButton.setPreferredSize(new Dimension(130, 38));
        deleteButton.addActionListener(e -> deleteReparation());
        RedButton refreshButton = new RedButton("Actualiser");
        refreshButton.setPreferredSize(new Dimension(130, 38));
        refreshButton.addActionListener(e -> loadReparations());
        RedButton manageAppareilsButton = new RedButton("Gérer");
        manageAppareilsButton.setPreferredSize(new Dimension(120, 38));
        manageAppareilsButton.addActionListener(e -> manageAppareils());

        secondaryActions.add(editButton);
        secondaryActions.add(Box.createRigidArea(new Dimension(8, 0)));
        secondaryActions.add(deleteButton);
        secondaryActions.add(Box.createRigidArea(new Dimension(8, 0)));
        secondaryActions.add(refreshButton);
        secondaryActions.add(Box.createRigidArea(new Dimension(8, 0)));
        secondaryActions.add(manageAppareilsButton);

        JPanel buttonContainer = new JPanel(new BorderLayout());
        buttonContainer.setOpaque(false);
        buttonContainer.add(primaryActions, BorderLayout.WEST);
        buttonContainer.add(secondaryActions, BorderLayout.EAST);

        headerPanel.add(title, BorderLayout.WEST);
        headerPanel.add(buttonContainer, BorderLayout.CENTER);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Table
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        reparationTable = new RedTable();
        reparationTable.setModel(tableModel);

        JScrollPane tableScroll = new JScrollPane(reparationTable);
        tableScroll.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_MEDIUM, 1),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        tableScroll.setBackground(UITheme.BACKGROUND);

        // Assemble
        add(headerPanel, BorderLayout.NORTH);
        add(tableScroll, BorderLayout.CENTER);
    }

    private void loadReparations() {
        tableModel.setRowCount(0);
        List<Reparation> reparations = gestionReparation.findAll();
        if (reparations != null) {
            for (Reparation reparation : reparations) {
                String reparateurName = reparation.getReparateur() != null
                        ? reparation.getReparateur().getNom() + " " + reparation.getReparateur().getPrenom()
                        : "N/A";
                tableModel.addRow(new Object[] {
                        reparation.getId(),
                        reparation.getCodeReparation(),
                        reparation.getStatut(),
                        reparation.getCauseDeReparation(),
                        reparation.getDateDeCreation(),
                        reparateurName,
                        reparation.getPrixTotal()
                });
            }
        }
        reparationTable.revalidate();
        reparationTable.repaint();
    }

    private void showAddDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Nouvelle Réparation", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Basic Info Section
        JPanel basicPanel = createBasicInfoPanel();
        mainPanel.add(basicPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Appareils Section
        JPanel appareilsPanel = new JPanel();
        appareilsPanel.setLayout(new BoxLayout(appareilsPanel, BoxLayout.Y_AXIS));
        appareilsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(UITheme.PRIMARY),
                "Appareils à Réparer"));
        mainPanel.add(appareilsPanel);

        // Set initial appareils
        JSpinner appareilsSpinner = (JSpinner) basicPanel.getClientProperty("appareilsSpinner");
        updateAppareilsPanel(appareilsPanel, (Integer) appareilsSpinner.getValue());
        appareilsSpinner
                .addChangeListener(e -> updateAppareilsPanel(appareilsPanel, (Integer) appareilsSpinner.getValue()));

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        RedButton saveButton = new RedButton("Enregistrer");
        RedButton cancelButton = new RedButton("Annuler");

        saveButton.addActionListener(e -> saveReparation(dialog, basicPanel, appareilsPanel));
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private JPanel createBasicInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(UITheme.PRIMARY),
                "Informations de Base"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Code Reparation
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Code Réparation:"), gbc);
        gbc.gridx = 1;
        JTextField codeField = new JTextField(20);
        panel.add(codeField, gbc);

        // Statut
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Statut:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> statutCombo = new JComboBox<>(
                new String[] { "En cours", "Terminée", "En attente", "Annulée" });
        panel.add(statutCombo, gbc);

        // Cause
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Cause de Réparation:"), gbc);
        gbc.gridx = 1;
        JTextField causeField = new JTextField(20);
        panel.add(causeField, gbc);

        // Date Creation (auto)
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Date de Création:"), gbc);
        gbc.gridx = 1;
        JLabel dateLabel = new JLabel(new Date().toString());
        panel.add(dateLabel, gbc);

        // Nombre Appareils
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Nombre d'Appareils:"), gbc);
        gbc.gridx = 1;
        JSpinner appareilsSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        panel.add(appareilsSpinner, gbc);

        // Prix Total
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("Prix Total (MAD):"), gbc);
        gbc.gridx = 1;
        JTextField prixField = new JTextField(20);
        panel.add(prixField, gbc);

        // Reparateur selection for Proprietaire
        if (AuthService.isProprietaire()) {
            gbc.gridx = 0;
            gbc.gridy = 6;
            panel.add(new JLabel("Réparateur:"), gbc);
            gbc.gridx = 1;
            JComboBox<Reparateur> reparateurCombo = new JComboBox<>();
            List<Reparateur> reparateurs = gestionReparateur.findAll();
            if (reparateurs != null) {
                for (Reparateur r : reparateurs) {
                    reparateurCombo.addItem(r);
                }
            }
            // Add proprietaire as option
            Proprietaire proprietaire = (Proprietaire) AuthService.getCurrentUser();
            Reparateur proprietaireAsReparateur = Reparateur.builder()
                    .id(proprietaire.getId())
                    .nom(proprietaire.getNom())
                    .prenom(proprietaire.getPrenom())
                    .email(proprietaire.getEmail())
                    .build();
            reparateurCombo.addItem(proprietaireAsReparateur);
            panel.add(reparateurCombo, gbc);
            panel.putClientProperty("reparateurCombo", reparateurCombo);
        }

        // Store components for later access
        panel.putClientProperty("codeField", codeField);
        panel.putClientProperty("statutCombo", statutCombo);
        panel.putClientProperty("causeField", causeField);
        panel.putClientProperty("appareilsSpinner", appareilsSpinner);
        panel.putClientProperty("prixField", prixField);

        return panel;
    }

    private void updateAppareilsPanel(Component appareilsPanelComponent, int nombreAppareils) {
        if (!(appareilsPanelComponent instanceof JPanel))
            return;
        JPanel appareilsPanel = (JPanel) appareilsPanelComponent;
        appareilsPanel.removeAll();

        for (int i = 0; i < nombreAppareils; i++) {
            JPanel appareilPanel = createAppareilPanel(i + 1);
            appareilsPanel.add(appareilPanel);
            appareilsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        appareilsPanel.revalidate();
        appareilsPanel.repaint();
    }

    private JPanel createAppareilPanel(int appareilNumber) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(UITheme.SECONDARY),
                "Appareil " + appareilNumber));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // IMEI
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("IMEI Code:"), gbc);
        gbc.gridx = 1;
        JTextField imeiField = new JTextField(15);
        panel.add(imeiField, gbc);

        // Couleur
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Couleur:"), gbc);
        gbc.gridx = 1;
        JTextField couleurField = new JTextField(15);
        panel.add(couleurField, gbc);

        // Marque
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Marque:"), gbc);
        gbc.gridx = 1;
        JTextField marqueField = new JTextField(15);
        panel.add(marqueField, gbc);

        // Modele
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Modèle:"), gbc);
        gbc.gridx = 1;
        JTextField modeleField = new JTextField(15);
        panel.add(modeleField, gbc);

        // Type
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Type:"), gbc);
        gbc.gridx = 1;
        JTextField typeField = new JTextField(15);
        panel.add(typeField, gbc);

        // Nombre Pieces
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("Nombre de Pièces Cassées:"), gbc);
        gbc.gridx = 1;
        JSpinner piecesSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 20, 1));
        piecesSpinner.addChangeListener(e -> updatePiecesPanel(panel, (Integer) piecesSpinner.getValue()));
        panel.add(piecesSpinner, gbc);

        // Pieces Panel
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        JPanel piecesPanel = new JPanel();
        piecesPanel.setLayout(new BoxLayout(piecesPanel, BoxLayout.Y_AXIS));
        piecesPanel.setBorder(BorderFactory.createTitledBorder("Pièces Cassées"));
        panel.add(piecesPanel, gbc);

        // Store components
        panel.putClientProperty("imeiField", imeiField);
        panel.putClientProperty("couleurField", couleurField);
        panel.putClientProperty("marqueField", marqueField);
        panel.putClientProperty("modeleField", modeleField);
        panel.putClientProperty("typeField", typeField);
        panel.putClientProperty("piecesSpinner", piecesSpinner);
        panel.putClientProperty("piecesPanel", piecesPanel);

        return panel;
    }

    private void updatePiecesPanel(JPanel appareilPanel, int nombrePieces) {
        JPanel piecesPanel = (JPanel) appareilPanel.getClientProperty("piecesPanel");
        piecesPanel.removeAll();

        for (int i = 0; i < nombrePieces; i++) {
            JPanel piecePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            piecePanel.add(new JLabel("Pièce " + (i + 1) + ":"));
            JTextField pieceField = new JTextField(20);
            piecePanel.add(pieceField);
            piecesPanel.add(piecePanel);
        }

        piecesPanel.revalidate();
        piecesPanel.repaint();
    }

    private void saveReparation(JDialog dialog, JPanel basicPanel, JPanel appareilsPanel) {
        try {
            // Get basic info
            JTextField codeField = (JTextField) basicPanel.getClientProperty("codeField");
            JComboBox<String> statutCombo = (JComboBox<String>) basicPanel.getClientProperty("statutCombo");
            JTextField causeField = (JTextField) basicPanel.getClientProperty("causeField");
            JSpinner appareilsSpinner = (JSpinner) basicPanel.getClientProperty("appareilsSpinner");
            JTextField prixField = (JTextField) basicPanel.getClientProperty("prixField");

            if (codeField.getText().trim().isEmpty() || causeField.getText().trim().isEmpty()) {
                UITheme.showStyledMessageDialog(dialog, "Code et cause sont obligatoires", "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            double prixTotal = 0.0;
            try {
                prixTotal = Double.parseDouble(prixField.getText().trim());
                if (prixTotal < 0) {
                    UITheme.showStyledMessageDialog(dialog, "Le prix total doit être positif", "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException e) {
                UITheme.showStyledMessageDialog(dialog, "Prix total invalide", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Reparateur reparateur;
            if (AuthService.isReparateur()) {
                reparateur = (Reparateur) AuthService.getCurrentUser();
            } else if (AuthService.isProprietaire()) {
                JComboBox<Reparateur> reparateurCombo = (JComboBox<Reparateur>) basicPanel
                        .getClientProperty("reparateurCombo");
                reparateur = (Reparateur) reparateurCombo.getSelectedItem();
                if (reparateur == null) {
                    UITheme.showStyledMessageDialog(dialog, "Veuillez sélectionner un réparateur", "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // If proprietaire selected himself, create Reparateur if not exists
                Proprietaire proprietaire = (Proprietaire) AuthService.getCurrentUser();
                if (reparateur.getId() == proprietaire.getId()) {
                    Reparateur existing = gestionReparateur.findById(proprietaire.getId());
                    if (existing == null) {
                        Reparateur newReparateur = Reparateur.builder()
                                .nom(proprietaire.getNom())
                                .prenom(proprietaire.getPrenom())
                                .email(proprietaire.getEmail())
                                .password(proprietaire.getPassword())
                                .role("REPARATEUR")
                                .pourcentage(0)
                                .telephone("")
                                .build();
                        gestionReparateur.add(newReparateur);
                        reparateur = newReparateur;
                    } else {
                        reparateur = existing;
                    }
                }
            } else {
                UITheme.showStyledMessageDialog(dialog, "Accès non autorisé", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            List<Appareil> appareils = new ArrayList<>();
            int totalPieces = 0;

            // Process each appareil
            for (Component comp : appareilsPanel.getComponents()) {
                if (comp instanceof JPanel && ((JPanel) comp).getBorder() != null) {
                    JPanel appareilPanel = (JPanel) comp;
                    JTextField imeiField = (JTextField) appareilPanel.getClientProperty("imeiField");
                    JTextField couleurField = (JTextField) appareilPanel.getClientProperty("couleurField");
                    JTextField marqueField = (JTextField) appareilPanel.getClientProperty("marqueField");
                    JTextField modeleField = (JTextField) appareilPanel.getClientProperty("modeleField");
                    JTextField typeField = (JTextField) appareilPanel.getClientProperty("typeField");
                    JSpinner piecesSpinner = (JSpinner) appareilPanel.getClientProperty("piecesSpinner");
                    JPanel piecesPanel = (JPanel) appareilPanel.getClientProperty("piecesPanel");

                    if (imeiField.getText().trim().isEmpty() || marqueField.getText().trim().isEmpty()
                            || modeleField.getText().trim().isEmpty()) {
                        UITheme.showStyledMessageDialog(dialog,
                                "IMEI, Marque et Modèle sont obligatoires pour chaque appareil", "Erreur",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    List<Piece> pieces = new ArrayList<>();
                    for (Component pieceComp : piecesPanel.getComponents()) {
                        if (pieceComp instanceof JPanel) {
                            for (Component subComp : ((JPanel) pieceComp).getComponents()) {
                                if (subComp instanceof JTextField) {
                                    String pieceName = ((JTextField) subComp).getText().trim();
                                    if (!pieceName.isEmpty()) {
                                        Piece piece = Piece.builder().nomPiece(pieceName).build();
                                        pieces.add(piece);
                                    }
                                }
                            }
                        }
                    }

                    Appareil appareil = Appareil.builder()
                            .imei(imeiField.getText().trim())
                            .couleur(couleurField.getText().trim())
                            .marque(marqueField.getText().trim())
                            .modele(modeleField.getText().trim())
                            .typeAppareil(typeField.getText().trim())
                            .pieces(pieces)
                            .build();

                    appareils.add(appareil);
                    totalPieces += pieces.size();
                }
            }

            Reparation newReparation = Reparation.builder()
                    .codeReparation(codeField.getText().trim())
                    .statut((String) statutCombo.getSelectedItem())
                    .causeDeReparation(causeField.getText().trim())
                    .dateDeCreation(new Date())
                    .nombreDappareils(appareils.size())
                    .nombreDePiecesARaparer(totalPieces)
                    .prixTotal(prixTotal)
                    .appareils(appareils)
                    .reparateur(reparateur)
                    .caisse(reparateur.getCaisse())
                    .build();

            gestionReparation.add(newReparation);
            loadReparations();
            dialog.dispose();
            UITheme.showStyledMessageDialog(this, "Réparation créée avec succès!", "Succès",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            UITheme.showStyledMessageDialog(dialog, "Erreur: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editReparation() {
        int selectedRow = reparationTable.getSelectedRow();
        if (selectedRow == -1) {
            UITheme.showStyledMessageDialog(this, "Veuillez sélectionner une réparation", "Aucune sélection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int reparationId = (int) tableModel.getValueAt(selectedRow, 0);
        Reparation reparation = gestionReparation.findById(reparationId);

        if (reparation == null) {
            UITheme.showStyledMessageDialog(this, "Réparation non trouvée", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "✏️ Modifier Réparation", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(450, 350);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel codeLabel = new JLabel("Code:");
        JTextField codeField = new JTextField(reparation.getCodeReparation());
        JLabel statutLabel = new JLabel("Statut:");
        JComboBox<String> statutCombo = new JComboBox<>(
                new String[] { "En cours", "Terminée", "En attente", "Annulée" });
        statutCombo.setSelectedItem(reparation.getStatut());
        JLabel causeLabel = new JLabel("Cause:");
        JTextField causeField = new JTextField(reparation.getCauseDeReparation());
        JLabel prixLabel = new JLabel("Prix Total:");
        JTextField prixField = new JTextField(String.valueOf(reparation.getPrixTotal()));

        formPanel.add(codeLabel);
        formPanel.add(codeField);
        formPanel.add(statutLabel);
        formPanel.add(statutCombo);
        formPanel.add(causeLabel);
        formPanel.add(causeField);
        formPanel.add(prixLabel);
        formPanel.add(prixField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        RedButton saveButton = new RedButton("Enregistrer");
        RedButton cancelButton = new RedButton("Annuler");

        saveButton.addActionListener(e -> {
            if (codeField.getText().trim().isEmpty() || causeField.getText().trim().isEmpty()
                    || prixField.getText().trim().isEmpty()) {
                UITheme.showStyledMessageDialog(dialog, "Tous les champs sont obligatoires", "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                double prixTotal = Double.parseDouble(prixField.getText().trim());

                reparation.setCodeReparation(codeField.getText().trim());
                reparation.setStatut((String) statutCombo.getSelectedItem());
                reparation.setCauseDeReparation(causeField.getText().trim());
                reparation.setPrixTotal(prixTotal);
                gestionReparation.update(reparation);
                loadReparations();
                dialog.dispose();
            } catch (NumberFormatException ex) {
                UITheme.showStyledMessageDialog(dialog, "Le prix total doit être un nombre valide.", "Erreur",
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
    }

    private void deleteReparation() {
        int selectedRow = reparationTable.getSelectedRow();
        if (selectedRow == -1) {
            UITheme.showStyledMessageDialog(this, "Veuillez sélectionner une réparation", "Aucune sélection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int reparationId = (int) tableModel.getValueAt(selectedRow, 0);
        Reparation reparation = gestionReparation.findById(reparationId);

        if (reparation == null) {
            UITheme.showStyledMessageDialog(this, "Réparation non trouvée", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = UITheme.showStyledConfirmDialog(this,
                "Supprimer la réparation: " + reparation.getCodeReparation() + "?", "Confirmation");
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                gestionReparation.delete(reparation);
                loadReparations();
            } catch (Exception ex) {
                UITheme.showStyledMessageDialog(this, "Erreur de suppression: " + ex.getMessage(), "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void manageAppareils() {
        int selectedRow = reparationTable.getSelectedRow();
        if (selectedRow == -1) {
            UITheme.showStyledMessageDialog(this, "Veuillez sélectionner une réparation", "Aucune sélection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        UITheme.showStyledMessageDialog(this,
                "La gestion des appareils pour une réparation sera implémentée prochainement.", "Information",
                JOptionPane.INFORMATION_MESSAGE);
    }
}