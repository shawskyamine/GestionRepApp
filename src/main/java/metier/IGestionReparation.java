package metier;

import dao.Reparation;
import java.util.Date;
import java.util.List;
import exception.DatabaseException;
import exception.EntityNotFoundException;

public interface IGestionReparation {
    void add(Reparation reparation) throws DatabaseException;

    void update(Reparation reparation) throws DatabaseException;

    void delete(Reparation reparation) throws DatabaseException;

    Reparation findById(int id) throws DatabaseException, EntityNotFoundException;

    List<Reparation> findAll() throws DatabaseException;

    Reparation findByCodeReparation(String code) throws DatabaseException, EntityNotFoundException;

    List<Reparation> findByStatut(String statut) throws DatabaseException;

    List<Reparation> findByReparateur(int reparateurId) throws DatabaseException;

    List<Reparation> findByDateRange(Date startDate, Date endDate) throws DatabaseException;
}