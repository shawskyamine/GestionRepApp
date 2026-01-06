package presentation.ui.utils;

import dao.Utilisateur;
import dao.Reparateur;
import dao.Boutique;
import dao.Proprietaire;
import metier.GestionUtilisateur;
import metier.GestionReparateur;
import metier.GestionProprietaire;
import exception.AuthenticationException;
import exception.DatabaseException;
import exception.EntityNotFoundException;

import java.util.List;

public class AuthService {
    private static Utilisateur currentUser;
    private static Boutique selectedBoutique;

    public static void login(String email, String password) throws AuthenticationException {
        System.out.println("AuthService.login called with email: '" + email + "'");
        GestionUtilisateur gestionUtilisateur = new GestionUtilisateur();
        Utilisateur user = gestionUtilisateur.findByEmail(email);

        // If not found in Utilisateur, check Reparateur
        if (user == null) {
            System.out.println("User not found in Utilisateur, checking Reparateur...");
            
            try {
                GestionReparateur gestionReparateur = new GestionReparateur();
                Reparateur reparateur = gestionReparateur.findByEmail(email);
                if (reparateur != null) {
                    System.out.println("Found Reparateur: " + reparateur.getEmail() + ", Role: " + reparateur.getRole());
                    user = reparateur;
                }
            } catch (DatabaseException | EntityNotFoundException e) {
                System.out.println("Reparateur not found: " + e.getMessage());
            }
            
            // If still not found, check Proprietaire
            if (user == null) {
                System.out.println("Checking Proprietaire...");
                try {
                    GestionProprietaire gestionProprietaire = new GestionProprietaire();
                    List<Proprietaire> proprietaires = gestionProprietaire.findByEmail(email);
                    if (!proprietaires.isEmpty()) {
                        Proprietaire proprietaire = proprietaires.get(0);
                        System.out.println("Found Proprietaire: " + proprietaire.getEmail() + ", Role: " + proprietaire.getRole());
                        user = proprietaire;
                    }
                } catch (Exception e) {
                    System.out.println("Error checking Proprietaire: " + e.getMessage());
                }
            }
        } else {
            System.out.println("Found Utilisateur: " + user.getEmail() + ", Role: " + user.getRole());
        }

        System.out.println("User found: " + (user != null ? "YES - " + user.getEmail() + ", Role: " + user.getRole() : "NO"));

        if (user == null) {
            throw new AuthenticationException("Utilisateur non trouvé avec cet email");
        }

        if (!user.getPassword().equals(password)) {
            throw new AuthenticationException("Mot de passe incorrect");
        }

        System.out.println("Password matches, setting session. Final role: " + user.getRole());
        setSession(user);
    }

    private static void setSession(Utilisateur user) {
        currentUser = user;
    }

    public static void logout() {
        currentUser = null;
        selectedBoutique = null;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static Utilisateur getCurrentUser() {
        return currentUser;
    }

    public static String getUserRole() {
        return (currentUser != null) ? currentUser.getRole() : null;
    }

    public static int getUserId() {
        return (currentUser != null) ? currentUser.getId() : 0;
    }

    public static String getUserName() {
        return (currentUser != null) ? currentUser.getNom() : null;
    }

    public static boolean isProprietaire() {
        String role = getUserRole();
        return "PROPRIETAIRE".equalsIgnoreCase(role) || "ADMIN".equalsIgnoreCase(role);
    }

    public static boolean isReparateur() {
        String role = getUserRole();
        return "REPARATEUR".equalsIgnoreCase(role);
    }

    public static boolean isMagasinier() {
        String role = getUserRole();
        return "MAGASINIER".equalsIgnoreCase(role);
    }

    public static void setSelectedBoutique(Boutique boutique) {
        selectedBoutique = boutique;
    }

    public static Boutique getSelectedBoutique() {
        return selectedBoutique;
    }

    public static String getSelectedBoutiqueName() {
        return (selectedBoutique != null) ? selectedBoutique.getNomboutique() : null;
    }
}