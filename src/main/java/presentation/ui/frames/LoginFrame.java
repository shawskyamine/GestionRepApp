package presentation.ui.frames;

import javax.swing.*;
import java.awt.*;
import presentation.ui.components.RedButton;
import presentation.ui.components.RedCard;
import presentation.ui.utils.UITheme;
import presentation.ui.utils.AuthService;
import metier.GestionUtilisateur;
import dao.Utilisateur;
import dao.Reparateur;
import metier.GestionReparateur;
import metier.GestionProprietaire;
import dao.Proprietaire;
import exception.DatabaseException;
import exception.EntityNotFoundException;
import presentation.ui.frames.ClientConsultationDialog;

public class LoginFrame extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JLabel errorLabel;
    private boolean isPasswordVisible = false;
    private GestionProprietaire gestionProprietaire;

    public LoginFrame() {
        UITheme.applyTheme();
        gestionProprietaire = new GestionProprietaire();

        setTitle("Connexion - Gestion Réparation");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setResizable(false);

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UITheme.BG_PRIMARY);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // Left side - Visual/Icon area
        JPanel leftPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Gradient background
                GradientPaint gradient = new GradientPaint(
                        0, 0,
                        new Color(UITheme.PRIMARY.getRed(), UITheme.PRIMARY.getGreen(), UITheme.PRIMARY.getBlue(), 20),
                        0, getHeight(),
                        new Color(UITheme.PRIMARY.getRed(), UITheme.PRIMARY.getGreen(), UITheme.PRIMARY.getBlue(), 5));
                g2.setPaint(gradient);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Decorative circles
                g2.setColor(
                        new Color(UITheme.PRIMARY.getRed(), UITheme.PRIMARY.getGreen(), UITheme.PRIMARY.getBlue(), 15));
                g2.fillOval(getWidth() / 4, getHeight() / 4, 150, 150);
                g2.fillOval(getWidth() * 3 / 4, getHeight() * 2 / 3, 100, 100);

                g2.dispose();
            }
        };
        leftPanel.setBackground(UITheme.BG_PRIMARY);
        leftPanel.setPreferredSize(new Dimension(400, 0));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(60, 60, 60, 60));

        // Icon/Logo area
        JPanel iconPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;
                int size = 120;

                // Draw tool/wrench icon
                g2.setColor(UITheme.PRIMARY);
                g2.setStroke(new BasicStroke(6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                // Wrench handle
                int handleLength = size / 2;
                int handleWidth = size / 6;
                g2.drawLine(centerX - handleWidth / 2, centerY - handleLength / 2,
                        centerX - handleWidth / 2, centerY + handleLength / 2);
                g2.drawLine(centerX + handleWidth / 2, centerY - handleLength / 2,
                        centerX + handleWidth / 2, centerY + handleLength / 2);

                // Wrench head (hexagon)
                int headSize = size / 3;
                int[] xPoints = new int[6];
                int[] yPoints = new int[6];
                for (int i = 0; i < 6; i++) {
                    double angle = Math.PI / 3 * i - Math.PI / 2;
                    xPoints[i] = (int) (centerX + headSize * Math.cos(angle));
                    yPoints[i] = (int) (centerY - handleLength / 2 + headSize * Math.sin(angle));
                }
                g2.drawPolygon(xPoints, yPoints, 6);

                g2.dispose();
            }
        };
        iconPanel.setOpaque(false);
        iconPanel.setPreferredSize(new Dimension(200, 200));

        JLabel leftTitle = new JLabel("Gestion de Réparation");
        leftTitle.setFont(UITheme.getTitleFont().deriveFont(32f));
        leftTitle.setForeground(UITheme.TEXT_PRIMARY);
        leftTitle.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel leftSubtitle = new JLabel("Système professionnel de gestion");
        leftSubtitle.setFont(UITheme.getBodyFont().deriveFont(16f));
        leftSubtitle.setForeground(UITheme.TEXT_TERTIARY);
        leftSubtitle.setHorizontalAlignment(SwingConstants.CENTER);
        leftSubtitle.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JPanel leftContent = new JPanel();
        leftContent.setLayout(new BoxLayout(leftContent, BoxLayout.Y_AXIS));
        leftContent.setOpaque(false);
        leftContent.add(Box.createVerticalGlue());
        leftContent.add(iconPanel);
        leftContent.add(Box.createRigidArea(new Dimension(0, 30)));
        leftContent.add(leftTitle);
        leftContent.add(leftSubtitle);
        leftContent.add(Box.createVerticalGlue());

        leftPanel.add(leftContent, BorderLayout.CENTER);

        // Right side - Login form
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(UITheme.BG_PRIMARY);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(60, 60, 60, 60));

        // Login card
        RedCard loginCard = new RedCard();
        loginCard.setLayout(new BoxLayout(loginCard, BoxLayout.Y_AXIS));
        loginCard.setMaximumSize(new Dimension(450, Integer.MAX_VALUE));
        loginCard.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel welcomeLabel = new JLabel("Connexion");
        welcomeLabel.setFont(UITheme.getHeadingFont().deriveFont(24f));
        welcomeLabel.setForeground(UITheme.TEXT_PRIMARY);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 32, 0));

        // Email field
        JLabel emailLabel = new JLabel("Email");
        emailLabel.setFont(UITheme.getLabelFont());
        emailLabel.setForeground(UITheme.TEXT_SECONDARY);
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        emailLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        emailField = new JTextField();
        emailField.setMaximumSize(new Dimension(450, 45));
        emailField.setFont(UITheme.getBodyFont());
        emailField.setBorder(UITheme.createModernBorder(UITheme.BORDER_MEDIUM, 10));

        // Password field
        JLabel passLabel = new JLabel("Mot de passe");
        passLabel.setFont(UITheme.getLabelFont());
        passLabel.setForeground(UITheme.TEXT_SECONDARY);
        passLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        passLabel.setBorder(BorderFactory.createEmptyBorder(24, 0, 10, 0));

        JPanel passwordPanel = new JPanel(new BorderLayout());
        passwordPanel.setOpaque(false);
        passwordPanel.setMaximumSize(new Dimension(450, 45));

        passwordField = new JPasswordField();
        passwordField.setFont(UITheme.getBodyFont());
        passwordField.setBorder(UITheme.createModernBorder(UITheme.BORDER_MEDIUM, 10));

        JButton toggleBtn = new JButton(isPasswordVisible ? "Masquer" : "Afficher");
        toggleBtn.setFont(UITheme.getSmallFont());
        toggleBtn.setForeground(UITheme.PRIMARY);
        toggleBtn.setBorderPainted(false);
        toggleBtn.setContentAreaFilled(false);
        toggleBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        toggleBtn.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        toggleBtn.addActionListener(e -> {
            isPasswordVisible = !isPasswordVisible;
            toggleBtn.setText(isPasswordVisible ? "Masquer" : "Afficher");
            if (isPasswordVisible) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar('•');
            }
        });

        passwordPanel.add(passwordField, BorderLayout.CENTER);
        passwordPanel.add(toggleBtn, BorderLayout.EAST);

        // Error label
        errorLabel = new JLabel(" ");
        errorLabel.setForeground(UITheme.ERROR);
        errorLabel.setFont(UITheme.getSmallFont());
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        errorLabel.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));

        // Login button
        RedButton loginButton = new RedButton("Se connecter");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setMaximumSize(new Dimension(450, 50));
        loginButton.setFont(UITheme.getButtonFont().deriveFont(16f));
        loginButton.addActionListener(e -> {
            String error = performLogin();
            errorLabel.setText(error != null ? error : " ");
        });

        // Sign up button
        RedButton signUpButton = new RedButton("S'inscrire");
        signUpButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        signUpButton.setMaximumSize(new Dimension(450, 50));
        signUpButton.setFont(UITheme.getButtonFont().deriveFont(16f));
        signUpButton.addActionListener(e -> showSignUpDialog());

        // Footer
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        footerPanel.setOpaque(false);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(24, 0, 0, 0));

        JButton clientButton = new JButton("Suivre ma réparation");
        clientButton.setForeground(UITheme.PRIMARY);
        clientButton.setBorderPainted(false);
        clientButton.setContentAreaFilled(false);
        clientButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clientButton.setFont(UITheme.getSmallFont());
        clientButton.addActionListener(e -> showClientConsultation());

        footerPanel.add(clientButton);

        // Assemble card
        loginCard.add(welcomeLabel);
        loginCard.add(emailLabel);
        loginCard.add(emailField);
        loginCard.add(passLabel);
        loginCard.add(passwordPanel);
        loginCard.add(errorLabel);
        loginCard.add(Box.createRigidArea(new Dimension(0, 12)));
        loginCard.add(loginButton);
        loginCard.add(Box.createRigidArea(new Dimension(0, 12)));
        loginCard.add(signUpButton);
        loginCard.add(footerPanel);

        // Center card in right panel
        JPanel rightCenter = new JPanel();
        rightCenter.setLayout(new BoxLayout(rightCenter, BoxLayout.Y_AXIS));
        rightCenter.setOpaque(false);
        rightCenter.add(Box.createVerticalGlue());
        rightCenter.add(loginCard);
        rightCenter.add(Box.createVerticalGlue());

        rightPanel.add(rightCenter, BorderLayout.CENTER);

        // Main layout - split view
        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(rightPanel, BorderLayout.CENTER);

        add(mainPanel);
        getRootPane().setDefaultButton(loginButton);
    }

    private String performLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty()) {
            return "Veuillez entrer votre email";
        }

        if (password.isEmpty()) {
            return "Veuillez entrer votre mot de passe";
        }

        try {
            AuthService.login(email, password);

            dispose();
            try {
                // Check if user is proprietaire
                if (AuthService.isProprietaire()) {
                    new BoutiqueSelectionFrame().setVisible(true);
                } else {
                    MainFrame mainFrame = new MainFrame();
                    mainFrame.setVisible(true);
                }
            } catch (Exception e) {
                UITheme.showStyledMessageDialog(null,
                        "Erreur lors de l'ouverture de l'application: " + e.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                new LoginFrame().setVisible(true);
            }
            return null;
        } catch (Exception e) {
            return "Erreur d'authentification: " + e.getMessage();
        }
    }

    private void showClientConsultation() {
        new ClientConsultationDialog(this).setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            createDefaultUsers();
            new LoginFrame().setVisible(true);
        });
    }

    private static void createDefaultUsers() {
        GestionUtilisateur gestionUtilisateur = new GestionUtilisateur();
        GestionReparateur gestionReparateur = new GestionReparateur();
        GestionProprietaire gestionProprietaire = new GestionProprietaire();

        try {
            // Check if proprietaire exists
            boolean proprietaireExists = !gestionProprietaire.findByEmail("proprietaire@test.com").isEmpty();

            if (!proprietaireExists) {
                System.out.println("Creating default users...");

                // Create proprietaire
                Proprietaire proprietaire = Proprietaire.builder()
                        .nom("Fatima")
                        .prenom("Propriétaire")
                        .email("proprietaire@test.com")
                        .password("test123")
                        .role("PROPRIETAIRE")
                        .build();
                gestionProprietaire.add(proprietaire);

                // Create reparateur
                Reparateur reparateur = Reparateur.builder()
                        .nom("Ali")
                        .prenom("Réparateur")
                        .email("reparateur@test.com")
                        .password("test123")
                        .role("REPARATEUR")
                        .pourcentage(10)
                        .telephone("0612345678")
                        .build();
                gestionReparateur.add(reparateur);

                // Create magasinier
                Utilisateur magasinier = Utilisateur.builder()
                        .nom("Samir")
                        .prenom("Magasinier")
                        .email("magasinier@test.com")
                        .password("test123")
                        .role("MAGASINIER")
                        .build();
                gestionUtilisateur.create(magasinier);

                // Create admin
                Utilisateur admin = Utilisateur.builder()
                        .nom("Admin")
                        .prenom("System")
                        .email("admin@test.com")
                        .password("admin123")
                        .role("ADMIN")
                        .build();
                gestionUtilisateur.create(admin);

                System.out.println("Default users created successfully.");
            }

        } catch (Exception e) {
            System.err.println("Error creating default users: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showSignUpDialog() {
        JDialog dialog = new JDialog(this, "Inscription Propriétaire", true);
        dialog.setSize(500, 700);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        mainPanel.setBackground(UITheme.BG_SECONDARY);

        // Title
        JLabel titleLabel = new JLabel("Créer un compte Propriétaire");
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

        // Nom
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        JLabel nomLabel = new JLabel("Nom");
        nomLabel.setFont(UITheme.getLabelFont());
        nomLabel.setForeground(UITheme.TEXT_SECONDARY);
        formPanel.add(nomLabel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JTextField nomField = new JTextField();
        nomField.setPreferredSize(new Dimension(250, 40));
        nomField.setFont(UITheme.getBodyFont());
        nomField.setBorder(UITheme.createModernBorder(UITheme.BORDER_MEDIUM, 8));
        formPanel.add(nomField, gbc);

        // Prénom
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        JLabel prenomLabel = new JLabel("Prénom");
        prenomLabel.setFont(UITheme.getLabelFont());
        prenomLabel.setForeground(UITheme.TEXT_SECONDARY);
        formPanel.add(prenomLabel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JTextField prenomField = new JTextField();
        prenomField.setPreferredSize(new Dimension(250, 40));
        prenomField.setFont(UITheme.getBodyFont());
        prenomField.setBorder(UITheme.createModernBorder(UITheme.BORDER_MEDIUM, 8));
        formPanel.add(prenomField, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.3;
        JLabel emailLabel = new JLabel("Email");
        emailLabel.setFont(UITheme.getLabelFont());
        emailLabel.setForeground(UITheme.TEXT_SECONDARY);
        formPanel.add(emailLabel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JTextField signupEmailField = new JTextField();
        signupEmailField.setPreferredSize(new Dimension(250, 40));
        signupEmailField.setFont(UITheme.getBodyFont());
        signupEmailField.setBorder(UITheme.createModernBorder(UITheme.BORDER_MEDIUM, 8));
        formPanel.add(signupEmailField, gbc);

        // Mot de passe
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.3;
        JLabel passLabel = new JLabel("Mot de passe");
        passLabel.setFont(UITheme.getLabelFont());
        passLabel.setForeground(UITheme.TEXT_SECONDARY);
        formPanel.add(passLabel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JPasswordField signupPasswordField = new JPasswordField();
        signupPasswordField.setPreferredSize(new Dimension(250, 40));
        signupPasswordField.setFont(UITheme.getBodyFont());
        signupPasswordField.setBorder(UITheme.createModernBorder(UITheme.BORDER_MEDIUM, 8));
        formPanel.add(signupPasswordField, gbc);

        // Confirmer mot de passe
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.3;
        JLabel confirmLabel = new JLabel("Confirmer mot de passe");
        confirmLabel.setFont(UITheme.getLabelFont());
        confirmLabel.setForeground(UITheme.TEXT_SECONDARY);
        formPanel.add(confirmLabel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JPasswordField confirmPasswordField = new JPasswordField();
        confirmPasswordField.setPreferredSize(new Dimension(250, 40));
        confirmPasswordField.setFont(UITheme.getBodyFont());
        confirmPasswordField.setBorder(UITheme.createModernBorder(UITheme.BORDER_MEDIUM, 8));
        formPanel.add(confirmPasswordField, gbc);

        // Error label
        JLabel signupErrorLabel = new JLabel(" ");
        signupErrorLabel.setForeground(UITheme.ERROR);
        signupErrorLabel.setFont(UITheme.getSmallFont());
        signupErrorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        signupErrorLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));

        RedButton signupButton = new RedButton("S'inscrire");
        signupButton.setPreferredSize(new Dimension(140, 45));
        signupButton.addActionListener(e -> {
            String error = null;
            try {
                error = gestionProprietaire.signUp(
                    nomField.getText().trim(), 
                    prenomField.getText().trim(),
                    signupEmailField.getText().trim(), 
                    new String(signupPasswordField.getPassword()),
                    new String(confirmPasswordField.getPassword())
                );
            } catch (Exception ex) {
                error = "Erreur lors de l'inscription: " + ex.getMessage();
            }
            
            signupErrorLabel.setText(error != null ? error : " ");
            if (error == null) {
                dialog.dispose();
                UITheme.showStyledMessageDialog(this, 
                    "Compte créé avec succès! Vous pouvez maintenant vous connecter.",
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        RedButton cancelButton = new RedButton("Annuler");
        cancelButton.setPreferredSize(new Dimension(120, 45));
        cancelButton.setBackground(UITheme.SURFACE);
        cancelButton.setForeground(UITheme.TEXT_SECONDARY);
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(signupButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(titleLabel);
        mainPanel.add(formPanel);
        mainPanel.add(signupErrorLabel);
        mainPanel.add(buttonPanel);

        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }


}