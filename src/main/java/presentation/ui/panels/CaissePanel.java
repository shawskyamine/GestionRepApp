package presentation.ui.panels;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;
import metier.GestionCaisse;
import metier.GestionReparateur;
import dao.Caisse;
import dao.Reparateur;
import dao.Reparation;
import dao.Emprunt;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import presentation.ui.utils.AuthService;
import presentation.ui.utils.UITheme;
import presentation.ui.components.RedTable;
import presentation.ui.components.RedButton;
import exception.DatabaseException;
import exception.ValidationException;
import exception.InvalidInputException;
import exception.AuthorizationException;
import exception.EntityNotFoundException;
import exception.InsufficientFundsException;
import java.util.Date;

public class CaissePanel extends JPanel {
    private GestionCaisse gestionCaisse;
    private RedTable transactionTable;
    private RedTable loansTakenTable;
    private RedTable loansGivenTable;
    private DefaultTableModel tableModel;
    private DefaultTableModel takenTableModel;
    private DefaultTableModel givenTableModel;
    private JLabel balanceLabel;
    private String[] columnNames = { "Date", "Description", "Type", "Montant" };
    private JTabbedPane tabbedPane;
    
    // Statistics labels
    private JLabel prisValueLabel;
    private JLabel donnesValueLabel;
    private JLabel enCoursValueLabel;
    private JLabel enRetardValueLabel;

    public CaissePanel() {
        gestionCaisse = new GestionCaisse();
        setLayout(new BorderLayout());
        setBackground(UITheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        initComponents();
        try {
            loadCaisseData();
        } catch (DatabaseException | AuthorizationException e) {
            balanceLabel.setText("Erreur: " + e.getMessage());
            tableModel.setRowCount(0);
        }
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

        JLabel title = new JLabel("Gestion de la Caisse");
        title.setFont(UITheme.getTitleFont());
        title.setForeground(UITheme.TEXT_PRIMARY);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));

        // Primary actions
        JPanel primaryActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        primaryActions.setOpaque(false);
        RedButton addButton = new RedButton("Nouvelle Transaction");
        addButton.setPreferredSize(new Dimension(170, 42));
        addButton.addActionListener(e -> showAddTransactionDialog());
        primaryActions.add(addButton);
        
        RedButton prendreEmpruntBtn = new RedButton("Prendre Emprunt");
        prendreEmpruntBtn.setPreferredSize(new Dimension(150, 42));
        prendreEmpruntBtn.addActionListener(e -> showTakeLoanDialog());
        primaryActions.add(prendreEmpruntBtn);
        
        RedButton donnerEmpruntBtn = new RedButton("Donner Emprunt");
        donnerEmpruntBtn.setPreferredSize(new Dimension(150, 42));
        donnerEmpruntBtn.addActionListener(e -> showGiveLoanDialog());
        primaryActions.add(donnerEmpruntBtn);

        // Secondary actions
        JPanel secondaryActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        secondaryActions.setOpaque(false);

        RedButton refreshButton = new RedButton("Actualiser");
        refreshButton.setPreferredSize(new Dimension(150, 42));
        refreshButton.addActionListener(e -> {
            try {
                loadCaisseData();
                loadLoanData();
            } catch (DatabaseException | AuthorizationException ex) {
                UITheme.showStyledMessageDialog(this, "Erreur lors de l'actualisation: " + ex.getMessage(), "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        secondaryActions.add(refreshButton);

        JPanel buttonContainer = new JPanel(new BorderLayout());
        buttonContainer.setOpaque(false);
        buttonContainer.add(primaryActions, BorderLayout.WEST);
        buttonContainer.add(secondaryActions, BorderLayout.EAST);

        topRow.add(title, BorderLayout.WEST);
        topRow.add(buttonContainer, BorderLayout.CENTER);

        // Bottom row: Balance display
        JPanel bottomRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        bottomRow.setOpaque(false);
        balanceLabel = new JLabel("Solde: 0.00 MAD");
        balanceLabel.setFont(UITheme.getHeadingFont());
        balanceLabel.setForeground(UITheme.PRIMARY);
        bottomRow.add(balanceLabel);

        // Add both rows to header
        headerPanel.add(topRow);
        headerPanel.add(bottomRow);

        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(UITheme.BACKGROUND);
        tabbedPane.setForeground(UITheme.TEXT_PRIMARY);
        
        // Tab 1: All Transactions
        JPanel transactionsPanel = createTransactionsPanel();
        
        // Tab 2: Loans Taken
        JPanel loansTakenPanel = createLoansTakenPanel();
        
        // Tab 3: Loans Given
        JPanel loansGivenPanel = createLoansGivenPanel();
        
        // Tab 4: Loan Management
        JPanel manageLoansPanel = createManageLoansPanel();
        
        tabbedPane.addTab("Toutes Transactions", transactionsPanel);
        tabbedPane.addTab("Emprunts Pris", loansTakenPanel);
        tabbedPane.addTab("Emprunts Donnés", loansGivenPanel);
        tabbedPane.addTab("Gérer Emprunts", manageLoansPanel);

        // Assemble
        add(headerPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private JPanel createTransactionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BACKGROUND);
        
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        transactionTable = new RedTable();
        transactionTable.setModel(tableModel);

        JScrollPane tableScroll = new JScrollPane(transactionTable);
        tableScroll.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_MEDIUM, 1),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        tableScroll.setBackground(UITheme.BACKGROUND);
        
        panel.add(tableScroll, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createLoansTakenPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BACKGROUND);
        
        String[] columns = {"ID", "Date", "Échéance", "Partenaire", "Montant", "État", "Description"};
        takenTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        loansTakenTable = new RedTable();
        loansTakenTable.setModel(takenTableModel);

        JScrollPane scrollPane = new JScrollPane(loansTakenTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_MEDIUM, 1),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createLoansGivenPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BACKGROUND);
        
        String[] columns = {"ID", "Date", "Échéance", "Partenaire", "Montant", "État", "Description"};
        givenTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        loansGivenTable = new RedTable();
        loansGivenTable.setModel(givenTableModel);

        JScrollPane scrollPane = new JScrollPane(loansGivenTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_MEDIUM, 1),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createManageLoansPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(UITheme.BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header
        JLabel header = new JLabel("Gestion des Emprunts");
        header.setFont(UITheme.getHeadingFont());
        header.setForeground(UITheme.PRIMARY);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setBackground(UITheme.BACKGROUND);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        RedButton prendreEmpruntBtn = new RedButton("Prendre un Emprunt");
        prendreEmpruntBtn.addActionListener(e -> showTakeLoanDialog());
        
        RedButton donnerEmpruntBtn = new RedButton("Donner un Emprunt");
        donnerEmpruntBtn.addActionListener(e -> showGiveLoanDialog());
        
        RedButton rembourserBtn = new RedButton("Rembourser");
        rembourserBtn.addActionListener(e -> showRepayDialog());
        
        RedButton refreshBtn = new RedButton("Actualiser");
        refreshBtn.addActionListener(e -> loadLoanData());
        
        buttonPanel.add(prendreEmpruntBtn);
        buttonPanel.add(donnerEmpruntBtn);
        buttonPanel.add(rembourserBtn);
        buttonPanel.add(refreshBtn);
        
        // Stats Panel
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        statsPanel.setBackground(UITheme.BACKGROUND);
        statsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_MEDIUM), "Statistiques"));
        statsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel prisLabel = new JLabel("Total Emprunts Pris:");
        prisValueLabel = new JLabel("0.00 MAD");
        
        JLabel donnesLabel = new JLabel("Total Emprunts Donnés:");
        donnesValueLabel = new JLabel("0.00 MAD");
        
        JLabel enCoursLabel = new JLabel("Emprunts en Cours:");
        enCoursValueLabel = new JLabel("0");
        
        JLabel enRetardLabel = new JLabel("Emprunts en Retard:");
        enRetardValueLabel = new JLabel("0");
        
        statsPanel.add(prisLabel);
        statsPanel.add(prisValueLabel);
        statsPanel.add(donnesLabel);
        statsPanel.add(donnesValueLabel);
        statsPanel.add(enCoursLabel);
        statsPanel.add(enCoursValueLabel);
        statsPanel.add(enRetardLabel);
        statsPanel.add(enRetardValueLabel);
        
        panel.add(header);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(buttonPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(statsPanel);
        
        return panel;
    }

    private void loadCaisseData() throws DatabaseException, AuthorizationException {
        // Check if current user is reparateur or proprietaire
        if (!AuthService.isReparateur() && !AuthService.isProprietaire()) {
            throw new AuthorizationException("Accès non autorisé - Réservé aux réparateurs et propriétaires");
        }

        if (AuthService.isProprietaire()) {
            // For proprietaire, show all transactions from all caisses
            balanceLabel.setText("Propriétaire - Vue générale des caisses");
            tableModel.setRowCount(0);
            
            try {
                List<Object[]> allTransactions = getAllTransactions();
                if (allTransactions != null && !allTransactions.isEmpty()) {
                    for (Object[] transaction : allTransactions) {
                        tableModel.addRow(transaction);
                    }
                } else {
                    tableModel.addRow(new Object[] { "Aucune donnée", "", "", "" });
                }
            } catch (DatabaseException e) {
                tableModel.addRow(new Object[] { "Erreur de chargement", "", "", "" });
            }
            return;
        }

        // For reparateur
        int userId = AuthService.getUserId();
        GestionReparateur gestionReparateur = new GestionReparateur();
        Reparateur reparateur;
        try {
            reparateur = gestionReparateur.findById(userId);
        } catch (DatabaseException | EntityNotFoundException e) {
            balanceLabel.setText("Erreur: " + e.getMessage());
            tableModel.setRowCount(0);
            return;
        }

        if (reparateur.getCaisse() == null) {
            // Create caisse for existing reparateur
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("ClientUP");
            EntityManager em = emf.createEntityManager();
            EntityTransaction tr = em.getTransaction();
            try {
                tr.begin();
                Caisse caisse = Caisse.builder()
                        .reparateur(reparateur)
                        .build();
                em.persist(caisse);
                reparateur.setCaisse(caisse);
                em.merge(reparateur);
                tr.commit();
                System.out.println("Created caisse for reparateur: " + reparateur.getEmail());
            } catch (Exception ex) {
                if (tr.isActive()) {
                    tr.rollback();
                }
                ex.printStackTrace();
                balanceLabel.setText("Erreur lors de la création de la caisse");
                tableModel.setRowCount(0);
                return;
            } finally {
                if (em.isOpen()) {
                    em.close();
                }
                if (emf.isOpen()) {
                    emf.close();
                }
            }
        }

        int caisseId = reparateur.getCaisse().getId();

        try {
            double balance = gestionCaisse.getSolde(caisseId);
            balanceLabel.setText(String.format("Solde: %.2f MAD", balance));

            tableModel.setRowCount(0);
            List<Object[]> transactions = getTransactions(caisseId);
            if (transactions != null && !transactions.isEmpty()) {
                for (Object[] transaction : transactions) {
                    tableModel.addRow(transaction);
                }
            } else {
                tableModel.addRow(new Object[] { "Aucune transaction", "", "", "" });
            }
            
            // Load loan data
            loadLoanData();
            
        } catch (DatabaseException e) {
            balanceLabel.setText("Erreur de chargement");
            e.printStackTrace();
        }
    }
    
    private void loadLoanData() {
        try {
            if (!AuthService.isReparateur()) {
                return; // Only for reparateurs
            }
            
            int userId = AuthService.getUserId();
            GestionReparateur gestionReparateur = new GestionReparateur();
            Reparateur reparateur = gestionReparateur.findById(userId);
            
            if (reparateur.getCaisse() != null) {
                int caisseId = reparateur.getCaisse().getId();
                
                // Load loans taken
                List<Emprunt> pris = gestionCaisse.getEmpruntsPris(caisseId);
                takenTableModel.setRowCount(0);
                for (Emprunt e : pris) {
                    String partner = e.isEstExterne() ? e.getNomExterne() : 
                                     e.getNomPartenaire() + " (" + e.getRolePartenaire() + ")";
                    takenTableModel.addRow(new Object[]{
                        e.getId(),
                        e.getDateEmprunt(),
                        e.getDateDeRetour(),
                        partner,
                        String.format("%.2f MAD", e.getMontant()),
                        e.getEtat(),
                        e.getDescription()
                    });
                }
                
                // Load loans given
                List<Emprunt> donnes = gestionCaisse.getEmpruntsDonnes(caisseId);
                givenTableModel.setRowCount(0);
                for (Emprunt e : donnes) {
                    String partner = e.isEstExterne() ? e.getNomExterne() : 
                                     e.getNomPartenaire() + " (" + e.getRolePartenaire() + ")";
                    givenTableModel.addRow(new Object[]{
                        e.getId(),
                        e.getDateEmprunt(),
                        e.getDateDeRetour(),
                        partner,
                        String.format("%.2f MAD", e.getMontant()),
                        e.getEtat(),
                        e.getDescription()
                    });
                }
                
                // Update statistics
                double totalPris = gestionCaisse.getTotalEmpruntsPris(caisseId);
                double totalDonnes = gestionCaisse.getTotalEmpruntsDonnes(caisseId);
                List<Emprunt> enCours = gestionCaisse.getEmpruntsEnCours(caisseId);
                List<Emprunt> enRetard = gestionCaisse.getEmpruntsEnRetard(caisseId);
                
                prisValueLabel.setText(String.format("%.2f MAD", totalPris));
                donnesValueLabel.setText(String.format("%.2f MAD", totalDonnes));
                enCoursValueLabel.setText(String.valueOf(enCours.size()));
                enRetardValueLabel.setText(String.valueOf(enRetard.size()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            UITheme.showStyledMessageDialog(this, "Erreur lors du chargement des emprunts: " + e.getMessage(), 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private List<Object[]> getTransactions(int caisseId) throws DatabaseException {
        List<Object[]> transactions = new ArrayList<>();

        // Get réparations (credits)
        List<Reparation> reparations = gestionCaisse.getReparationsByCaisse(caisseId);
        if (reparations != null) {
            for (Reparation r : reparations) {
                transactions.add(new Object[] {
                    r.getDateDeCreation(),
                    "Réparation " + r.getCodeReparation(),
                    "Crédit",
                    String.format("%.2f MAD", r.getPrixTotal())
                });
            }
        }

        // Get emprunts (debits)
        List<Emprunt> emprunts = gestionCaisse.getEmpruntsByCaisse(caisseId);
        if (emprunts != null) {
            for (Emprunt e : emprunts) {
                String type = "Débit";
                if ("EMPRUNT_PRIS".equals(e.getTypeTransaction())) {
                    type = "Emprunt Pris";
                } else if ("EMPRUNT_DONNE".equals(e.getTypeTransaction())) {
                    type = "Emprunt Donné";
                }
                
                String description = "Emprunt #" + e.getId();
                if (e.getDescription() != null && !e.getDescription().isEmpty()) {
                    description = e.getDescription();
                }
                
                transactions.add(new Object[] {
                    e.getDateEmprunt(),
                    description,
                    type,
                    String.format("%.2f MAD", e.getMontant())
                });
            }
        }

        // Sort by date (newest first)
        transactions.sort((t1, t2) -> {
            Date date1 = (Date) t1[0];
            Date date2 = (Date) t2[0];
            return date2.compareTo(date1); // Reverse order for newest first
        });

        return transactions;
    }

    private List<Object[]> getAllTransactions() throws DatabaseException {
        List<Object[]> transactions = new ArrayList<>();

        try {
            // Get all reparations
            List<Reparation> reparations = gestionCaisse.getAllReparations();
            if (reparations != null) {
                for (Reparation r : reparations) {
                    String reparateurName = (r.getReparateur() != null) ? r.getReparateur().getNom() : "Inconnu";
                    transactions.add(new Object[] {
                        r.getDateDeCreation(),
                        "Réparation " + r.getCodeReparation() + " - " + reparateurName,
                        "Crédit",
                        String.format("%.2f MAD", r.getPrixTotal())
                    });
                }
            }

            // Get all emprunts
            List<Emprunt> emprunts = gestionCaisse.getAllEmprunts();
            if (emprunts != null) {
                for (Emprunt e : emprunts) {
                    String reparateurName = "Inconnu";
                    if (e.getCaisse() != null && e.getCaisse().getReparateur() != null) {
                        reparateurName = e.getCaisse().getReparateur().getNom();
                    }
                    
                    String type = "Débit";
                    if ("EMPRUNT_PRIS".equals(e.getTypeTransaction())) {
                        type = "Emprunt Pris";
                    } else if ("EMPRUNT_DONNE".equals(e.getTypeTransaction())) {
                        type = "Emprunt Donné";
                    }
                    
                    String description = "Emprunt #" + e.getId() + " - " + reparateurName;
                    if (e.getDescription() != null && !e.getDescription().isEmpty()) {
                        description = e.getDescription() + " - " + reparateurName;
                    }
                    
                    transactions.add(new Object[] {
                        e.getDateEmprunt(),
                        description,
                        type,
                        String.format("%.2f MAD", e.getMontant())
                    });
                }
            }

            // Sort by date (newest first)
            transactions.sort((t1, t2) -> {
                Date date1 = (Date) t1[0];
                Date date2 = (Date) t2[0];
                return date2.compareTo(date1); // Reverse order for newest first
            });

        } catch (Exception e) {
            throw new DatabaseException("Erreur lors du chargement des transactions: " + e.getMessage(), e);
        }

        return transactions;
    }

    private void showAddTransactionDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Nouvelle Transaction", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel typeLabel = new JLabel("Type:");
        JComboBox<String> typeCombo = new JComboBox<>(new String[] { "Crédit", "Débit" });

        JLabel descriptionLabel = new JLabel("Description:");
        JTextField descriptionField = new JTextField();

        JLabel amountLabel = new JLabel("Montant (MAD):");
        JTextField amountField = new JTextField();

        JLabel dateLabel = new JLabel("Date:");
        JLabel currentDateLabel = new JLabel(new Date().toString());

        formPanel.add(typeLabel);
        formPanel.add(typeCombo);
        formPanel.add(descriptionLabel);
        formPanel.add(descriptionField);
        formPanel.add(amountLabel);
        formPanel.add(amountField);
        formPanel.add(dateLabel);
        formPanel.add(currentDateLabel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        RedButton saveButton = new RedButton("💾 Enregistrer");
        RedButton cancelButton = new RedButton("Annuler");

        saveButton.addActionListener(e -> {
            try {
                String description = descriptionField.getText().trim();
                String type = (String) typeCombo.getSelectedItem();
                double amount;
                try {
                    amount = Double.parseDouble(amountField.getText().trim());
                } catch (NumberFormatException ex) {
                    throw new InvalidInputException("Montant invalide: " + ex.getMessage(), ex);
                }

                if (description.isEmpty()) {
                    throw new ValidationException("Description requise");
                }
                if (amount <= 0) {
                    throw new ValidationException("Montant doit être positif");
                }

                // Call the method in GestionCaisse
                boolean success = false;
                try {
                    success = gestionCaisse.addTransaction(
                            AuthService.getUserId(),
                            description,
                            type,
                            amount,
                            new Date());
                } catch (DatabaseException ex) {
                    UITheme.showStyledMessageDialog(dialog, "Erreur base de données: " + ex.getMessage(), "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (success) {
                    UITheme.showStyledMessageDialog(dialog, "Transaction enregistrée!", "Succès",
                            JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    try {
                        loadCaisseData();
                    } catch (DatabaseException | AuthorizationException ex) {
                        UITheme.showStyledMessageDialog(dialog, "Erreur lors de l'actualisation: " + ex.getMessage(),
                                "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    UITheme.showStyledMessageDialog(dialog, "Erreur lors de l'enregistrement", "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                }

            } catch (ValidationException | InvalidInputException ex) {
                UITheme.showStyledMessageDialog(dialog, ex.getMessage(), "Erreur de validation",
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
    
    private void showTakeLoanDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Prendre un Emprunt", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Source selection
        JLabel sourceLabel = new JLabel("Source:");
        JComboBox<String> sourceCombo = new JComboBox<>(new String[] {"Interne", "Externe"});
        
        JLabel partnerTypeLabel = new JLabel("Type Partenaire:");
        JComboBox<String> partnerTypeCombo = new JComboBox<>(new String[] {"REPARATEUR", "PROPRIETAIRE"});
        
        JLabel partnerIdLabel = new JLabel("ID Partenaire:");
        JTextField partnerIdField = new JTextField();
        
        JLabel partnerNameLabel = new JLabel("Nom Partenaire:");
        JTextField partnerNameField = new JTextField();
        
        JLabel externalNameLabel = new JLabel("Nom Externe:");
        JTextField externalNameField = new JTextField();
        externalNameField.setVisible(false);
        
        // Other fields
        JLabel amountLabel = new JLabel("Montant (MAD):");
        JTextField amountField = new JTextField();
        
        JLabel descriptionLabel = new JLabel("Description:");
        JTextField descriptionField = new JTextField();
        
        JLabel dateLabel = new JLabel("Date Emprunt:");
        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setValue(new Date());
        
        JLabel returnLabel = new JLabel("Date Retour:");
        JSpinner returnSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor returnEditor = new JSpinner.DateEditor(returnSpinner, "dd/MM/yyyy");
        returnSpinner.setEditor(returnEditor);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 1);
        returnSpinner.setValue(calendar.getTime());
        
        formPanel.add(sourceLabel);
        formPanel.add(sourceCombo);
        formPanel.add(partnerTypeLabel);
        formPanel.add(partnerTypeCombo);
        formPanel.add(partnerIdLabel);
        formPanel.add(partnerIdField);
        formPanel.add(partnerNameLabel);
        formPanel.add(partnerNameField);
        formPanel.add(amountLabel);
        formPanel.add(amountField);
        formPanel.add(descriptionLabel);
        formPanel.add(descriptionField);
        formPanel.add(dateLabel);
        formPanel.add(dateSpinner);
        formPanel.add(returnLabel);
        formPanel.add(returnSpinner);
        
        // Toggle external/internal fields
        sourceCombo.addActionListener(e -> {
            boolean isExternal = "Externe".equals(sourceCombo.getSelectedItem());
            partnerTypeLabel.setVisible(!isExternal);
            partnerTypeCombo.setVisible(!isExternal);
            partnerIdLabel.setVisible(!isExternal);
            partnerIdField.setVisible(!isExternal);
            partnerNameLabel.setVisible(!isExternal);
            partnerNameField.setVisible(!isExternal);
            externalNameLabel.setVisible(isExternal);
            externalNameField.setVisible(isExternal);
            
            // Add external name field to form if not already added
            if (isExternal) {
                formPanel.remove(amountLabel);
                formPanel.remove(amountField);
                formPanel.remove(descriptionLabel);
                formPanel.remove(descriptionField);
                formPanel.add(externalNameLabel);
                formPanel.add(externalNameField);
                formPanel.add(amountLabel);
                formPanel.add(amountField);
                formPanel.add(descriptionLabel);
                formPanel.add(descriptionField);
            } else {
                formPanel.remove(externalNameLabel);
                formPanel.remove(externalNameField);
            }
            dialog.pack();
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        RedButton saveButton = new RedButton("💾 Enregistrer");
        RedButton cancelButton = new RedButton("Annuler");
        
        saveButton.addActionListener(e -> {
            try {
                String source = (String) sourceCombo.getSelectedItem();
                double amount = Double.parseDouble(amountField.getText());
                String description = descriptionField.getText();
                Date dateEmprunt = (Date) dateSpinner.getValue();
                Date dateRetour = (Date) returnSpinner.getValue();
                
                if (amount <= 0) {
                    throw new ValidationException("Le montant doit être positif");
                }
                if (dateRetour.before(dateEmprunt)) {
                    throw new ValidationException("La date de retour doit être après la date d'emprunt");
                }
                
                int reparateurId = AuthService.getUserId();
                GestionReparateur gestionReparateur = new GestionReparateur();
                Reparateur reparateur = gestionReparateur.findById(reparateurId);
                int caisseId = reparateur.getCaisse().getId();
                
                if ("Interne".equals(source)) {
                    int partnerId = Integer.parseInt(partnerIdField.getText());
                    String partnerName = partnerNameField.getText();
                    String partnerType = (String) partnerTypeCombo.getSelectedItem();
                    
                    if (partnerName.isEmpty()) {
                        throw new ValidationException("Le nom du partenaire est requis");
                    }
                    
                    gestionCaisse.prendreEmprunt(caisseId, description, amount, 
                                                dateEmprunt, dateRetour, 
                                                partnerType, partnerId, partnerName);
                } else {
                    String externalName = externalNameField.getText();
                    if (externalName.isEmpty()) {
                        throw new ValidationException("Le nom externe est requis");
                    }
                    gestionCaisse.prendreEmpruntExterne(caisseId, description, amount, 
                                                       dateEmprunt, dateRetour, externalName);
                }
                
                UITheme.showStyledMessageDialog(dialog, "Emprunt enregistré!", "Succès",
                        JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadCaisseData();
                
            } catch (ValidationException ex) {
                UITheme.showStyledMessageDialog(dialog, ex.getMessage(), "Erreur de validation",
                        JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                UITheme.showStyledMessageDialog(dialog, "ID partenaire invalide", "Erreur",
                        JOptionPane.ERROR_MESSAGE);
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
    
    private void showGiveLoanDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Donner un Emprunt", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Source selection
        JLabel sourceLabel = new JLabel("Destination:");
        JComboBox<String> sourceCombo = new JComboBox<>(new String[] {"Interne", "Externe"});
        
        JLabel partnerTypeLabel = new JLabel("Type Partenaire:");
        JComboBox<String> partnerTypeCombo = new JComboBox<>(new String[] {"REPARATEUR", "PROPRIETAIRE"});
        
        JLabel partnerIdLabel = new JLabel("ID Partenaire:");
        JTextField partnerIdField = new JTextField();
        
        JLabel partnerNameLabel = new JLabel("Nom Partenaire:");
        JTextField partnerNameField = new JTextField();
        
        JLabel externalNameLabel = new JLabel("Nom Externe:");
        JTextField externalNameField = new JTextField();
        externalNameField.setVisible(false);
        
        // Other fields
        JLabel amountLabel = new JLabel("Montant (MAD):");
        JTextField amountField = new JTextField();
        
        JLabel descriptionLabel = new JLabel("Description:");
        JTextField descriptionField = new JTextField();
        
        JLabel dateLabel = new JLabel("Date Emprunt:");
        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setValue(new Date());
        
        JLabel returnLabel = new JLabel("Date Retour:");
        JSpinner returnSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor returnEditor = new JSpinner.DateEditor(returnSpinner, "dd/MM/yyyy");
        returnSpinner.setEditor(returnEditor);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 1);
        returnSpinner.setValue(calendar.getTime());
        
        formPanel.add(sourceLabel);
        formPanel.add(sourceCombo);
        formPanel.add(partnerTypeLabel);
        formPanel.add(partnerTypeCombo);
        formPanel.add(partnerIdLabel);
        formPanel.add(partnerIdField);
        formPanel.add(partnerNameLabel);
        formPanel.add(partnerNameField);
        formPanel.add(amountLabel);
        formPanel.add(amountField);
        formPanel.add(descriptionLabel);
        formPanel.add(descriptionField);
        formPanel.add(dateLabel);
        formPanel.add(dateSpinner);
        formPanel.add(returnLabel);
        formPanel.add(returnSpinner);
        
        // Toggle external/internal fields
        sourceCombo.addActionListener(e -> {
            boolean isExternal = "Externe".equals(sourceCombo.getSelectedItem());
            partnerTypeLabel.setVisible(!isExternal);
            partnerTypeCombo.setVisible(!isExternal);
            partnerIdLabel.setVisible(!isExternal);
            partnerIdField.setVisible(!isExternal);
            partnerNameLabel.setVisible(!isExternal);
            partnerNameField.setVisible(!isExternal);
            externalNameLabel.setVisible(isExternal);
            externalNameField.setVisible(isExternal);
            
            // Add external name field to form if not already added
            if (isExternal) {
                formPanel.remove(amountLabel);
                formPanel.remove(amountField);
                formPanel.remove(descriptionLabel);
                formPanel.remove(descriptionField);
                formPanel.add(externalNameLabel);
                formPanel.add(externalNameField);
                formPanel.add(amountLabel);
                formPanel.add(amountField);
                formPanel.add(descriptionLabel);
                formPanel.add(descriptionField);
            } else {
                formPanel.remove(externalNameLabel);
                formPanel.remove(externalNameField);
            }
            dialog.pack();
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        RedButton saveButton = new RedButton("💾 Enregistrer");
        RedButton cancelButton = new RedButton("Annuler");
        
        saveButton.addActionListener(e -> {
            try {
                String source = (String) sourceCombo.getSelectedItem();
                double amount = Double.parseDouble(amountField.getText());
                String description = descriptionField.getText();
                Date dateEmprunt = (Date) dateSpinner.getValue();
                Date dateRetour = (Date) returnSpinner.getValue();
                
                if (amount <= 0) {
                    throw new ValidationException("Le montant doit être positif");
                }
                if (dateRetour.before(dateEmprunt)) {
                    throw new ValidationException("La date de retour doit être après la date d'emprunt");
                }
                
                int reparateurId = AuthService.getUserId();
                GestionReparateur gestionReparateur = new GestionReparateur();
                Reparateur reparateur = gestionReparateur.findById(reparateurId);
                int caisseId = reparateur.getCaisse().getId();
                
                if ("Interne".equals(source)) {
                    int partnerId = Integer.parseInt(partnerIdField.getText());
                    String partnerName = partnerNameField.getText();
                    String partnerType = (String) partnerTypeCombo.getSelectedItem();
                    
                    if (partnerName.isEmpty()) {
                        throw new ValidationException("Le nom du partenaire est requis");
                    }
                    
                    gestionCaisse.donnerEmprunt(caisseId, description, amount, 
                                               dateEmprunt, dateRetour, 
                                               partnerType, partnerId, partnerName);
                } else {
                    String externalName = externalNameField.getText();
                    if (externalName.isEmpty()) {
                        throw new ValidationException("Le nom externe est requis");
                    }
                    gestionCaisse.donnerEmpruntExterne(caisseId, description, amount, 
                                                      dateEmprunt, dateRetour, externalName);
                }
                
                UITheme.showStyledMessageDialog(dialog, "Emprunt donné avec succès!", "Succès",
                        JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadCaisseData();
                
            } catch (InsufficientFundsException ex) {
                UITheme.showStyledMessageDialog(dialog, "Solde insuffisant: " + ex.getMessage(), "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            } catch (ValidationException ex) {
                UITheme.showStyledMessageDialog(dialog, ex.getMessage(), "Erreur de validation",
                        JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                UITheme.showStyledMessageDialog(dialog, "ID partenaire invalide", "Erreur",
                        JOptionPane.ERROR_MESSAGE);
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
    
    private void showRepayDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Rembourser un Emprunt", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);
        
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel empruntIdLabel = new JLabel("ID de l'Emprunt:");
        JTextField empruntIdField = new JTextField();
        
        JLabel dateLabel = new JLabel("Date Remboursement:");
        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setValue(new Date());
        
        formPanel.add(empruntIdLabel);
        formPanel.add(empruntIdField);
        formPanel.add(dateLabel);
        formPanel.add(dateSpinner);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        RedButton saveButton = new RedButton("💾 Rembourser");
        RedButton cancelButton = new RedButton("Annuler");
        
        saveButton.addActionListener(e -> {
            try {
                int empruntId = Integer.parseInt(empruntIdField.getText());
                Date dateRemboursement = (Date) dateSpinner.getValue();
                
                gestionCaisse.rembourserEmprunt(empruntId, dateRemboursement);
                
                UITheme.showStyledMessageDialog(dialog, "Emprunt remboursé avec succès!", "Succès",
                        JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadCaisseData();
                
            } catch (NumberFormatException ex) {
                UITheme.showStyledMessageDialog(dialog, "ID d'emprunt invalide", "Erreur",
                        JOptionPane.ERROR_MESSAGE);
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
}