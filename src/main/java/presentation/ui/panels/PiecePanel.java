package presentation.ui.panels;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import presentation.ui.components.*;
import presentation.ui.utils.UITheme;
import metier.GestionPiece;
import dao.Piece;

public class PiecePanel extends JPanel {
    private GestionPiece gestionPiece;
    private RedTable pieceTable;
    private DefaultTableModel tableModel;
    private String[] columnNames = { "ID", "Nom de la Pièce" };

    public PiecePanel() {
        gestionPiece = new GestionPiece();
        setLayout(new BorderLayout());
        setBackground(UITheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        initComponents();
        loadPieces();
    }

    private void initComponents() {
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel title = new JLabel("⚙️ Gestion des Pièces");
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
        editButton.addActionListener(e -> editPiece());
        RedButton deleteButton = new RedButton("Supprimer");
        deleteButton.setPreferredSize(new Dimension(130, 38));
        deleteButton.addActionListener(e -> deletePiece());
        RedButton refreshButton = new RedButton("Actualiser");
        refreshButton.setPreferredSize(new Dimension(130, 38));
        refreshButton.addActionListener(e -> loadPieces());

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

        pieceTable = new RedTable();
        pieceTable.setModel(tableModel);

        JScrollPane tableScroll = new JScrollPane(pieceTable);
        tableScroll.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_MEDIUM, 1),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        tableScroll.setBackground(UITheme.BACKGROUND);

        // Assemble
        add(headerPanel, BorderLayout.NORTH);
        add(tableScroll, BorderLayout.CENTER);
    }

    private void loadPieces() {
        tableModel.setRowCount(0);
        List<Piece> pieces = gestionPiece.findAll();
        if (pieces != null) {
            for (Piece piece : pieces) {
                tableModel.addRow(new Object[] {
                        piece.getId(),
                        piece.getNomPiece()
                });
            }
        }
    }

    private void showAddDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Nouvelle Pièce", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 150);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel nomLabel = new JLabel("Nom de la pièce:");
        JTextField nomField = new JTextField();

        formPanel.add(nomLabel);
        formPanel.add(nomField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        RedButton saveButton = new RedButton("Enregistrer");
        RedButton cancelButton = new RedButton("Annuler");

        saveButton.addActionListener(e -> {
            if (nomField.getText().trim().isEmpty()) {
                UITheme.showStyledMessageDialog(dialog, "Le nom de la pièce est obligatoire", "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                Piece newPiece = Piece.builder()
                        .nomPiece(nomField.getText().trim())
                        .build();

                gestionPiece.add(newPiece);
                loadPieces();
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

    private void editPiece() {
        int selectedRow = pieceTable.getSelectedRow();
        if (selectedRow == -1) {
            UITheme.showStyledMessageDialog(this, "Veuillez sélectionner une pièce", "Aucune sélection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int pieceId = (int) tableModel.getValueAt(selectedRow, 0);
        Piece piece = gestionPiece.findById(pieceId);

        if (piece == null) {
            UITheme.showStyledMessageDialog(this, "Pièce non trouvée", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "✏️ Modifier Pièce", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 150);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel nomLabel = new JLabel("Nom de la pièce:");
        JTextField nomField = new JTextField(piece.getNomPiece());

        formPanel.add(nomLabel);
        formPanel.add(nomField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        RedButton saveButton = new RedButton("Enregistrer");
        RedButton cancelButton = new RedButton("Annuler");

        saveButton.addActionListener(e -> {
            if (nomField.getText().trim().isEmpty()) {
                UITheme.showStyledMessageDialog(dialog, "Le nom de la pièce est obligatoire", "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                piece.setNomPiece(nomField.getText().trim());
                gestionPiece.update(piece);
                loadPieces();
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

    private void deletePiece() {
        int selectedRow = pieceTable.getSelectedRow();
        if (selectedRow == -1) {
            UITheme.showStyledMessageDialog(this, "Veuillez sélectionner une pièce", "Aucune sélection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int pieceId = (int) tableModel.getValueAt(selectedRow, 0);
        Piece piece = gestionPiece.findById(pieceId);

        if (piece == null) {
            UITheme.showStyledMessageDialog(this, "Pièce non trouvée", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = UITheme.showStyledConfirmDialog(this, "Supprimer la pièce: " + piece.getNomPiece() + "?",
                "Confirmation");
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                gestionPiece.delete(piece);
                loadPieces();
            } catch (Exception ex) {
                UITheme.showStyledMessageDialog(this, "Erreur de suppression: " + ex.getMessage(), "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}