package metier;

import dao.Client;
import java.util.List;

public interface IGestionClient {
    // CRUD operations
    Client create(Client client);
    Client update(Client client);
    boolean delete(int id);
    boolean delete(Client client);
    Client findById(int id);
    List<Client> findAll();
    
    // Specific search methods
    Client findByTelephone(String telephone);
    List<Client> findByNom(String nom);
    List<Client> findByPrenom(String prenom);
    List<Client> findByNomAndPrenom(String nom, String prenom);
    
    // Count
    long count();
}