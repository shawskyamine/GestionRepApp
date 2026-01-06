package presentation.ui.panels;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import presentation.ui.components.*;
import presentation.ui.utils.UITheme;
import presentation.ui.utils.Validator;
import metier.GestionClient;
import dao.Client;

public class ClientPanel extends JPanel {
    private GestionClient gestionClient = new GestionClient();
    private RedTable clientTable;
    private DefaultTableModel tableModel;
    private String[] columnNames = { "ID", "Nom", "Prénom", "Email", "Téléphone", "Rôle" };

    public ClientPanel() {
        setLayout(new BorderLayout());
        setBackground(UITheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        initComponents();
        loadRealData();
    }

    private void initComponents() {
        // Enhanced Header
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Top row: Title and CRUD buttons
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        topRow.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0)); // Space below

        JLabel title = new JLabel("Gestion des Clients");
        title.setFont(UITheme.getTitleFont());
        title.setForeground(UITheme.TEXT_PRIMARY);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));

        // Primary actions
        JPanel primaryActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        primaryActions.setOpaque(false);
        RedButton addButton = new RedButton("Nouveau Client");
        addButton.setPreferredSize(new Dimension(140, 38));
        addButton.addActionListener(e -> showAddDialog());
        primaryActions.add(addButton);

        // Secondary actions
        JPanel secondaryActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        secondaryActions.setOpaque(false);
        RedButton editButton = new RedButton("Modifier");
        editButton.setPreferredSize(new Dimension(120, 38));
        editButton.addActionListener(e -> editClient());
        RedButton deleteButton = new RedButton("Supprimer");
        deleteButton.setPreferredSize(new Dimension(130, 38));
        deleteButton.addActionListener(e -> deleteClient());
        RedButton refreshButton = new RedButton("Actualiser");
        refreshButton.setPreferredSize(new Dimension(130, 38));
        refreshButton.addActionListener(e -> loadRealData());

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

        // Bottom row: Enhanced Search Panel
        RedCard searchCard = new RedCard();
        searchCard.setLayout(new BorderLayout());
        searchCard.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        JPanel searchContentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        searchContentPanel.setOpaque(false);

        JLabel searchLabel = new JLabel("Rechercher:");
        searchLabel.setFont(UITheme.getLabelFont());
        searchLabel.setForeground(UITheme.TEXT_SECONDARY);
        
        JTextField searchField = new JTextField(25);
        searchField.setPreferredSize(new Dimension(380, 48));
        searchField.setFont(UITheme.getBodyFont().deriveFont(14f));
        searchField.setBorder(UITheme.createModernBorder(UITheme.BORDER_MEDIUM, 10));

        RedButton searchButton = new RedButton("Rechercher");
        searchButton.setPreferredSize(new Dimension(150, 48));
        RedButton clearButton = new RedButton("Effacer");
        clearButton.setPreferredSize(new Dimension(130, 48));

        searchButton.addActionListener(e -> searchClients(searchField.getText()));
        clearButton.addActionListener(e -> {
            searchField.setText("");
            loadRealData();
        });

        searchContentPanel.add(searchLabel);
        searchContentPanel.add(Box.createRigidArea(new Dimension(12, 0)));
        searchContentPanel.add(searchField);
        searchContentPanel.add(Box.createRigidArea(new Dimension(12, 0)));
        searchContentPanel.add(searchButton);
        searchContentPanel.add(Box.createRigidArea(new Dimension(8, 0)));
        searchContentPanel.add(clearButton);

        searchCard.add(searchContentPanel, BorderLayout.CENTER);

        // Add both rows to header
        headerPanel.add(topRow);
        headerPanel.add(searchCard);

        // Table
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        clientTable = new RedTable();
        clientTable.setModel(tableModel);

        JScrollPane tableScroll = new JScrollPane(clientTable);
        tableScroll.setBorder(BorderFactory.createEmptyBorder());
        tableScroll.setBackground(UITheme.BACKGROUND);

        // Assemble
        add(headerPanel, BorderLayout.NORTH);
        add(tableScroll, BorderLayout.CENTER);
    }

    private void loadRealData() {
        try {
            List<Client> clients = gestionClient.findAll();
            tableModel.setRowCount(0);

            if (clients != null) {
                for (Client client : clients) {
                    tableModel.addRow(new Object[] {
                            client.getId(),
                            client.getNom(),
                            client.getPrenom(),
                            client.getEmail(),
                            client.getTelephone(),
                            client.getRole()
                    });
                }

                if (clients.isEmpty()) {
                    // Optional: Show message if table is empty
                    System.out.println("Aucun client trouvé dans la base de données");
                }
            }
        } catch (Exception e) {
            UITheme.showStyledMessageDialog(this,
                    "Erreur de chargement des clients: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void showAddDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Nouveau Client", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(450, 350);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String[] labels = { "Nom:", "Prénom:", "Email:", "Mot de passe:", "Téléphone:" };
        JTextField[] fields = new JTextField[labels.length];
        JPasswordField passwordField = new JPasswordField();

        for (int i = 0; i < labels.length; i++) {
            JLabel label = new JLabel(labels[i]);
            label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            label.setAlignmentX(Component.LEFT_ALIGNMENT);

            if (i == 3) { // Password field
                passwordField.setMaximumSize(new Dimension(400, 35));
                formPanel.add(label);
                formPanel.add(Box.createRigidArea(new Dimension(0, 5)));
                formPanel.add(passwordField);
                formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
            } else {
                fields[i] = new JTextField();
                fields[i].setMaximumSize(new Dimension(400, 35));

                formPanel.add(label);
                formPanel.add(Box.createRigidArea(new Dimension(0, 5)));
                formPanel.add(fields[i]);
                formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
            }
        }

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        RedButton saveButton = new RedButton("Enregistrer");
        RedButton cancelButton = new RedButton("Annuler");

        saveButton.addActionListener(e -> {
            // Get field values
            String nom = fields[0].getText().trim();
            String prenom = fields[1].getText().trim();
            String email = fields[2].getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            String telephone = fields[4].getText().trim();

            // Validate fields
            String nameError = Validator.getNameError(nom);
            if (nameError != null) {
                UITheme.showStyledMessageDialog(dialog, nameError, "Erreur de validation", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String prenomError = Validator.getNameError(prenom);
            if (prenomError != null) {
                UITheme.showStyledMessageDialog(dialog, "Prénom: " + prenomError, "Erreur de validation",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            String emailError = Validator.getEmailError(email);
            if (emailError != null) {
                UITheme.showStyledMessageDialog(dialog, emailError, "Erreur de validation", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String passwordError = Validator.getPasswordError(password);
            if (passwordError != null) {
                UITheme.showStyledMessageDialog(dialog, passwordError, "Erreur de validation",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            String phoneError = Validator.getPhoneError(telephone);
            if (phoneError != null) {
                UITheme.showStyledMessageDialog(dialog, phoneError, "Erreur de validation", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                // Show loading dialog
                JDialog loadingDialog = UITheme.createLoadingDialog(dialog, "Enregistrement en cours...");
                SwingUtilities.invokeLater(() -> loadingDialog.setVisible(true));

                // Create real client in database
                Client newClient = Client.builder()
                        .nom(nom)
                        .prenom(prenom)
                        .email(email)
                        .password(password)
                        .role("CLIENT")
                        .telephone(telephone)
                        .build();

                Client savedClient = gestionClient.create(newClient);

                // Close loading dialog
                SwingUtilities.invokeLater(() -> loadingDialog.dispose());

                // Refresh table
                loadRealData();

                UITheme.showStyledMessageDialog(dialog,
                        "Client ajouté avec succès!<br>ID: " + savedClient.getId(),
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();

            } catch (Exception ex) {
                // Close loading dialog if open
                SwingUtilities.invokeLater(() -> {
                    Window[] windows = Window.getWindows();
                    for (Window window : windows) {
                        if (window instanceof JDialog && window.isVisible() &&
                                "Chargement...".equals(((JDialog) window).getTitle())) {
                            window.dispose();
                        }
                    }
                });

                UITheme.showStyledMessageDialog(dialog,
                        "Erreur d'ajout: " + ex.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(new JScrollPane(formPanel), BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void editClient() {
        int row = clientTable.getSelectedRow();
        if (row != -1) {
            int id = (int) tableModel.getValueAt(row, 0);

            try {
                // Get client from database
                Client client = gestionClient.findById(id);

                if (client == null) {
                    UITheme.showStyledMessageDialog(this,
                            "Client non trouvé dans la base de données",
                            "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Create edit dialog
                JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                        "Modifier Client", true);
                dialog.setLayout(new BorderLayout());
                dialog.setSize(450, 350);
                dialog.setLocationRelativeTo(this);

                JPanel formPanel = new JPanel();
                formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
                formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

                String[] labels = { "Nom:", "Prénom:", "Email:", "Mot de passe:", "Téléphone:" };
                JTextField[] fields = new JTextField[labels.length];
                JPasswordField passwordField = new JPasswordField();
                String[] values = { client.getNom(), client.getPrenom(), client.getEmail(), client.getPassword(),
                        client.getTelephone() };

                for (int i = 0; i < labels.length; i++) {
                    JLabel label = new JLabel(labels[i]);
                    label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                    label.setAlignmentX(Component.LEFT_ALIGNMENT);

                    if (i == 3) { // Password field
                        passwordField.setText(values[i]);
                        passwordField.setMaximumSize(new Dimension(400, 35));
                        formPanel.add(label);
                        formPanel.add(Box.createRigidArea(new Dimension(0, 5)));
                        formPanel.add(passwordField);
                        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
                    } else {
                        fields[i] = new JTextField(values[i]);
                        fields[i].setMaximumSize(new Dimension(400, 35));

                        formPanel.add(label);
                        formPanel.add(Box.createRigidArea(new Dimension(0, 5)));
                        formPanel.add(fields[i]);
                        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
                    }
                }

                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                RedButton saveButton = new RedButton("💾 Enregistrer");
                RedButton cancelButton = new RedButton("Annuler");

                saveButton.addActionListener(e -> {
                    try {
                        // Validate fields before updating
                        String nom = fields[0].getText().trim();
                        String prenom = fields[1].getText().trim();
                        String email = fields[2].getText().trim();
                        String password = new String(passwordField.getPassword()).trim();
                        String telephone = fields[4].getText().trim();

                        // Validate
                        String nameError = Validator.getNameError(nom);
                        if (nameError != null) {
                            UITheme.showStyledMessageDialog(dialog, nameError, "Erreur de validation", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        String prenomError = Validator.getNameError(prenom);
                        if (prenomError != null) {
                            UITheme.showStyledMessageDialog(dialog, "Prénom: " + prenomError, "Erreur de validation", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        String emailError = Validator.getEmailError(email);
                        if (emailError != null) {
                            UITheme.showStyledMessageDialog(dialog, emailError, "Erreur de validation", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        String passwordError = Validator.getPasswordError(password);
                        if (passwordError != null) {
                            UITheme.showStyledMessageDialog(dialog, passwordError, "Erreur de validation", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        String phoneError = Validator.getPhoneError(telephone);
                        if (phoneError != null) {
                            UITheme.showStyledMessageDialog(dialog, phoneError, "Erreur de validation", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        // Update client
                        client.setNom(nom);
                        client.setPrenom(prenom);
                        client.setEmail(email);
                        client.setPassword(password);
                        client.setTelephone(telephone);

                        gestionClient.update(client);

                        // Refresh table
                        loadRealData();

                        UITheme.showStyledMessageDialog(dialog,
                                "Client modifié avec succès!",
                                "Succès", JOptionPane.INFORMATION_MESSAGE);
                        dialog.dispose();

                    } catch (Exception ex) {
                        UITheme.showStyledMessageDialog(dialog,
                                "Erreur de modification: " + ex.getMessage(),
                                "Erreur", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                });

                cancelButton.addActionListener(e -> dialog.dispose());

                buttonPanel.add(saveButton);
                buttonPanel.add(cancelButton);

                dialog.add(new JScrollPane(formPanel), BorderLayout.CENTER);
                dialog.add(buttonPanel, BorderLayout.SOUTH);

                dialog.setVisible(true);

            } catch (Exception e) {
                UITheme.showStyledMessageDialog(this,
                        "Erreur: " + e.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }

        } else {
            UITheme.showStyledMessageDialog(this,
                    "Veuillez sélectionner un client",
                    "Aucune sélection", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deleteClient() {
        int row = clientTable.getSelectedRow();
        if (row != -1) {
            int id = (int) tableModel.getValueAt(row, 0);
            String name = tableModel.getValueAt(row, 1) + " " + tableModel.getValueAt(row, 2);

            int confirm = UITheme.showStyledConfirmDialog(this,
                    "Supprimer le client: " + name + "?<br>(ID: " + id + ")",
                    "Confirmation");

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    // Delete from database
                    boolean success = gestionClient.delete(id);

                    if (success) {
                        // Remove from table
                        tableModel.removeRow(row);
                        UITheme.showStyledMessageDialog(this,
                                "Client supprimé avec succès!",
                                "Succès", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        UITheme.showStyledMessageDialog(this,
                                "Échec de suppression du client",
                                "Erreur", JOptionPane.ERROR_MESSAGE);
                    }

                } catch (Exception e) {
                    UITheme.showStyledMessageDialog(this,
                            "Erreur de suppression: " + e.getMessage(),
                            "Erreur", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
        } else {
            UITheme.showStyledMessageDialog(this,
                    "Veuillez sélectionner un client",
                    "Aucune sélection", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void searchClients(String query) {
        if (query == null || query.trim().isEmpty()) {
            loadRealData();
            return;
        }

        query = query.trim().toLowerCase();

        // Create new table model with same columns
        DefaultTableModel tempModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Search through all rows
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            boolean found = false;

            // Check each column in the row
            for (int j = 0; j < tableModel.getColumnCount(); j++) {
                Object value = tableModel.getValueAt(i, j);
                if (value != null && value.toString().toLowerCase().contains(query)) {
                    found = true;
                    break;
                }
            }

            // If found, add the entire row to temp model
            if (found) {
                Object[] rowData = new Object[tableModel.getColumnCount()];
                for (int j = 0; j < tableModel.getColumnCount(); j++) {
                    rowData[j] = tableModel.getValueAt(i, j);
                }
                tempModel.addRow(rowData);
            }
        }

        // Update the table with search results
        clientTable.setModel(tempModel);

        // If no results found
        if (tempModel.getRowCount() == 0) {
            UITheme.showStyledMessageDialog(this,
                    "Aucun client trouvé pour: \"" + query + "\"",
                    "Aucun résultat", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}