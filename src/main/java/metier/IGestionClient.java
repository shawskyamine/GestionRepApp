package metier;

import dao.Client;
import java.util.List;
import exception.DatabaseException;
import exception.EntityNotFoundException;

public interface IGestionClient {
    // CRUD operations
    Client create(Client client) throws DatabaseException;

    Client update(Client client) throws DatabaseException;

    boolean delete(int id) throws DatabaseException;

    boolean delete(Client client) throws DatabaseException;

    Client findById(int id) throws DatabaseException;

    List<Client> findAll() throws DatabaseException;

    // Specific search methods
    Client findByTelephone(String telephone) throws DatabaseException, EntityNotFoundException;

    List<Client> findByNom(String nom) throws DatabaseException;

    List<Client> findByPrenom(String prenom) throws DatabaseException;

    List<Client> findByNomAndPrenom(String nom, String prenom) throws DatabaseException;

    // Count
    long count() throws DatabaseException;
}