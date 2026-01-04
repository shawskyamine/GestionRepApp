# Gestion Réparation - Application Modernisée

## Description

Application de gestion des réparations avec interface moderne et fonctionnalités complètes.

## Fonctionnalités Principales

### 🔐 Authentification

- Connexion avec différents rôles (Propriétaire, Réparateur, Magasinier)
- Comptes de test intégrés
- Gestion de session sécurisée

### 👥 Gestion des Clients

- CRUD complet (Créer, Lire, Modifier, Supprimer)
- Recherche en temps réel
- Validation des données (email, téléphone, nom)
- Interface moderne avec indicateurs de chargement

### 📱 Gestion des Appareils

- Suivi des appareils en réparation
- Association client-appareil

### 🔧 Gestion des Réparations

- Création et suivi des réparations
- Assignation des réparateurs
- Gestion des statuts

### ⚙️ Gestion des Pièces

- Inventaire des pièces de rechange
- Suivi des stocks

### 💰 Caisse

- Gestion financière
- Suivi des transactions

### 🏪 Boutiques

- Gestion des points de vente

## Améliorations Réalisées

### 🎨 Interface Utilisateur

- **Thème moderne** : Passage du rouge au bleu professionnel
- **Design responsive** : Interface adaptée à différentes tailles d'écran
- **Composants stylisés** : Boutons, cartes et tableaux modernisés
- **Icônes cohérentes** : Utilisation d'emojis et d'icônes standards

### ✅ Validation et Sécurité

- **Validation avancée** : Vérification des emails, téléphones, mots de passe
- **Messages d'erreur** : Feedback utilisateur clair et précis
- **Indicateurs de chargement** : Interface non-bloquante pendant les opérations

### 🔧 Fonctionnalités Techniques

- **Recherche intégrée** : Recherche en temps réel dans les tableaux
- **CRUD complet** : Toutes les opérations de base de données implémentées
- **Gestion d'erreurs** : Gestion robuste des exceptions
- **Architecture MVC** : Séparation claire des couches

## Technologies Utilisées

- **Java 17** : Langage de programmation
- **Swing** : Interface graphique
- **JPA/Hibernate** : Persistance des données
- **MySQL** : Base de données
- **Lombok** : Réduction du code boilerplate

## Comptes de Test

- **Propriétaire** : proprietaire@test.com / test123
- **Réparateur** : reparateur@test.com / test123
- **Magasinier** : magasinier@test.com / test123

## Installation et Exécution

### Prérequis

- Java 17 ou supérieur
- MySQL Server
- Base de données `GestionClient`

### Lancement

```bash
# Compiler et exécuter
javac -cp "lib/*" src/main/java/presentation/App.java
java -cp "lib/*:." presentation.App
```

### Configuration Base de Données

Modifier `src/main/resources/META-INF/persistence.xml` pour les paramètres de connexion.

## Structure du Projet

```
src/main/java/
├── dao/           # Entités JPA
├── metier/        # Logique métier
├── presentation/  # Interface utilisateur
│   ├── ui/
│   │   ├── components/  # Composants réutilisables
│   │   ├── frames/      # Fenêtres principales
│   │   ├── panels/      # Panneaux de contenu
│   │   └── utils/       # Utilitaires UI
└── exception/     # Gestion d'erreurs
```

## Améliorations Futures

- Migration vers JavaFX pour une meilleure UX
- API REST pour intégration web
- Notifications en temps réel
- Rapports PDF
- Sauvegarde automatique
- Mode hors ligne</content>
  <parameter name="filePath">c:\Users\acer\Documents\GestionRepAppV2\README.md
