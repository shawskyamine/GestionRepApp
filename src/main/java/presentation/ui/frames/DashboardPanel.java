package presentation.ui.frames;

import javax.swing.*;
import java.awt.*;
import presentation.ui.components.*;
import presentation.ui.utils.UITheme;

public class DashboardPanel extends JPanel {
    public DashboardPanel() {
        setLayout(new BorderLayout());
        setBackground(UITheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        initComponents();
    }
    
    private void initComponents() {
        JLabel title = new JLabel("📊 Tableau de Bord");
        title.setFont(UITheme.getTitleFont());
        title.setForeground(UITheme.TEXT_DARK);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        JPanel statsPanel = createStatsPanel();
        JPanel recentPanel = createRecentPanel();
        JPanel actionsPanel = createActionsPanel();
        
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        
        content.add(title);
        content.add(statsPanel);
        content.add(Box.createRigidArea(new Dimension(0, 20)));
        content.add(recentPanel);
        content.add(Box.createRigidArea(new Dimension(0, 20)));
        content.add(actionsPanel);
        
        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(UITheme.BACKGROUND);
        
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 4, 15, 15));
        panel.setOpaque(false);
        
        String[] stats = {"Clients", "Appareils", "Réparations", "Pièces", 
                         "Réparateurs", "Boutiques", "Caisse (MAD)", "Emprunts"};
        String[] values = {"154", "89", "23", "156", "12", "5", "12,500", "3"};
        
        for (int i = 0; i < stats.length; i++) {
            panel.add(createStatCard(stats[i], values[i]));
        }
        
        return panel;
    }
    
    private JPanel createStatCard(String title, String value) {
        RedCard card = new RedCard(new BorderLayout());
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UITheme.getCardTitleFont());
        titleLabel.setForeground(UITheme.TEXT_LIGHT);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(UITheme.getCardValueFont());
        valueLabel.setForeground(UITheme.PRIMARY_RED);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createRecentPanel() {
        RedCard panel = new RedCard(new BorderLayout());
        
        JLabel title = new JLabel("📅 Activités Récentes");
        title.setFont(UITheme.getSubtitleFont());
        title.setForeground(UITheme.TEXT_DARK);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        String[] activities = {
            "✅ Nouveau client: Mohamed Ali",
            "🔧 Réparation #R-1023 terminée",
            "📱 Nouvel appareil ajouté: iPhone 13",
            "⚙️ Pièce commandée: Écran Samsung S21",
            "💰 Paiement reçu: 1200 MAD",
            "🔄 Réparation #R-1024 en cours",
            "👤 Réparateur Ahmed ajouté"
        };
        
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);
        
        for (String activity : activities) {
            JLabel activityLabel = new JLabel("• " + activity);
            activityLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            activityLabel.setForeground(UITheme.TEXT_DARK);
            activityLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
            listPanel.add(activityLabel);
        }
        
        panel.add(title, BorderLayout.NORTH);
        panel.add(listPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createActionsPanel() {
        RedCard panel = new RedCard(new BorderLayout());
        
        JLabel title = new JLabel("⚡ Actions Rapides");
        title.setFont(UITheme.getSubtitleFont());
        title.setForeground(UITheme.TEXT_DARK);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonsPanel.setOpaque(false);
        
        String[] actions = {"Nouveau Client", "Nouvel Appareil", "Nouvelle Réparation", 
                           "Ajouter Pièce", "Voir Rapports"};
        
        for (String action : actions) {
            RedButton btn = new RedButton(action);
            btn.addActionListener(e -> {
                JOptionPane.showMessageDialog(this, 
                    "Fonctionnalité à implémenter: " + action,
                    "Info", JOptionPane.INFORMATION_MESSAGE);
            });
            buttonsPanel.add(btn);
        }
        
        panel.add(title, BorderLayout.NORTH);
        panel.add(buttonsPanel, BorderLayout.CENTER);
        
        return panel;
    }
}