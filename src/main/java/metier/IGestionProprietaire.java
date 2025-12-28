package metier;

import dao.Proprietaire;
import dao.Boutique;
import java.util.List;

public interface IGestionProprietaire {
    void add(Proprietaire proprietaire);
    void update(Proprietaire proprietaire);
    void delete(Proprietaire proprietaire);
    Proprietaire findById(int id);
    List<Proprietaire> findAll();
    List<Proprietaire> findByEmail(String email);
    Proprietaire findByEmailAndPassword(String email, String motDePasse);
}