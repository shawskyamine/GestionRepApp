package presentation.ui.panels;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import presentation.ui.components.*;
import presentation.ui.utils.UITheme;
import metier.GestionReparateur;
import dao.Reparateur;

public class ReparateurPanel extends JPanel {
    private GestionReparateur gestionReparateur;
    private RedTable reparateurTable;
    private DefaultTableModel tableModel;
    private String[] columnNames = { "ID", "Nom", "Prénom", "Email", "Téléphone", "Pourcentage" };

    public ReparateurPanel() {
        gestionReparateur = new GestionReparateur();
        setLayout(new BorderLayout());
        setBackground(UITheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        initComponents();
        loadReparateurs();
    }

    private void initComponents() {
        // Enhanced Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel title = new JLabel("Gestion des Réparateurs");
        title.setFont(UITheme.getTitleFont().deriveFont(32f));
        title.setForeground(UITheme.TEXT_PRIMARY);

        // Primary actions (left side)
        JPanel primaryActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        primaryActions.setOpaque(false);
        RedButton addButton = new RedButton("Nouveau Réparateur");
        addButton.setPreferredSize(new Dimension(190, 48));
        addButton.addActionListener(e -> showAddDialog());
        primaryActions.add(addButton);

        // Secondary actions (right side)
        JPanel secondaryActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        secondaryActions.setOpaque(false);

        RedButton editButton = new RedButton("Modifier");
        editButton.setPreferredSize(new Dimension(140, 48));
        editButton.addActionListener(e -> editReparateur());

        RedButton deleteButton = new RedButton("Supprimer");
        deleteButton.setPreferredSize(new Dimension(140, 48));
        deleteButton.addActionListener(e -> deleteReparateur());

        RedButton refreshButton = new RedButton("Actualiser");
        refreshButton.setPreferredSize(new Dimension(140, 48));
        refreshButton.addActionListener(e -> loadReparateurs());

        secondaryActions.add(editButton);
        secondaryActions.add(deleteButton);
        secondaryActions.add(refreshButton);

        RedButton caisseButton = new RedButton("Caisse");
        caisseButton.setPreferredSize(new Dimension(120, 48));
        caisseButton.addActionListener(e -> openCaisse());
        secondaryActions.add(caisseButton);

        JPanel buttonContainer = new JPanel(new BorderLayout());
        buttonContainer.setOpaque(false);
        buttonContainer.add(primaryActions, BorderLayout.WEST);
        buttonContainer.add(secondaryActions, BorderLayout.EAST);

        headerPanel.add(title, BorderLayout.WEST);
        headerPanel.add(buttonContainer, BorderLayout.CENTER);

        // Table
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        reparateurTable = new RedTable();
        reparateurTable.setModel(tableModel);

        RedCard tableCard = new RedCard();
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        JScrollPane tableScroll = new JScrollPane(reparateurTable);
        tableScroll.setBorder(BorderFactory.createEmptyBorder());
        tableScroll.setBackground(UITheme.BACKGROUND);
        tableCard.add(tableScroll, BorderLayout.CENTER);

        // Assemble
        add(headerPanel, BorderLayout.NORTH);
        add(tableCard, BorderLayout.CENTER);
    }

    private void loadReparateurs() {
        tableModel.setRowCount(0);
        List<Reparateur> reparateurs = gestionReparateur.findAll();
        if (reparateurs != null) {
            for (Reparateur reparateur : reparateurs) {
                tableModel.addRow(new Object[] {
                        reparateur.getId(),
                        reparateur.getNom(),
                        reparateur.getPrenom(),
                        reparateur.getEmail(),
                        reparateur.getTelephone(),
                        reparateur.getPourcentage() + "%"
                });
            }
        }
    }

    private void showAddDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Nouveau Réparateur", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(540, 480);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(UITheme.BG_SECONDARY);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(UITheme.BG_SECONDARY);
        formPanel.setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 16, 16);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Form fields
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        JLabel nomLabel = new JLabel("Nom:");
        nomLabel.setFont(UITheme.getLabelFont());
        nomLabel.setForeground(UITheme.TEXT_SECONDARY);
        formPanel.add(nomLabel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JTextField nomField = new JTextField(25);
        nomField.setPreferredSize(new Dimension(0, 38));
        formPanel.add(nomField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        JLabel prenomLabel = new JLabel("Prénom:");
        prenomLabel.setFont(UITheme.getLabelFont());
        prenomLabel.setForeground(UITheme.TEXT_SECONDARY);
        formPanel.add(prenomLabel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JTextField prenomField = new JTextField(25);
        prenomField.setPreferredSize(new Dimension(0, 38));
        formPanel.add(prenomField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.3;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(UITheme.getLabelFont());
        emailLabel.setForeground(UITheme.TEXT_SECONDARY);
        formPanel.add(emailLabel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JTextField emailField = new JTextField(25);
        emailField.setPreferredSize(new Dimension(0, 38));
        formPanel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.3;
        JLabel telLabel = new JLabel("Téléphone:");
        telLabel.setFont(UITheme.getLabelFont());
        telLabel.setForeground(UITheme.TEXT_SECONDARY);
        formPanel.add(telLabel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JTextField telephoneField = new JTextField(25);
        telephoneField.setPreferredSize(new Dimension(0, 38));
        formPanel.add(telephoneField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.3;
        JLabel pourcLabel = new JLabel("Pourcentage (%):");
        pourcLabel.setFont(UITheme.getLabelFont());
        pourcLabel.setForeground(UITheme.TEXT_SECONDARY);
        formPanel.add(pourcLabel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JSpinner pourcentageSpinner = new JSpinner(new SpinnerNumberModel(10, 0, 100, 1));
        pourcentageSpinner.setPreferredSize(new Dimension(0, 38));
        formPanel.add(pourcentageSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 0.3;
        JLabel passLabel = new JLabel("Mot de passe:");
        passLabel.setFont(UITheme.getLabelFont());
        passLabel.setForeground(UITheme.TEXT_SECONDARY);
        formPanel.add(passLabel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JPasswordField passwordField = new JPasswordField(25);
        passwordField.setPreferredSize(new Dimension(0, 38));
        formPanel.add(passwordField, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        RedButton saveButton = new RedButton("Enregistrer");
        RedButton cancelButton = new RedButton("Annuler");

        saveButton.addActionListener(e -> {
            if (nomField.getText().trim().isEmpty() || prenomField.getText().trim().isEmpty() ||
                    emailField.getText().trim().isEmpty() || passwordField.getPassword().length == 0) {
                UITheme.showStyledMessageDialog(dialog, "Tous les champs sont obligatoires", "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                Reparateur reparateur = Reparateur.builder()
                        .nom(nomField.getText().trim())
                        .prenom(prenomField.getText().trim())
                        .email(emailField.getText().trim())
                        .password(new String(passwordField.getPassword()))
                        .role("REPARATEUR")
                        .telephone(telephoneField.getText().trim())
                        .pourcentage((Integer) pourcentageSpinner.getValue())
                        .build();

                gestionReparateur.add(reparateur);
                loadReparateurs();
                dialog.dispose();
                UITheme.showStyledMessageDialog(this, "Réparateur créé avec succès!", "Succès",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                UITheme.showStyledMessageDialog(dialog, "Erreur: " + ex.getMessage(), "Erreur",
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

    private void editReparateur() {
        int selectedRow = reparateurTable.getSelectedRow();
        if (selectedRow == -1) {
            UITheme.showStyledMessageDialog(this, "Veuillez sélectionner un réparateur", "Aucune sélection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int reparateurId = (int) tableModel.getValueAt(selectedRow, 0);
        Reparateur reparateur = gestionReparateur.findById(reparateurId);

        if (reparateur == null) {
            UITheme.showStyledMessageDialog(this, "Réparateur non trouvé", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "✏️ Modifier Réparateur", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(520, 420);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(UITheme.BG_SECONDARY);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(UITheme.BG_SECONDARY);
        formPanel.setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 16, 16);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Form fields with current values
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        JLabel nomLabel = new JLabel("Nom:");
        nomLabel.setFont(UITheme.getLabelFont());
        nomLabel.setForeground(UITheme.TEXT_SECONDARY);
        formPanel.add(nomLabel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JTextField nomField = new JTextField(reparateur.getNom(), 25);
        nomField.setPreferredSize(new Dimension(0, 38));
        formPanel.add(nomField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        JLabel prenomLabel = new JLabel("Prénom:");
        prenomLabel.setFont(UITheme.getLabelFont());
        prenomLabel.setForeground(UITheme.TEXT_SECONDARY);
        formPanel.add(prenomLabel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JTextField prenomField = new JTextField(reparateur.getPrenom(), 25);
        prenomField.setPreferredSize(new Dimension(0, 38));
        formPanel.add(prenomField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.3;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(UITheme.getLabelFont());
        emailLabel.setForeground(UITheme.TEXT_SECONDARY);
        formPanel.add(emailLabel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JTextField emailField = new JTextField(reparateur.getEmail(), 25);
        emailField.setPreferredSize(new Dimension(0, 38));
        formPanel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.3;
        JLabel telLabel = new JLabel("Téléphone:");
        telLabel.setFont(UITheme.getLabelFont());
        telLabel.setForeground(UITheme.TEXT_SECONDARY);
        formPanel.add(telLabel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JTextField telephoneField = new JTextField(reparateur.getTelephone(), 25);
        telephoneField.setPreferredSize(new Dimension(0, 38));
        formPanel.add(telephoneField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.3;
        JLabel pourcLabel = new JLabel("Pourcentage (%):");
        pourcLabel.setFont(UITheme.getLabelFont());
        pourcLabel.setForeground(UITheme.TEXT_SECONDARY);
        formPanel.add(pourcLabel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JSpinner pourcentageSpinner = new JSpinner(new SpinnerNumberModel(reparateur.getPourcentage(), 0, 100, 1));
        pourcentageSpinner.setPreferredSize(new Dimension(0, 38));
        formPanel.add(pourcentageSpinner, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        buttonPanel.setBackground(UITheme.BG_SECONDARY);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(16, 32, 0, 32));
        RedButton saveButton = new RedButton("Enregistrer");
        saveButton.setPreferredSize(new Dimension(140, 38));
        RedButton cancelButton = new RedButton("Annuler");
        cancelButton.setPreferredSize(new Dimension(120, 38));
        cancelButton.setBackground(UITheme.SURFACE);
        cancelButton.setForeground(UITheme.TEXT_SECONDARY);

        saveButton.addActionListener(e -> {
            if (nomField.getText().trim().isEmpty() || prenomField.getText().trim().isEmpty() ||
                    emailField.getText().trim().isEmpty()) {
                UITheme.showStyledMessageDialog(dialog, "Tous les champs sont obligatoires", "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                reparateur.setNom(nomField.getText().trim());
                reparateur.setPrenom(prenomField.getText().trim());
                reparateur.setEmail(emailField.getText().trim());
                reparateur.setTelephone(telephoneField.getText().trim());
                reparateur.setPourcentage((Integer) pourcentageSpinner.getValue());

                gestionReparateur.update(reparateur);
                loadReparateurs();
                dialog.dispose();
                UITheme.showStyledMessageDialog(this, "Réparateur modifié avec succès!", "Succès",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                UITheme.showStyledMessageDialog(dialog, "Erreur: " + ex.getMessage(), "Erreur",
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

    private void deleteReparateur() {
        int selectedRow = reparateurTable.getSelectedRow();
        if (selectedRow == -1) {
            UITheme.showStyledMessageDialog(this, "Veuillez sélectionner un réparateur", "Aucune sélection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = UITheme.showStyledConfirmDialog(this,
                "Êtes-vous sûr de vouloir supprimer ce réparateur?",
                "Confirmation de suppression");

        if (confirm == JOptionPane.YES_OPTION) {
            int reparateurId = (int) tableModel.getValueAt(selectedRow, 0);
            Reparateur reparateur = gestionReparateur.findById(reparateurId);

            if (reparateur != null) {
                try {
                    gestionReparateur.delete(reparateur);
                    loadReparateurs();
                    UITheme.showStyledMessageDialog(this, "Réparateur supprimé avec succès!", "Succès",
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    UITheme.showStyledMessageDialog(this, "Erreur lors de la suppression: " + ex.getMessage(), "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void openCaisse() {
        JFrame caisseFrame = new JFrame("Caisse");
        caisseFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        caisseFrame.setSize(1000, 700);
        caisseFrame.setLocationRelativeTo(this);
        caisseFrame.add(new CaissePanel());
        caisseFrame.setVisible(true);
    }
}