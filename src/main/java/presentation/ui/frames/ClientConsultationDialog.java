package presentation.ui.frames;

import javax.swing.*;
import java.awt.*;
import presentation.ui.components.*;
import presentation.ui.utils.UITheme;
import metier.GestionReparation;
import dao.Reparation;
import dao.Appareil;
import dao.Piece;
import exception.DatabaseException;
import exception.EntityNotFoundException;

public class ClientConsultationDialog extends JDialog {
    private GestionReparation gestionReparation;
    private JTextField codeField;
    private JPanel detailsPanel;

    public ClientConsultationDialog(Frame parent) {
        super(parent, "Suivre ma Réparation", true);
        System.out.println("ClientConsultationDialog constructor called");
        gestionReparation = new GestionReparation();

        setSize(700, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        initComponents();
        System.out.println("ClientConsultationDialog initialized");
    }

    private void initComponents() {
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UITheme.PRIMARY);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Suivre ma Réparation");
        title.setFont(UITheme.getTitleFont());
        title.setForeground(Color.WHITE);

        headerPanel.add(title, BorderLayout.WEST);

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        searchPanel.setBackground(UITheme.BACKGROUND);

        JLabel codeLabel = new JLabel("Code de Réparation:");
        codeLabel.setFont(UITheme.getLabelFont());
        codeField = new JTextField(20);
        codeField.setFont(UITheme.getBodyFont());

        RedButton searchButton = new RedButton("Rechercher");
        searchButton.addActionListener(e -> {
            System.out.println("Search button clicked");
            searchReparation();
        });

        searchPanel.add(codeLabel);
        searchPanel.add(codeField);
        searchPanel.add(searchButton);

        // Details Panel
        detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBackground(UITheme.BACKGROUND);
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        JScrollPane scrollPane = new JScrollPane(detailsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        RedButton closeButton = new RedButton("Fermer");
        closeButton.addActionListener(e -> dispose());

        buttonPanel.add(closeButton);

        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(searchPanel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void searchReparation() {
        System.out.println("searchReparation() method called");
        String code = codeField.getText().trim();
        System.out.println("Code entered: '" + code + "'");

        if (code.isEmpty()) {
            UITheme.showStyledMessageDialog(this, "Veuillez entrer un code de réparation", "Code manquant",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            System.out.println("Searching for reparation with code: " + code);
            Reparation reparation = gestionReparation.findByCodeReparation(code);
            System.out.println("Reparation found: " + (reparation != null ? reparation.getCodeReparation() : "null"));

            displayReparationDetails(reparation);
        } catch (Exception e) {
            System.out.println("Exception in searchReparation: " + e.getMessage());
            UITheme.showStyledMessageDialog(this, "Erreur lors de la recherche: " + e.getMessage(), "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayReparationDetails(Reparation reparation) {
        detailsPanel.removeAll();

        // Basic Info
        JPanel basicPanel = new JPanel(new GridLayout(0, 2, 10, 5));
        basicPanel.setBackground(UITheme.BACKGROUND);
        basicPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(UITheme.PRIMARY),
                "Informations de la Réparation"));

        addDetail(basicPanel, "Code:", reparation.getCodeReparation());
        addDetail(basicPanel, "Statut:", reparation.getStatut());
        addDetail(basicPanel, "Cause:", reparation.getCauseDeReparation());
        addDetail(basicPanel, "Date de Création:", reparation.getDateDeCreation().toString());
        addDetail(basicPanel, "Réparateur:",
                reparation.getReparateur() != null
                        ? reparation.getReparateur().getNom() + " " + reparation.getReparateur().getPrenom()
                        : "N/A");
        addDetail(basicPanel, "Prix Total:", String.format("%.2f MAD", reparation.getPrixTotal()));

        detailsPanel.add(basicPanel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Appareils
        if (reparation.getAppareils() != null && !reparation.getAppareils().isEmpty()) {
            JPanel appareilsPanel = new JPanel();
            appareilsPanel.setLayout(new BoxLayout(appareilsPanel, BoxLayout.Y_AXIS));
            appareilsPanel.setBackground(UITheme.BACKGROUND);
            appareilsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(UITheme.PRIMARY),
                    "Appareils à Réparer"));

            for (Appareil appareil : reparation.getAppareils()) {
                JPanel appareilPanel = createAppareilPanel(appareil);
                appareilsPanel.add(appareilPanel);
                appareilsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }

            detailsPanel.add(appareilsPanel);
        }

        detailsPanel.revalidate();
        detailsPanel.repaint();
    }

    private void addDetail(JPanel panel, String label, String value) {
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(UITheme.getLabelFont());
        labelComp.setForeground(UITheme.TEXT_SECONDARY);

        JLabel valueComp = new JLabel(value);
        valueComp.setFont(UITheme.getBodyFont());
        valueComp.setForeground(UITheme.TEXT_PRIMARY);

        panel.add(labelComp);
        panel.add(valueComp);
    }

    private JPanel createAppareilPanel(Appareil appareil) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BACKGROUND);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_COLOR),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        // Appareil Info
        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 5, 2));
        infoPanel.setBackground(UITheme.BACKGROUND);

        addDetail(infoPanel, "IMEI:", appareil.getImei());
        addDetail(infoPanel, "Marque:", appareil.getMarque());
        addDetail(infoPanel, "Modèle:", appareil.getModele());
        addDetail(infoPanel, "Type:", appareil.getTypeAppareil());
        addDetail(infoPanel, "Couleur:", appareil.getCouleur());

        panel.add(infoPanel, BorderLayout.NORTH);

        // Pieces
        if (appareil.getPieces() != null && !appareil.getPieces().isEmpty()) {
            JPanel piecesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            piecesPanel.setBackground(UITheme.BACKGROUND);
            piecesPanel.setBorder(BorderFactory.createTitledBorder("Pièces à réparer"));

            for (Piece piece : appareil.getPieces()) {
                JLabel pieceLabel = new JLabel("• " + piece.getNomPiece());
                pieceLabel.setFont(UITheme.getBodyFont());
                pieceLabel.setForeground(UITheme.TEXT_PRIMARY);
                piecesPanel.add(pieceLabel);
            }

            panel.add(piecesPanel, BorderLayout.CENTER);
        }

        return panel;
    }
}