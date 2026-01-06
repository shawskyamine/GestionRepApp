package metier;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.NoResultException;
import dao.Client;
import exception.DatabaseException;
import exception.EntityNotFoundException;

public class GestionClient implements IGestionClient {

    private EntityManager em;

    public GestionClient() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("ClientUP");
        em = emf.createEntityManager();
    }

    @Override
    public Client create(Client client) throws DatabaseException {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            em.persist(client);
            tr.commit();
            return client;
        } catch (Exception e) {
            if (tr.isActive()) {
                tr.rollback();
            }
            throw new DatabaseException("Erreur lors de la création du client: " + e.getMessage(), e);
        }
    }

    @Override
    public Client update(Client client) throws DatabaseException {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            Client updated = em.merge(client);
            tr.commit();
            return updated;
        } catch (Exception e) {
            if (tr.isActive()) {
                tr.rollback();
            }
            throw new DatabaseException("Erreur lors de la modification du client: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(int id) throws DatabaseException {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            Client client = em.find(Client.class, id);
            if (client != null) {
                em.remove(client);
                tr.commit();
                return true;
            }
            tr.rollback();
            return false;
        } catch (Exception e) {
            if (tr.isActive()) {
                tr.rollback();
            }
            throw new DatabaseException("Erreur lors de la suppression du client: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(Client client) throws DatabaseException {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            em.remove(em.contains(client) ? client : em.merge(client));
            tr.commit();
            return true;
        } catch (Exception e) {
            if (tr.isActive()) {
                tr.rollback();
            }
            throw new DatabaseException("Erreur lors de la suppression du client: " + e.getMessage(), e);
        }
    }

    @Override
    public Client findById(int id) throws DatabaseException {
        try {
            Client client = em.find(Client.class, id);
            if (client == null) {
                throw new DatabaseException("Client avec ID " + id + " non trouvé");
            }
            return client;
        } catch (Exception e) {
            if (e instanceof DatabaseException) {
                throw (DatabaseException) e;
            }
            throw new DatabaseException("Erreur lors de la recherche du client: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Client> findAll() throws DatabaseException {
        try {
            TypedQuery<Client> query = em.createQuery("SELECT c FROM Client c", Client.class);
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Erreur lors du chargement des clients: " + e.getMessage(), e);
        }
    }

    public List<Client> findAllByBoutique(dao.Boutique boutique) throws DatabaseException {
        try {
            TypedQuery<Client> query = em.createQuery("SELECT c FROM Client c WHERE c.boutique = :boutique",
                    Client.class);
            query.setParameter("boutique", boutique);
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Erreur lors du chargement des clients par boutique: " + e.getMessage(), e);
        }
    }

    @Override
    public Client findByTelephone(String telephone) throws DatabaseException, EntityNotFoundException {
        try {
            TypedQuery<Client> query = em.createQuery(
                    "SELECT c FROM Client c WHERE c.telephone = :telephone",
                    Client.class);
            query.setParameter("telephone", telephone);
            List<Client> results = query.getResultList();
            if (results.isEmpty()) {
                throw new EntityNotFoundException("Client avec téléphone " + telephone + " non trouvé");
            }
            return results.get(0);
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("Erreur lors de la recherche par téléphone: " + e.getMessage(), e);
        }
    }

    public List<Client> search(String keyword) throws DatabaseException {
        try {
            TypedQuery<Client> query = em.createQuery(
                    "SELECT c FROM Client c WHERE " +
                            "c.nom LIKE :keyword OR c.prenom LIKE :keyword OR c.telephone LIKE :keyword",
                    Client.class);
            query.setParameter("keyword", "%" + keyword + "%");
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Erreur lors de la recherche de clients: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Client> findByNom(String nom) throws DatabaseException {
        try {
            TypedQuery<Client> query = em.createQuery(
                    "SELECT c FROM Client c WHERE c.nom = :nom",
                    Client.class);
            query.setParameter("nom", nom);
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Erreur lors de la recherche par nom: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Client> findByPrenom(String prenom) throws DatabaseException {
        try {
            TypedQuery<Client> query = em.createQuery(
                    "SELECT c FROM Client c WHERE c.prenom = :prenom",
                    Client.class);
            query.setParameter("prenom", prenom);
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Erreur lors de la recherche par prénom: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Client> findByNomAndPrenom(String nom, String prenom) throws DatabaseException {
        try {
            TypedQuery<Client> query = em.createQuery(
                    "SELECT c FROM Client c WHERE c.nom = :nom AND c.prenom = :prenom",
                    Client.class);
            query.setParameter("nom", nom);
            query.setParameter("prenom", prenom);
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Erreur lors de la recherche par nom et prénom: " + e.getMessage(), e);
        }
    }

    @Override
    public long count() throws DatabaseException {
        try {
            TypedQuery<Long> query = em.createQuery("SELECT COUNT(c) FROM Client c", Long.class);
            return query.getSingleResult();
        } catch (Exception e) {
            throw new DatabaseException("Erreur lors du comptage des clients: " + e.getMessage(), e);
        }
    }

    // Close EntityManager when done
    public void close() {
        if (em != null && em.isOpen()) {
            em.close();
        }
    }
}