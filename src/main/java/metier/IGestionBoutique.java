package metier;

import dao.Boutique;
import java.util.List;
import exception.DatabaseException;

public interface IGestionBoutique {
    void add(Boutique boutique) throws DatabaseException;

    void update(Boutique boutique) throws DatabaseException;

    void delete(Boutique boutique) throws DatabaseException;

    Boutique findById(int id) throws DatabaseException;

    List<Boutique> findAll() throws DatabaseException;

    List<Boutique> findByNom(String nom) throws DatabaseException;

    List<Boutique> findByProprietaire(int proprietaireId) throws DatabaseException;
}