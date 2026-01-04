package metier;

import java.util.List;
import dao.Utilisateur;

public interface IGestionUtilisateur {
    public Utilisateur create(Utilisateur utilisateur);
    public Utilisateur findById(int id);
    public Utilisateur findByEmail(String email);
    public List<Utilisateur> findAll();
    public Utilisateur findByNomAndPrenom(String nom, String prenom);
    public Utilisateur update(Utilisateur utilisateur);
    public boolean delete(int id);
    public boolean delete(Utilisateur utilisateur);
    public long count();
}