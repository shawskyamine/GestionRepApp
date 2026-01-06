package metier;

import dao.Appareil;
import java.util.List;
import exception.DatabaseException;

public interface IGestionAppareil {
    void add(Appareil appareil) throws DatabaseException;
    void update(Appareil appareil) throws DatabaseException;
    void delete(Appareil appareil) throws DatabaseException;
    Appareil findById(int id) throws DatabaseException;
    List<Appareil> findAll() throws DatabaseException;
    List<Appareil> findByMarque(String marque) throws DatabaseException;
    List<Appareil> findByModele(String modele) throws DatabaseException;
    List<Appareil> findByImei(String imei) throws DatabaseException;
    List<Appareil> findByReparation(int reparationId) throws DatabaseException;
}