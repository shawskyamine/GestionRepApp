package metier;

import dao.Boutique;
import java.util.List;

public interface IGestionBoutique {
    void add(Boutique boutique);
    void update(Boutique boutique);
    void delete(Boutique boutique);
    Boutique findById(int id);
    List<Boutique> findAll();
    List<Boutique> findByNom(String nom);
    List<Boutique> findByProprietaire(int proprietaireId);
}