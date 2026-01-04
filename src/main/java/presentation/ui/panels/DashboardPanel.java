package presentation.ui.panels;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;
import presentation.ui.components.*;
import presentation.ui.utils.UITheme;
import metier.*;
import dao.*;

public class DashboardPanel extends JPanel {
    private GestionClient gestionClient;
    private GestionAppareil gestionAppareil;
    private GestionReparation gestionReparation;
    private GestionPiece gestionPiece;
    private GestionReparateur gestionReparateur;
    private GestionBoutique gestionBoutique;
    private GestionEmprunt gestionEmprunt;
    private GestionCaisse gestionCaisse;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public DashboardPanel(CardLayout cardLayout, JPanel mainPanel) {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        initGestionServices();
        setLayout(new BorderLayout());
        setBackground(UITheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(32, 40, 32, 40));

        initComponents();
    }

    private void initGestionServices() {
        gestionClient = new GestionClient();
        gestionAppareil = new GestionAppareil();
        gestionReparation = new GestionReparation();
        gestionPiece = new GestionPiece();
        gestionReparateur = new GestionReparateur();
        gestionBoutique = new GestionBoutique();
        gestionEmprunt = new GestionEmprunt();
        gestionCaisse = new GestionCaisse();
    }

    private void initComponents() {
        // Enhanced Header with visual element
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 32, 0));

        JPanel titleContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titleContainer.setOpaque(false);

        JLabel title = new JLabel("Tableau de Bord");
        title.setFont(UITheme.getTitleFont().deriveFont(32f));
        title.setForeground(UITheme.TEXT_PRIMARY);

        titleContainer.add(title);
        headerPanel.add(titleContainer, BorderLayout.WEST);

        // Stats cards
        JPanel statsPanel = createStatsPanel();

        // Two column layout for bottom sections
        JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        bottomPanel.setOpaque(false);

        // Recent activities
        JPanel recentPanel = createRecentPanel();

        // Quick actions
        JPanel actionsPanel = createActionsPanel();

        bottomPanel.add(recentPanel);
        bottomPanel.add(actionsPanel);

        // Assemble
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        content.add(headerPanel);
        content.add(statsPanel);
        content.add(Box.createRigidArea(new Dimension(0, 24)));
        content.add(bottomPanel);

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(UITheme.BACKGROUND);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 4, 20, 20));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        String[] stats = { "Clients", "Appareils", "Réparations", "Pièces",
                "Réparateurs", "Boutiques", "Caisse (MAD)", "Emprunts" };
        String[] values = {
                String.valueOf(gestionClient.findAll().size()),
                String.valueOf(gestionAppareil.findAll().size()),
                String.valueOf(gestionReparation.findAll().size()),
                String.valueOf(gestionPiece.findAll().size()),
                String.valueOf(gestionReparateur.findAll().size()),
                String.valueOf(gestionBoutique.findAll().size()),
                String.format("%.2f MAD",
                        gestionCaisse.findAll().isEmpty() ? 0.0
                                : gestionCaisse.getSolde(gestionCaisse.findAll().get(0).getId())),
                String.valueOf(gestionEmprunt.findAll().size())
        };

        for (int i = 0; i < stats.length; i++) {
            panel.add(createStatCard(stats[i], values[i]));
        }

        return panel;
    }

    private JPanel createStatCard(String title, String value) {
        RedCard card = new RedCard(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UITheme.getLabelFont());
        titleLabel.setForeground(UITheme.TEXT_SECONDARY);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(UITheme.getHeadingFont().deriveFont(24f));
        valueLabel.setForeground(UITheme.PRIMARY);
        valueLabel.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));

        card.add(topPanel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createRecentPanel() {
        RedCard panel = new RedCard(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JLabel title = new JLabel("Activités Récentes");
        title.setFont(UITheme.getSubtitleFont().deriveFont(20f));
        title.setForeground(UITheme.TEXT_PRIMARY);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        List<Reparation> recentReparations = gestionReparation.findAll().stream().limit(3).collect(Collectors.toList());
        List<Client> recentClients = gestionClient.findAll().stream().limit(2).collect(Collectors.toList());

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);

        for (Reparation reparation : recentReparations) {
            String activity = "Réparation " + reparation.getCodeReparation() + " - " + reparation.getStatut();
            JLabel activityLabel = new JLabel("• " + activity);
            activityLabel.setFont(UITheme.getBodyFont()); // Using UITheme font
            activityLabel.setForeground(UITheme.TEXT_PRIMARY); // Changed from TEXT_DARK to TEXT_PRIMARY
            activityLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 0)); // Added left padding
            listPanel.add(activityLabel);
        }

        for (Client client : recentClients) {
            String activity = "Nouveau client: " + client.getNom() + " " + client.getPrenom(); // Added emoji
            JLabel activityLabel = new JLabel("• " + activity);
            activityLabel.setFont(UITheme.getBodyFont()); // Using UITheme font
            activityLabel.setForeground(UITheme.TEXT_PRIMARY); // Changed from TEXT_DARK to TEXT_PRIMARY
            activityLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 0)); // Added left padding
            listPanel.add(activityLabel);
        }

        panel.add(title, BorderLayout.NORTH);
        panel.add(listPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createActionsPanel() {
        RedCard panel = new RedCard(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JLabel title = new JLabel("Actions Rapides");
        title.setFont(UITheme.getSubtitleFont().deriveFont(20f));
        title.setForeground(UITheme.TEXT_PRIMARY);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        buttonsPanel.setOpaque(false);

        String[] actions = { "Nouveau Client", "Nouvel Appareil", "Nouvelle Réparation",
                "Ajouter Pièce", "Nouvelle Boutique", "Voir Rapports" };
        String[] panelNames = { "CLIENTS", "APPAREILS", "REPARATIONS", "PIECES", "BOUTIQUES", "DASHBOARD" };

        for (int i = 0; i < actions.length; i++) {
            String action = actions[i];
            String panelName = panelNames[i];
            RedButton btn = new RedButton(action);
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
            btn.setAlignmentX(Component.LEFT_ALIGNMENT);
            btn.addActionListener(e -> {
                if ("Voir Rapports".equals(action)) {
                    UITheme.showStyledMessageDialog(this, "Fonctionnalité de rapports à implémenter", "Info",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    cardLayout.show(mainPanel, panelName);
                }
            });
            buttonsPanel.add(btn);
            if (i < actions.length - 1) {
                buttonsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }

        panel.add(title, BorderLayout.NORTH);
        panel.add(buttonsPanel, BorderLayout.CENTER);

        return panel;
    }
}