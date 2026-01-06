package presentation.ui.frames;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import presentation.ui.components.RedButton;
import presentation.ui.components.RedCard;
import presentation.ui.utils.UITheme;
import presentation.ui.utils.AuthService;
import metier.GestionBoutique;
import metier.GestionProprietaire;
import dao.Boutique;
import dao.Proprietaire;
import dao.Utilisateur;
import exception.DatabaseException;

public class BoutiqueSelectionFrame extends JFrame {
    private GestionBoutique gestionBoutique;
    private GestionProprietaire gestionProprietaire;
    private JPanel boutiquesPanel;
    private Proprietaire currentProprietaire;

    public BoutiqueSelectionFrame() {
        System.out.println("BoutiqueSelectionFrame constructor called");
        UITheme.applyTheme();
        gestionBoutique = new GestionBoutique();
        gestionProprietaire = new GestionProprietaire();

        // Get current proprietaire
        Utilisateur currentUser = AuthService.getCurrentUser();
        System.out.println("Current user from AuthService: "
                + (currentUser != null ? currentUser.getEmail() + " (" + currentUser.getClass().getSimpleName() + ")"
                        : "null"));

        if (currentUser instanceof Proprietaire) {
            currentProprietaire = (Proprietaire) currentUser;
            System.out.println("Current user is Proprietaire instance: " + currentProprietaire.getEmail());
        } else {
            // Fallback to finding by email
            List<Proprietaire> proprietaires = gestionProprietaire.findByEmail(currentUser.getEmail());
            if (!proprietaires.isEmpty()) {
                currentProprietaire = proprietaires.get(0);
                System.out.println("Found proprietaire by email: " + currentProprietaire.getEmail());
            } else {
                System.out.println("No proprietaire found for email: " + currentUser.getEmail());
                JOptionPane.showMessageDialog(null, "Erreur: Aucun propriétaire trouvé pour cet utilisateur.", "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                new LoginFrame().setVisible(true);
                dispose();
                return;
            }
        }

        setTitle("Sélection de Boutique - Gestion Réparation");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setResizable(false);

        initComponents();
        loadBoutiques();
        System.out.println("BoutiqueSelectionFrame initialization completed");
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UITheme.BG_PRIMARY);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UITheme.BG_PRIMARY);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        JLabel titleLabel = new JLabel("Sélectionnez votre Boutique");
        titleLabel.setFont(UITheme.getTitleFont().deriveFont(28f));
        titleLabel.setForeground(UITheme.TEXT_PRIMARY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        RedButton createBoutiqueBtn = new RedButton("Créer une nouvelle Boutique");
        createBoutiqueBtn.setPreferredSize(new Dimension(280, 50));
        createBoutiqueBtn.addActionListener(e -> showCreateBoutiqueDialog());

        RedButton refreshBtn = new RedButton("Actualiser");
        refreshBtn.setPreferredSize(new Dimension(170, 50));
        refreshBtn.addActionListener(e -> loadBoutiques());

        JPanel headerEast = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        headerEast.setOpaque(false);
        headerEast.add(refreshBtn);
        headerEast.add(createBoutiqueBtn);

        JPanel headerCenter = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerCenter.setOpaque(false);
        headerCenter.add(titleLabel);

        headerPanel.add(headerCenter, BorderLayout.CENTER);
        headerPanel.add(headerEast, BorderLayout.EAST);

        // Boutiques list
        RedCard boutiquesCard = new RedCard();
        boutiquesCard.setLayout(new BorderLayout());
        boutiquesCard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel listTitle = new JLabel("Vos Boutiques");
        listTitle.setFont(UITheme.getHeadingFont().deriveFont(20f));
        listTitle.setForeground(UITheme.TEXT_PRIMARY);
        listTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        boutiquesPanel = new JPanel();
        boutiquesPanel.setLayout(new BoxLayout(boutiquesPanel, BoxLayout.Y_AXIS));
        boutiquesPanel.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(boutiquesPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.add(listTitle, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        boutiquesCard.add(contentPanel, BorderLayout.CENTER);

        // Footer
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setOpaque(false);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        RedButton logoutBtn = new RedButton("Déconnexion");
        logoutBtn.setPreferredSize(new Dimension(170, 50));
        logoutBtn.setBackground(UITheme.SURFACE);
        logoutBtn.setForeground(UITheme.TEXT_SECONDARY);
        logoutBtn.addActionListener(e -> logout());

        footerPanel.add(logoutBtn);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(boutiquesCard, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void loadBoutiques() {
        System.out.println("loadBoutiques called");
        boutiquesPanel.removeAll();

        if (currentProprietaire == null) {
            JLabel noProprietaireLabel = new JLabel("Erreur: Propriétaire non trouvé");
            noProprietaireLabel.setFont(UITheme.getBodyFont());
            noProprietaireLabel.setForeground(UITheme.ERROR);
            boutiquesPanel.add(noProprietaireLabel);
            System.out.println("currentProprietaire is null");
            return;
        }

        System.out.println("Loading boutiques for proprietaire: " + currentProprietaire.getEmail() + " (ID: "
                + currentProprietaire.getId() + ")");
        try {
            List<Boutique> boutiques = gestionBoutique.findByProprietaire(currentProprietaire.getId());
            System.out.println("Found " + (boutiques != null ? boutiques.size() : "null") + " boutiques");

            if (boutiques == null || boutiques.isEmpty()) {
                JPanel emptyPanel = new JPanel(new BorderLayout());
                emptyPanel.setOpaque(false);
                emptyPanel.setBorder(BorderFactory.createEmptyBorder(40, 20, 40, 20));

                JLabel emptyLabel = new JLabel("Aucune boutique trouvée");
                emptyLabel.setFont(UITheme.getBodyFont().deriveFont(16f));
                emptyLabel.setForeground(UITheme.TEXT_SECONDARY);
                emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);

                JLabel hintLabel = new JLabel("Créez votre première boutique pour commencer");
                hintLabel.setFont(UITheme.getSmallFont());
                hintLabel.setForeground(UITheme.TEXT_TERTIARY);
                hintLabel.setHorizontalAlignment(SwingConstants.CENTER);
                hintLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

                emptyPanel.add(emptyLabel, BorderLayout.CENTER);
                emptyPanel.add(hintLabel, BorderLayout.SOUTH);

                boutiquesPanel.add(emptyPanel);
            } else {
                for (Boutique boutique : boutiques) {
                    System.out.println("Adding boutique: " + boutique.getNomboutique());
                    JPanel boutiqueItem = createBoutiqueItem(boutique);
                    boutiquesPanel.add(boutiqueItem);
                    boutiquesPanel.add(Box.createRigidArea(new Dimension(0, 20)));
                }
            }
        } catch (DatabaseException e) {
            System.out.println("DatabaseException in loadBoutiques: " + e.getMessage());
            e.printStackTrace();
            JLabel errorLabel = new JLabel("Erreur de chargement des boutiques: " + e.getMessage());
            errorLabel.setFont(UITheme.getBodyFont());
            errorLabel.setForeground(UITheme.ERROR);
            boutiquesPanel.add(errorLabel);
        }

        boutiquesPanel.revalidate();
        boutiquesPanel.repaint();
        System.out.println("loadBoutiques completed");
    }

    private JPanel createBoutiqueItem(Boutique boutique) {
        // Create a RedCard for each boutique
        RedCard card = new RedCard();
        card.setLayout(new BorderLayout());
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        card.setPreferredSize(new Dimension(card.getPreferredSize().width, 120));

        // Hover effect
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBorderColor(UITheme.PRIMARY);
                card.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBorderColor(UITheme.BORDER_MEDIUM);
                card.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                selectBoutique(boutique);
            }
        });

        // Left side - Boutique icon
        JPanel iconPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;
                int size = 40;

                // Draw shop/store icon with better styling
                g2.setColor(UITheme.PRIMARY);
                g2.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                // Building outline
                g2.drawRect(centerX - size / 2, centerY - size / 2, size, size);

                // Door
                g2.drawLine(centerX, centerY - size / 2, centerX, centerY + size / 2);

                // Windows with better positioning
                int windowSize = 8;
                int offset = size / 4;
                g2.fillRect(centerX - offset - windowSize / 2, centerY - offset - windowSize / 2, windowSize,
                        windowSize);
                g2.fillRect(centerX + offset - windowSize / 2, centerY - offset - windowSize / 2, windowSize,
                        windowSize);

                g2.dispose();
            }
        };
        iconPanel.setOpaque(false);
        iconPanel.setPreferredSize(new Dimension(70, 70));

        // Center - Boutique info
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setOpaque(false);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Boutique name with elegant styling
        JLabel nameLabel = new JLabel(boutique.getNomboutique());
        nameLabel.setFont(UITheme.getTitleFont().deriveFont(Font.BOLD, 22f));
        nameLabel.setForeground(UITheme.TEXT_PRIMARY);
        nameLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        // Address with clean styling
        JPanel addressPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        addressPanel.setOpaque(false);

        JLabel addressLabel = new JLabel(
                boutique.getAdresse() != null ? boutique.getAdresse() : "Adresse non spécifiée");
        addressLabel.setFont(UITheme.getBodyFont().deriveFont(15f));
        addressLabel.setForeground(UITheme.TEXT_SECONDARY);

        addressPanel.add(addressLabel);

        // Creation date with clean styling
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        datePanel.setOpaque(false);

        String dateStr = "";
        if (boutique.getDateDeCreation() != null) {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
            dateStr = "Créée le " + sdf.format(boutique.getDateDeCreation());
        }

        JLabel dateLabel = new JLabel(dateStr);
        dateLabel.setFont(UITheme.getSmallFont().deriveFont(13f));
        dateLabel.setForeground(UITheme.TEXT_TERTIARY);

        datePanel.add(dateLabel);

        infoPanel.add(nameLabel, BorderLayout.NORTH);
        infoPanel.add(addressPanel, BorderLayout.CENTER);
        infoPanel.add(datePanel, BorderLayout.SOUTH);

        // Right side - Arrow and select indicator
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel arrowLabel = new JLabel("▶");
        arrowLabel.setFont(UITheme.getHeadingFont().deriveFont(Font.BOLD, 28f));
        arrowLabel.setForeground(UITheme.PRIMARY);

        JLabel selectLabel = new JLabel("Sélectionner");
        selectLabel.setFont(UITheme.getSmallFont().deriveFont(Font.BOLD, 12f));
        selectLabel.setForeground(UITheme.TEXT_TERTIARY);

        rightPanel.add(arrowLabel, BorderLayout.CENTER);
        rightPanel.add(selectLabel, BorderLayout.SOUTH);

        // Assemble card
        card.add(iconPanel, BorderLayout.WEST);
        card.add(infoPanel, BorderLayout.CENTER);
        card.add(rightPanel, BorderLayout.EAST);

        return card;
    }

    private void selectBoutique(Boutique boutique) {
        System.out.println("BoutiqueSelectionFrame.selectBoutique called with boutique: " + boutique.getNomboutique());

        // Store selected boutique in AuthService
        AuthService.setSelectedBoutique(boutique);
        System.out.println("Selected boutique set in AuthService: " + AuthService.getSelectedBoutiqueName());

        // Close current frame
        dispose();
        System.out.println("BoutiqueSelectionFrame disposed");

        // Open MainFrame
        try {
            System.out.println("Creating MainFrame...");
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
            System.out.println("MainFrame created and set visible");
        } catch (Exception e) {
            System.out.println("Exception creating MainFrame: " + e.getMessage());
            e.printStackTrace();
            // Fallback to login frame
            new LoginFrame().setVisible(true);
        }
    }

    private void showCreateBoutiqueDialog() {
        JDialog dialog = new JDialog(this, "Créer une nouvelle Boutique", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        mainPanel.setBackground(UITheme.BG_SECONDARY);

        // Title
        JLabel titleLabel = new JLabel("Créer une nouvelle Boutique");
        titleLabel.setFont(UITheme.getHeadingFont().deriveFont(20f));
        titleLabel.setForeground(UITheme.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        // Form fields
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Nom boutique
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        JLabel nomLabel = new JLabel("Nom de la boutique");
        nomLabel.setFont(UITheme.getLabelFont());
        nomLabel.setForeground(UITheme.TEXT_SECONDARY);
        formPanel.add(nomLabel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JTextField nomField = new JTextField();
        nomField.setPreferredSize(new Dimension(280, 45));
        nomField.setFont(UITheme.getBodyFont());
        nomField.setBorder(UITheme.createModernBorder(UITheme.BORDER_MEDIUM, 8));
        formPanel.add(nomField, gbc);

        // Adresse
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        JLabel adresseLabel = new JLabel("Adresse");
        adresseLabel.setFont(UITheme.getLabelFont());
        adresseLabel.setForeground(UITheme.TEXT_SECONDARY);
        formPanel.add(adresseLabel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JTextField adresseField = new JTextField();
        adresseField.setPreferredSize(new Dimension(280, 45));
        adresseField.setFont(UITheme.getBodyFont());
        adresseField.setBorder(UITheme.createModernBorder(UITheme.BORDER_MEDIUM, 8));
        formPanel.add(adresseField, gbc);

        // Error label
        JLabel errorLabel = new JLabel(" ");
        errorLabel.setForeground(UITheme.ERROR);
        errorLabel.setFont(UITheme.getSmallFont());
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        errorLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));

        RedButton createBtn = new RedButton("Créer");
        createBtn.setPreferredSize(new Dimension(140, 50));
        createBtn.addActionListener(e -> {
            String error = createBoutique(nomField.getText().trim(), adresseField.getText().trim());
            errorLabel.setText(error != null ? error : " ");
            if (error == null) {
                dialog.dispose();
                loadBoutiques(); // Refresh the list
            }
        });

        RedButton cancelBtn = new RedButton("Annuler");
        cancelBtn.setPreferredSize(new Dimension(140, 50));
        cancelBtn.setBackground(UITheme.SURFACE);
        cancelBtn.setForeground(UITheme.TEXT_SECONDARY);
        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(createBtn);
        buttonPanel.add(cancelBtn);

        mainPanel.add(titleLabel);
        mainPanel.add(formPanel);
        mainPanel.add(errorLabel);
        mainPanel.add(buttonPanel);

        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private String createBoutique(String nom, String adresse) {
        if (nom.isEmpty()) {
            return "Le nom de la boutique est obligatoire";
        }

        try {
            Boutique boutique = Boutique.builder()
                    .nomboutique(nom)
                    .adresse(adresse)
                    .dateDeCreation(new java.util.Date())
                    .proprietaire(currentProprietaire)
                    .build();

            gestionBoutique.add(boutique);
            return null;
        } catch (Exception e) {
            return "Erreur lors de la création de la boutique: " + e.getMessage();
        }
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