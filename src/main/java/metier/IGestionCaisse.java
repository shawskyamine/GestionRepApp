package metier;

import dao.Caisse;
import dao.Reparation;
import dao.Emprunt;
import java.util.List;

public interface IGestionCaisse {
    // CRUD operations
    void add(Caisse caisse);
    void update(Caisse caisse);
    void delete(Caisse caisse);
    Caisse findById(int id);
    List<Caisse> findAll();
    
    // Financial operations
    void crediter(int caisseId, Reparation reparation);
    void debiter(int caisseId, Emprunt emprunt);
    double getSolde(int caisseId);
    double getTotalCredits(int caisseId);
    double getTotalDebits(int caisseId);
    
    // Relationship queries
    List<Reparation> getReparationsByCaisse(int caisseId);
    List<Emprunt> getEmpruntsByCaisse(int caisseId);
}