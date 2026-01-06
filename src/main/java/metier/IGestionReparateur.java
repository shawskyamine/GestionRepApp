package metier;

import dao.Reparateur;
import java.util.List;
import exception.DatabaseException;
import exception.EntityNotFoundException;

public interface IGestionReparateur {
    void add(Reparateur reparateur) throws DatabaseException;

    void update(Reparateur reparateur) throws DatabaseException;

    void delete(Reparateur reparateur) throws DatabaseException;

    Reparateur findById(int id) throws DatabaseException, EntityNotFoundException;

    List<Reparateur> findAll() throws DatabaseException;

    Reparateur findByEmail(String email) throws DatabaseException, EntityNotFoundException;
}