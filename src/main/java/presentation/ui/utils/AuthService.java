package presentation.ui.utils;

import dao.Utilisateur;
import dao.Reparateur;
import metier.GestionUtilisateur;
import metier.GestionReparateur;

public class AuthService {
    private static Utilisateur currentUser;

    public static boolean login(String email, String password) {
        System.out.println("AuthService.login called with email: '" + email + "'");
        GestionUtilisateur gestionUtilisateur = new GestionUtilisateur();
        Utilisateur user = gestionUtilisateur.findByEmail(email);

        // If not found in Utilisateur, check Reparateur
        if (user == null) {
            System.out.println("User not found in Utilisateur, checking Reparateur...");
            GestionReparateur gestionReparateur = new GestionReparateur();
            Reparateur reparateur = gestionReparateur.findByEmail(email);
            if (reparateur != null) {
                System.out.println("Found Reparateur: " + reparateur.getEmail() + ", Role: " + reparateur.getRole());
                user = reparateur;
            }
        } else {
            System.out.println("Found Utilisateur: " + user.getEmail() + ", Role: " + user.getRole());
        }

        System.out.println("User found: " + (user != null ? "YES - " + user.getEmail() + ", Role: " + user.getRole() : "NO"));

        if (user != null && user.getPassword().equals(password)) { // In a real app, use hashed passwords
            System.out.println("Password matches, setting session. Final role: " + user.getRole());
            setSession(user);
            return true;
        }
        System.out.println("Password doesn't match or user is null");
        return false;
    }

    private static void setSession(Utilisateur user) {
        currentUser = user;
    }

    public static void logout() {
        currentUser = null;
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
}