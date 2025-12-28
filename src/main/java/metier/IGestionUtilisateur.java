package metier;

import java.util.List;
import dao.Utilisateur;

public interface IGestionUtilisateur {
    // Create
    Utilisateur create(Utilisateur utilisateur);
    
    // Read
    Utilisateur findById(int id);
    List<Utilisateur> findAll();
    Utilisateur findByNomAndPrenom(String nom, String prenom);
    
    // Update
    Utilisateur update(Utilisateur utilisateur);
    
    // Delete
    boolean delete(int id);
    boolean delete(Utilisateur utilisateur);
    
    // Count
    long count();
}