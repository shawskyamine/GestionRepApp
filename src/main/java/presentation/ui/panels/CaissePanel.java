package presentation.ui.panels;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
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
import java.util.Date;

public class CaissePanel extends JPanel {
    private GestionCaisse gestionCaisse;
    private RedTable transactionTable;
    private DefaultTableModel tableModel;
    private JLabel balanceLabel;
    private String[] columnNames = { "Date", "Description", "Type", "Montant" };

    public CaissePanel() {
        gestionCaisse = new GestionCaisse();
        setLayout(new BorderLayout());
        setBackground(UITheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        initComponents();
        loadCaisseData();
    }

    private void initComponents() {
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 24, 0));

        JLabel title = new JLabel("💰 Gestion de la Caisse");
        title.setFont(UITheme.getTitleFont());
        title.setForeground(UITheme.TEXT_PRIMARY);
        headerPanel.add(title, BorderLayout.WEST);

        balanceLabel = new JLabel("Solde: 0.00 MAD");
        balanceLabel.setFont(UITheme.getTitleFont());
        balanceLabel.setForeground(UITheme.PRIMARY);
        headerPanel.add(balanceLabel, BorderLayout.EAST);

        // Table
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

        // Add buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));

        RedButton refreshButton = new RedButton("Actualiser");
        refreshButton.setPreferredSize(new Dimension(130, 38));
        refreshButton.addActionListener(e -> loadCaisseData());

        RedButton addTransactionButton = new RedButton("Nouvelle Transaction");
        addTransactionButton.setPreferredSize(new Dimension(180, 38));
        addTransactionButton.addActionListener(e -> showAddTransactionDialog());

        buttonPanel.add(refreshButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(8, 0)));
        buttonPanel.add(addTransactionButton);

        // Assemble
        add(headerPanel, BorderLayout.NORTH);
        add(tableScroll, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadCaisseData() {
        try {
            // Check if current user is a reparateur or proprietaire
            if (!AuthService.isReparateur() && !AuthService.isProprietaire()) {
                balanceLabel.setText("Accès non autorisé - Réservé aux réparateurs et propriétaires");
                tableModel.setRowCount(0);
                return;
            }

            if (AuthService.isProprietaire()) {
                // For proprietaire, show all transactions from all caisses
                balanceLabel.setText("Propriétaire - Vue générale des caisses");
                tableModel.setRowCount(0);
                List<Transaction> allTransactions = getAllTransactions();
                if (allTransactions != null && !allTransactions.isEmpty()) {
                    for (Transaction transaction : allTransactions) {
                        tableModel.addRow(new Object[] {
                                transaction.getDate(),
                                transaction.getDescription(),
                                transaction.getType(),
                                String.format("%.2f MAD", transaction.getAmount())
                        });
                    }
                } else {
                    tableModel.addRow(new Object[] {"Aucune donnée", "", "", ""});
                }
                return;
            }

            // For reparateur
            int userId = AuthService.getUserId();
            GestionReparateur gestionReparateur = new GestionReparateur();
            Reparateur reparateur = gestionReparateur.findById(userId);

            if (reparateur == null) {
                balanceLabel.setText("Réparateur non trouvé");
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

            double balance = gestionCaisse.getSolde(caisseId);
            balanceLabel.setText(String.format("Solde: %.2f MAD", balance));

            tableModel.setRowCount(0);
            List<Transaction> transactions = getTransactions(caisseId);
            if (transactions != null && !transactions.isEmpty()) {
                for (Transaction transaction : transactions) {
                    tableModel.addRow(new Object[] {
                            transaction.getDate(),
                            transaction.getDescription(),
                            transaction.getType(),
                            String.format("%.2f MAD", transaction.getAmount())
                    });
                }
            } else {
                tableModel.addRow(new Object[] {"Aucune transaction", "", "", ""});
            }
        } catch (Exception e) {
            balanceLabel.setText("Erreur de chargement");
            e.printStackTrace();
        }
    }

    private List<Transaction> getTransactions(int caisseId) {
        List<Transaction> transactions = new ArrayList<>();

        try {
            // Get réparations
            List<Reparation> reparations = gestionCaisse.getReparationsByCaisse(caisseId);
            if (reparations != null) {
                for (Reparation r : reparations) {
                    transactions.add(new Transaction(
                            r.getDateDeCreation(),
                            "Réparation " + r.getCodeReparation(),
                            "Crédit",
                            r.getPrixTotal()));
                }
            }

            // Get emprunts
            List<Emprunt> emprunts = gestionCaisse.getEmpruntsByCaisse(caisseId);
            if (emprunts != null) {
                for (Emprunt e : emprunts) {
                    transactions.add(new Transaction(
                            e.getDateEmprunt(),
                            "Emprunt #" + e.getId(),
                            "Débit",
                            e.getMontant()));
                }
            }

            // Sort by date (newest first)
            transactions.sort(Comparator.comparing(Transaction::getDate).reversed());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return transactions;
    }

    private List<Transaction> getAllTransactions() {
        List<Transaction> transactions = new ArrayList<>();

        try {
            // Get all reparations
            List<Reparation> reparations = gestionCaisse.getAllReparations();
            if (reparations != null) {
                for (Reparation r : reparations) {
                    String reparateurName = (r.getReparateur() != null) ? r.getReparateur().getNom() : "Inconnu";
                    transactions.add(new Transaction(
                            r.getDateDeCreation(),
                            "Réparation " + r.getCodeReparation() + " - " + reparateurName,
                            "Crédit",
                            r.getPrixTotal()));
                }
            }

            // Get all emprunts - FIXED: Access reparateur through caisse
            List<Emprunt> emprunts = gestionCaisse.getAllEmprunts();
            if (emprunts != null) {
                for (Emprunt e : emprunts) {
                    String reparateurName = "Inconnu";
                    // FIX: Access reparateur through caisse
                    if (e.getCaisse() != null && e.getCaisse().getReparateur() != null) {
                        reparateurName = e.getCaisse().getReparateur().getNom();
                    }
                    transactions.add(new Transaction(
                            e.getDateEmprunt(),
                            "Emprunt #" + e.getId() + " - " + reparateurName,
                            "Débit",
                            e.getMontant()));
                }
            }

            // Sort by date (newest first)
            transactions.sort(Comparator.comparing(Transaction::getDate).reversed());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return transactions;
    }

    private void showAddTransactionDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "➕ Nouvelle Transaction", true);
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
                double amount = Double.parseDouble(amountField.getText().trim());

                if (description.isEmpty() || amount <= 0) {
                    UITheme.showStyledMessageDialog(dialog, "Description et montant valide requis", "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // FIX: Call the method that now exists in GestionCaisse
                boolean success = gestionCaisse.addTransaction(
                        AuthService.getUserId(),
                        description,
                        type,
                        amount,
                        new Date()
                );

                if (success) {
                    UITheme.showStyledMessageDialog(dialog, "Transaction enregistrée!", "Succès",
                            JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    loadCaisseData();
                } else {
                    UITheme.showStyledMessageDialog(dialog, "Erreur lors de l'enregistrement", "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                }

            } catch (NumberFormatException ex) {
                UITheme.showStyledMessageDialog(dialog, "Montant invalide", "Erreur", JOptionPane.ERROR_MESSAGE);
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