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

public class LoginFrame extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JLabel errorLabel;
    private boolean isPasswordVisible = false;

    public LoginFrame() {
        UITheme.applyTheme();

        setTitle("Connexion - Gestion Réparation");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 650);
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
                    0, 0, new Color(UITheme.PRIMARY.getRed(), UITheme.PRIMARY.getGreen(), UITheme.PRIMARY.getBlue(), 20),
                    0, getHeight(), new Color(UITheme.PRIMARY.getRed(), UITheme.PRIMARY.getGreen(), UITheme.PRIMARY.getBlue(), 5)
                );
                g2.setPaint(gradient);
                g2.fillRect(0, 0, getWidth(), getHeight());
                
                // Decorative circles
                g2.setColor(new Color(UITheme.PRIMARY.getRed(), UITheme.PRIMARY.getGreen(), UITheme.PRIMARY.getBlue(), 15));
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
                g2.drawLine(centerX - handleWidth/2, centerY - handleLength/2, 
                           centerX - handleWidth/2, centerY + handleLength/2);
                g2.drawLine(centerX + handleWidth/2, centerY - handleLength/2, 
                           centerX + handleWidth/2, centerY + handleLength/2);
                
                // Wrench head (hexagon)
                int headSize = size / 3;
                int[] xPoints = new int[6];
                int[] yPoints = new int[6];
                for (int i = 0; i < 6; i++) {
                    double angle = Math.PI / 3 * i - Math.PI / 2;
                    xPoints[i] = (int)(centerX + headSize * Math.cos(angle));
                    yPoints[i] = (int)(centerY - handleLength/2 + headSize * Math.sin(angle));
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

        // Footer
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        footerPanel.setOpaque(false);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(24, 0, 0, 0));

        JButton testButton = new JButton("Comptes de test");
        testButton.setForeground(UITheme.PRIMARY);
        testButton.setBorderPainted(false);
        testButton.setContentAreaFilled(false);
        testButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        testButton.setFont(UITheme.getSmallFont());
        testButton.addActionListener(e -> showTestCredentials());

        JLabel separator = new JLabel("•");
        separator.setForeground(UITheme.TEXT_TERTIARY);
        separator.setFont(UITheme.getSmallFont());

        JButton clientButton = new JButton("Suivre ma réparation");
        clientButton.setForeground(UITheme.PRIMARY);
        clientButton.setBorderPainted(false);
        clientButton.setContentAreaFilled(false);
        clientButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clientButton.setFont(UITheme.getSmallFont());
        clientButton.addActionListener(e -> showClientConsultation());

        footerPanel.add(testButton);
        footerPanel.add(separator);
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
            boolean success = AuthService.login(email, password);

            if (success) {
                dispose();
                try {
                    MainFrame mainFrame = new MainFrame();
                    mainFrame.setVisible(true);
                } catch (Exception e) {
                    UITheme.showStyledMessageDialog(null,
                            "Erreur lors de l'ouverture de l'application: " + e.getMessage(),
                            "Erreur", JOptionPane.ERROR_MESSAGE);
                    new LoginFrame().setVisible(true);
                }
                return null;
            } else {
                return "Email ou mot de passe incorrect";
            }
        } catch (Exception e) {
            return "Erreur de connexion: " + e.getMessage();
        }
    }

    private void showTestCredentials() {
        String message = "<html><div style='width: 300px;'>" +
                "<h3>Comptes de test:</h3>" +
                "<b>Réparateur:</b><br>" +
                "• Email: <code>reparateur@test.com</code><br>" +
                "• Mot de passe: <code>test123</code><br><br>" +
                "<b>Propriétaire:</b><br>" +
                "• Email: <code>proprietaire@test.com</code><br>" +
                "• Mot de passe: <code>test123</code><br><br>" +
                "<b>Magasinier:</b><br>" +
                "• Email: <code>magasinier@test.com</code><br>" +
                "• Mot de passe: <code>test123</code><br><br>" +
                "<b>Admin:</b><br>" +
                "• Email: <code>admin@test.com</code><br>" +
                "• Mot de passe: <code>admin123</code>" +
                "</div></html>";

        UITheme.showStyledMessageDialog(this, message, "Comptes de test", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showClientConsultation() {
        new ClientConsultationDialog(this).setVisible(true);
    }

    public static void main(String[] args) {
        GestionUtilisateur gestionUtilisateur = new GestionUtilisateur();
        GestionReparateur gestionReparateur = new GestionReparateur();
        
        if (gestionUtilisateur.findByEmail("proprietaire@test.com") == null) {
            System.out.println("Creating default users...");
            
            Utilisateur proprietaire = Utilisateur.builder()
                    .nom("Fatima")
                    .prenom("Propriétaire")
                    .email("proprietaire@test.com")
                    .password("test123")
                    .role("PROPRIETAIRE")
                    .build();
            gestionUtilisateur.create(proprietaire);

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

            Utilisateur magasinier = Utilisateur.builder()
                    .nom("Samir")
                    .prenom("Magasinier")
                    .email("magasinier@test.com")
                    .password("test123")
                    .role("MAGASINIER")
                    .build();
            gestionUtilisateur.create(magasinier);

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

        if (gestionReparateur.findByEmail("reparateur@test.com") == null) {
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
        }

        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
