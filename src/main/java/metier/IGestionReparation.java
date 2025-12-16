package metier;

import dao.Reparation;
import java.util.Date;
import java.util.List;

public interface IGestionReparation {
    void add(Reparation reparation);
    void update(Reparation reparation);
    void delete(Reparation reparation);
    Reparation findById(int id);
    List<Reparation> findAll();
    Reparation findByCodeReparation(int code);
    List<Reparation> findByStatut(String statut);
    List<Reparation> findByReparateur(int reparateurId);
    List<Reparation> findByDateRange(Date startDate, Date endDate);
}