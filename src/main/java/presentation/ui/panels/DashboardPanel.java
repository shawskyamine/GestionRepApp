package presentation.ui.panels;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import presentation.ui.components.*;
import presentation.ui.utils.UITheme;
import metier.*;
import dao.*;
import exception.DatabaseException;
import presentation.ui.utils.AuthService;

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
        // Professional Header - Clean and Simple
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        JPanel titleContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titleContainer.setOpaque(false);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        JLabel title = new JLabel("Tableau de Bord");
        title.setFont(UITheme.getTitleFont().deriveFont(28f));
        title.setForeground(UITheme.TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Vue d'ensemble de votre activité de réparation");
        subtitle.setFont(UITheme.getBodyFont().deriveFont(14f));
        subtitle.setForeground(UITheme.TEXT_SECONDARY);

        titlePanel.add(title);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 4)));
        titlePanel.add(subtitle);

        titleContainer.add(titlePanel);
        headerPanel.add(titleContainer, BorderLayout.WEST);

        // Simple welcome message
        JPanel welcomePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        welcomePanel.setOpaque(false);

        String welcomeText = "Bienvenue, " + getCurrentUserName();
        JLabel welcomeLabel = new JLabel(welcomeText);
        welcomeLabel.setFont(UITheme.getBodyFont().deriveFont(14f));
        welcomeLabel.setForeground(UITheme.TEXT_SECONDARY);

        welcomePanel.add(welcomeLabel);
        headerPanel.add(welcomePanel, BorderLayout.EAST);

        // Key Metrics - 2x4 grid for main business metrics
        JPanel metricsPanel = createMetricsPanel();

        // Bottom sections - Recent activities and Quick actions
        JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 24, 0));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(24, 0, 0, 0));

        // Recent activities
        JPanel recentPanel = createRecentActivitiesPanel();

        // Quick actions
        JPanel actionsPanel = createQuickActionsPanel();

        bottomPanel.add(recentPanel);
        bottomPanel.add(actionsPanel);

        // Assemble main content
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        content.add(headerPanel);
        content.add(Box.createRigidArea(new Dimension(0, 16)));
        content.add(metricsPanel);
        content.add(bottomPanel);

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(UITheme.BACKGROUND);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createMetricsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 4, 20, 20));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // Core business metrics
        String[] metrics = {
                "Clients", "Appareils", "Réparations", "Pièces",
                "Réparateurs", "Boutiques", "Caisse", "Activités"
        };

        // Get all values with proper exception handling
        String clientCount = getClientCount();
        String appareilCount = getAppareilCount();
        String reparationCount = getReparationCount();
        String pieceCount = getPieceCount();
        String reparateurCount = getReparateurCount();
        String boutiqueCount = getBoutiqueCount();
        String caisseInfo = getCaisseInfo();
        String activitesCount = getActivitesCount();

        String[] values = {
                clientCount,
                appareilCount,
                reparationCount,
                pieceCount,
                reparateurCount,
                boutiqueCount,
                caisseInfo,
                activitesCount
        };

        // Professional color scheme
        Color[] accentColors = {
                UITheme.PRIMARY, UITheme.PRIMARY, UITheme.PRIMARY, UITheme.PRIMARY,
                UITheme.PRIMARY, UITheme.PRIMARY, UITheme.PRIMARY, UITheme.PRIMARY
        };

        for (int i = 0; i < metrics.length; i++) {
            panel.add(createMetricCard(metrics[i], values[i], accentColors[i]));
        }

        return panel;
    }

    // Helper methods with proper exception handling
    private String getClientCount() {
        try {
            Boutique selectedBoutique = AuthService.getSelectedBoutique();
            if (selectedBoutique != null) {
                List<Client> clients = gestionClient.findAllByBoutique(selectedBoutique);
                return String.valueOf(clients.size());
            } else {
                List<Client> clients = gestionClient.findAll();
                return String.valueOf(clients.size());
            }
        } catch (DatabaseException e) {
            return "0";
        }
    }

    private String getAppareilCount() {
        try {
            Boutique selectedBoutique = AuthService.getSelectedBoutique();
            if (selectedBoutique != null) {
                List<Appareil> appareils = gestionAppareil.findAllByBoutique(selectedBoutique);
                return String.valueOf(appareils.size());
            } else {
                List<Appareil> appareils = gestionAppareil.findAll();
                return String.valueOf(appareils.size());
            }
        } catch (DatabaseException e) {
            return "0";
        }
    }

    private String getReparationCount() {
        try {
            Boutique selectedBoutique = AuthService.getSelectedBoutique();
            if (selectedBoutique != null) {
                List<Reparation> reparations = gestionReparation.findAllByBoutique(selectedBoutique);
                return String.valueOf(reparations.size());
            } else {
                List<Reparation> reparations = gestionReparation.findAll();
                return String.valueOf(reparations.size());
            }
        } catch (DatabaseException e) {
            return "0";
        }
    }

    private String getPieceCount() {
        try {
            List<Piece> pieces = gestionPiece.findAll();
            return String.valueOf(pieces.size());
        } catch (DatabaseException e) {
            return "0";
        }
    }

    private String getReparateurCount() {
        try {
            Boutique selectedBoutique = AuthService.getSelectedBoutique();
            if (selectedBoutique != null) {
                List<Reparateur> reparateurs = gestionReparateur.findAllByBoutique(selectedBoutique);
                return String.valueOf(reparateurs.size());
            } else {
                List<Reparateur> reparateurs = gestionReparateur.findAll();
                return String.valueOf(reparateurs.size());
            }
        } catch (DatabaseException e) {
            return "0";
        }
    }

    private String getBoutiqueCount() {
        try {
            List<Boutique> boutiques = gestionBoutique.findAll();
            return String.valueOf(boutiques.size());
        } catch (DatabaseException e) {
            return "0";
        }
    }

    private String getCaisseInfo() {
        try {
            List<Caisse> caisses = gestionCaisse.findAll();
            if (!caisses.isEmpty()) {
                double solde = gestionCaisse.getSolde(caisses.get(0).getId());
                return String.format("%.0f MAD", solde);
            }
            return "0 MAD";
        } catch (DatabaseException e) {
            return "0 MAD";
        }
    }

    private String getActivitesCount() {
        try {
            int reparationCount = gestionReparation.findAll().size();
            int clientCount = gestionClient.findAll().size();
            return String.valueOf(reparationCount + clientCount);
        } catch (DatabaseException e) {
            return "0";
        }
    }

    private JPanel createMetricCard(String title, String value, Color accentColor) {
        RedCard card = new RedCard(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        card.setPreferredSize(new Dimension(180, 110));

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(UITheme.getHeadingFont().deriveFont(26f));
        valueLabel.setForeground(accentColor);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UITheme.getLabelFont().deriveFont(13f));
        titleLabel.setForeground(UITheme.TEXT_SECONDARY);

        contentPanel.add(valueLabel, BorderLayout.WEST);
        contentPanel.add(titleLabel, BorderLayout.EAST);

        card.add(contentPanel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createRecentActivitiesPanel() {
        RedCard panel = new RedCard(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setPreferredSize(new Dimension(400, 280));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));

        JLabel title = new JLabel("Activités Récentes");
        title.setFont(UITheme.getSubtitleFont().deriveFont(16f));
        title.setForeground(UITheme.TEXT_PRIMARY);

        headerPanel.add(title, BorderLayout.WEST);

        // Activities list
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);

        try {
            // Get recent data
            List<Reparation> recentReparations = gestionReparation.findAll().stream()
                    .sorted((r1, r2) -> r2.getDateDeCreation().compareTo(r1.getDateDeCreation()))
                    .limit(5)
                    .collect(Collectors.toList());

            List<Client> recentClients = gestionClient.findAll().stream()
                    .limit(3)
                    .collect(Collectors.toList());

            if (recentReparations.isEmpty() && recentClients.isEmpty()) {
                JLabel emptyLabel = new JLabel("Aucune activité récente");
                emptyLabel.setFont(UITheme.getBodyFont().deriveFont(14f));
                emptyLabel.setForeground(UITheme.TEXT_SECONDARY);
                emptyLabel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
                listPanel.add(emptyLabel);
            } else {
                // Show recent reparations
                for (Reparation reparation : recentReparations) {
                    JPanel activityPanel = new JPanel(new BorderLayout());
                    activityPanel.setOpaque(false);
                    activityPanel.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));

                    String activityText = "Réparation " + reparation.getCodeReparation() +
                            " - " + reparation.getStatut();
                    JLabel activityLabel = new JLabel(activityText);
                    activityLabel.setFont(UITheme.getBodyFont().deriveFont(13f));
                    activityLabel.setForeground(UITheme.TEXT_PRIMARY);

                    activityPanel.add(activityLabel, BorderLayout.CENTER);
                    listPanel.add(activityPanel);
                }

                // Show recent clients
                for (Client client : recentClients) {
                    JPanel activityPanel = new JPanel(new BorderLayout());
                    activityPanel.setOpaque(false);
                    activityPanel.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));

                    String activityText = "Nouveau client: " + client.getNom() + " " + client.getPrenom();
                    JLabel activityLabel = new JLabel(activityText);
                    activityLabel.setFont(UITheme.getBodyFont().deriveFont(13f));
                    activityLabel.setForeground(UITheme.TEXT_PRIMARY);

                    activityPanel.add(activityLabel, BorderLayout.CENTER);
                    listPanel.add(activityPanel);
                }
            }
        } catch (DatabaseException e) {
            JLabel errorLabel = new JLabel("Erreur de chargement des activités");
            errorLabel.setFont(UITheme.getBodyFont().deriveFont(14f));
            errorLabel.setForeground(UITheme.ERROR);
            errorLabel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
            listPanel.add(errorLabel);
        }

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(listPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createQuickActionsPanel() {
        RedCard panel = new RedCard(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setPreferredSize(new Dimension(400, 280));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));

        JLabel title = new JLabel("Actions Rapides");
        title.setFont(UITheme.getSubtitleFont().deriveFont(16f));
        title.setForeground(UITheme.TEXT_PRIMARY);

        headerPanel.add(title, BorderLayout.WEST);

        // Actions grid
        JPanel actionsGrid = new JPanel(new GridLayout(3, 2, 14, 14));
        actionsGrid.setOpaque(false);

        // Core actions based on available features
        String[][] actions = {
                { "Nouveau Client", "CLIENTS" },
                { "Nouvel Appareil", "APPAREILS" },
                { "Nouvelle Réparation", "REPARATIONS" },
                { "Ajouter Pièce", "PIECES" },
                { "Nouvelle Boutique", "BOUTIQUES" },
                { "Nouveau Réparateur", "REPARATEURS" }
        };

        for (String[] action : actions) {
            RedButton btn = new RedButton(action[0]);
            btn.setPreferredSize(new Dimension(180, 55));
            btn.setFont(UITheme.getBodyFont().deriveFont(14f));
            final String targetCard = action[1];
            btn.addActionListener(e -> cardLayout.show(mainPanel, targetCard));

            actionsGrid.add(btn);
        }

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(actionsGrid, BorderLayout.CENTER);

        return panel;
    }

    private String getCurrentUserName() {
        try {
            String userName = AuthService.getUserName();
            return (userName != null && !userName.trim().isEmpty()) ? userName : "Utilisateur";
        } catch (Exception e) {
            return "Utilisateur";
        }
    }
}