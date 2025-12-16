package metier;

import dao.Emprunt;
import java.util.Date;
import java.util.List;

public interface IGestionEmprunt {
    void add(Emprunt emprunt);
    void update(Emprunt emprunt);
    void delete(Emprunt emprunt);
    Emprunt findById(int id);
    List<Emprunt> findAll();
    List<Emprunt> findByEtat(String etat);
    List<Emprunt> findByCaisse(int caisseId);
    List<Emprunt> findByDateRange(Date startDate, Date endDate);
}