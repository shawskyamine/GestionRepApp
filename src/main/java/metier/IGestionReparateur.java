package metier;

import dao.Reparateur;
import java.util.List;

public interface IGestionReparateur {
    void add(Reparateur reparateur);
    void update(Reparateur reparateur);
    void delete(Reparateur reparateur);
    Reparateur findById(int id);
    List<Reparateur> findAll();
    Reparateur findByIdentifiant(String identifiant);
}