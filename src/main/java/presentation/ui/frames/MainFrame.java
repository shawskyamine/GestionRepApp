package presentation.ui.frames;

import javax.swing.*;
import java.awt.*;
import presentation.ui.panels.*;
import presentation.ui.components.*;
import presentation.ui.utils.UITheme;
import presentation.ui.utils.AuthService;
import exception.DatabaseException;
import exception.AuthorizationException;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public MainFrame() {
        System.out.println("MainFrame constructor called");
        try {
            if (!AuthService.isLoggedIn()) {
                System.out.println("User not logged in, showing login frame");
                JOptionPane.showMessageDialog(null,
                        "Session expirée. Veuillez vous reconnecter.",
                        "Session expirée",
                        JOptionPane.WARNING_MESSAGE);
                new LoginFrame().setVisible(true);
                return;
            }

            // Check if proprietaire has selected a boutique
            if (AuthService.isProprietaire() && AuthService.getSelectedBoutique() == null) {
                System.out.println("Proprietaire logged in but no boutique selected, showing boutique selection");
                System.out.println("Current user: " + AuthService.getCurrentUser());
                System.out.println("Is proprietaire: " + AuthService.isProprietaire());
                System.out.println("Selected boutique: " + AuthService.getSelectedBoutique());
                new BoutiqueSelectionFrame().setVisible(true);
                return;
            }

            System.out.println("User is logged in and has selected boutique (if proprietaire), initializing MainFrame");
            System.out.println("Selected boutique: " + AuthService.getSelectedBoutiqueName());
            UITheme.applyTheme();

            setTitle("Gestion Réparation - " + AuthService.getUserName());
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setExtendedState(JFrame.MAXIMIZED_BOTH);
            setSize(1600, 1000);
            setLocationRelativeTo(null);

            initComponents();
            System.out.println("MainFrame initialization completed successfully");
        } catch (Exception e) {
            System.out.println("Exception in MainFrame constructor: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erreur lors de l'initialisation de l'application: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initComponents() {
        try {
            setLayout(new BorderLayout());

            // Header
            JPanel headerPanel = createHeader();
            add(headerPanel, BorderLayout.NORTH);

            // Sidebar
            JPanel sidebarPanel = createSidebar();
            JScrollPane sidebarScroll = new JScrollPane(sidebarPanel);
            sidebarScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            sidebarScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            sidebarScroll.setBorder(BorderFactory.createEmptyBorder());
            add(sidebarScroll, BorderLayout.WEST);

            // Main content
            cardLayout = new CardLayout();
            mainPanel = new JPanel(cardLayout);
            mainPanel.setBackground(UITheme.BACKGROUND);

            // Add panels
            System.out.println("Creating panels...");
            mainPanel.add(new DashboardPanel(cardLayout, mainPanel), "DASHBOARD");
            System.out.println("DashboardPanel created");
            mainPanel.add(new ClientPanel(), "CLIENTS");
            System.out.println("ClientPanel created");
            mainPanel.add(new AppareilPanel(), "APPAREILS");
            System.out.println("AppareilPanel created");
            mainPanel.add(new ReparationPanel(), "REPARATIONS");
            System.out.println("ReparationPanel created");
            mainPanel.add(new PiecePanel(), "PIECES");
            System.out.println("PiecePanel created");
            
            // CaissePanel - handle exceptions properly
            try {
                mainPanel.add(new CaissePanel(), "CAISSE");
                System.out.println("CaissePanel created");
            } catch (Exception e) {
                System.out.println("Exception creating CaissePanel: " + e.getMessage());
                e.printStackTrace();
                // Don't show error dialog here, just continue without CaissePanel
                // Some users might not have access to Caisse
            }
            
            // BoutiquePanel - handle exceptions properly
            try {
                mainPanel.add(new BoutiquePanel(), "BOUTIQUES");
                System.out.println("BoutiquePanel created");
            } catch (Exception e) {
                System.out.println("Exception creating BoutiquePanel: " + e.getMessage());
                e.printStackTrace();
                // Continue without BoutiquePanel
            }
            
            // ReparateurPanel - handle exceptions properly
            try {
                mainPanel.add(new ReparateurPanel(), "REPARATEURS");
                System.out.println("ReparateurPanel created");
            } catch (Exception e) {
                System.out.println("Exception creating ReparateurPanel: " + e.getMessage());
                e.printStackTrace();
                // Continue without ReparateurPanel
            }

            JScrollPane scrollPane = new JScrollPane(mainPanel);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            scrollPane.getViewport().setBackground(UITheme.BACKGROUND);
            add(scrollPane, BorderLayout.CENTER);

            cardLayout.show(mainPanel, "DASHBOARD");
            System.out.println("initComponents completed");
        } catch (Exception e) {
            System.out.println("Exception in initComponents: " + e.getMessage());
            e.printStackTrace();
            throw e; // Re-throw to be caught by constructor
        }
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.BG_PRIMARY);
        header.setPreferredSize(new Dimension(1600, 120));
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER_DARK),
                BorderFactory.createEmptyBorder(20, 32, 20, 32)));

        // Left side - Main title
        JLabel title = new JLabel("GESTION DE RÉPARATION");
        title.setFont(UITheme.getTitleFont().deriveFont(26f));
        title.setForeground(UITheme.TEXT_PRIMARY);

        // Center - Boutique name (for proprietaires)
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10)); // Center with 10px vertical gap
        centerPanel.setOpaque(false);

        if (AuthService.isProprietaire() && AuthService.getSelectedBoutiqueName() != null) {
            System.out.println("Adding BoutiqueBadge to header with name: " + AuthService.getSelectedBoutiqueName());
            // Make sure BoutiqueBadge exists in presentation.ui.components
            BoutiqueBadge boutiqueBadge = new BoutiqueBadge(AuthService.getSelectedBoutiqueName());
            centerPanel.add(boutiqueBadge);
        } else {
            System.out.println("Not adding BoutiqueBadge - isProprietaire: " + AuthService.isProprietaire()
                    + ", boutiqueName: " + AuthService.getSelectedBoutiqueName());
        }

        // Right side - User info and logout
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 0));
        userPanel.setOpaque(false);

        String roleText = "Utilisateur"; // Default text

        if (AuthService.isProprietaire()) {
            roleText = "Propriétaire";
        } else if (AuthService.isReparateur()) {
            roleText = "Réparateur";
        } else if (AuthService.isMagasinier()) {
            roleText = "Magasinier";
        }

        JLabel userLabel = new JLabel(AuthService.getUserName() + " | " + roleText);
        userLabel.setForeground(UITheme.TEXT_SECONDARY);
        userLabel.setFont(UITheme.getBodyFont().deriveFont(15f));

        RedButton logoutBtn = new RedButton("Déconnexion");
        logoutBtn.setPreferredSize(new Dimension(160, 46));
        logoutBtn.addActionListener(e -> logout());

        userPanel.add(userLabel);
        userPanel.add(Box.createRigidArea(new Dimension(12, 0)));
        userPanel.add(logoutBtn);

        header.add(title, BorderLayout.WEST);
        header.add(centerPanel, BorderLayout.CENTER);
        header.add(userPanel, BorderLayout.EAST);

        return header;
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(UITheme.SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(28, 16, 28, 16));

        // Common items
        String[] items = { "Dashboard", "Clients", "Appareils",
                "Réparations", "Pièces", "Caisse",
                "Boutiques", "Réparateurs" };
        String[] cards = { "DASHBOARD", "CLIENTS", "APPAREILS", "REPARATIONS",
                "PIECES", "CAISSE", "BOUTIQUES", "REPARATEURS" };

        for (int i = 0; i < items.length; i++) {
            boolean visible = false;
            String currentCard = cards[i];

            if (AuthService.isProprietaire()) {
                visible = true;
            } else if (AuthService.isReparateur()) {
                if (currentCard.equals("DASHBOARD") || currentCard.equals("CLIENTS") ||
                        currentCard.equals("APPAREILS") || currentCard.equals("REPARATIONS") ||
                        currentCard.equals("CAISSE") || currentCard.equals("REPARATEURS")) {
                    visible = true;
                }
            } else if (AuthService.isMagasinier()) {
                if (currentCard.equals("DASHBOARD") || currentCard.equals("CLIENTS") ||
                        currentCard.equals("PIECES")) {
                    visible = true;
                }
            }

            if (visible) {
                RedNavButton button = new RedNavButton(items[i]);
                final String cardName = cards[i];
                button.addActionListener(e -> cardLayout.show(mainPanel, cardName));

                JPanel buttonWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
                buttonWrapper.setOpaque(false);
                buttonWrapper.add(button);
                sidebar.add(buttonWrapper);
                sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
            }
        }

        sidebar.add(Box.createVerticalGlue());

        // Version info
        JLabel versionLabel = new JLabel("v1.0.0 | " + AuthService.getCurrentUser());
        versionLabel.setFont(UITheme.getCaptionFont());
        versionLabel.setForeground(UITheme.TEXT_TERTIARY);
        versionLabel.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));

        JPanel versionWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        versionWrapper.setOpaque(false);
        versionWrapper.add(versionLabel);
        sidebar.add(versionWrapper);

        return sidebar;
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Voulez-vous vraiment vous déconnecter?",
                "Déconnexion",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            AuthService.logout();
            dispose();
            new LoginFrame().setVisible(true);
        }
    }
}