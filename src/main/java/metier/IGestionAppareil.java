package metier;

import dao.Appareil;
import java.util.List;

public interface IGestionAppareil {
    void add(Appareil appareil);
    void update(Appareil appareil);
    void delete(Appareil appareil);
    Appareil findById(int id);
    List<Appareil> findAll();
    List<Appareil> findByMarque(String marque);
    List<Appareil> findByModele(String modele);
    List<Appareil> findByImei(String imei);
    List<Appareil> findByReparation(int reparationId);
}