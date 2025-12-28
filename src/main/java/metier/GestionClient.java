package metier;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.NoResultException;
import dao.Client;

public class GestionClient implements IGestionClient {

    private EntityManager em;
    
    public GestionClient() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("ClientUP");
        em = emf.createEntityManager();
    }

    @Override
    public Client create(Client client) {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            em.persist(client);
            tr.commit();
            return client;
        } catch (Exception e) {
            tr.rollback();
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Client update(Client client) {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            Client updated = em.merge(client);
            tr.commit();
            return updated;
        } catch (Exception e) {
            tr.rollback();
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean delete(int id) {
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
            tr.rollback();
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(Client client) {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            em.remove(em.contains(client) ? client : em.merge(client));
            tr.commit();
            return true;
        } catch (Exception e) {
            tr.rollback();
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Client findById(int id) {
        try {
            return em.find(Client.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Client> findAll() {
        try {
            TypedQuery<Client> query = em.createQuery("SELECT c FROM Client c", Client.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Client findByTelephone(String telephone) {
        try {
            TypedQuery<Client> query = em.createQuery(
                "SELECT c FROM Client c WHERE c.telephone = :telephone", 
                Client.class
            );
            query.setParameter("telephone", telephone);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Client> findByNom(String nom) {
        try {
            TypedQuery<Client> query = em.createQuery(
                "SELECT c FROM Client c WHERE c.nom = :nom", 
                Client.class
            );
            query.setParameter("nom", nom);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Client> findByPrenom(String prenom) {
        try {
            TypedQuery<Client> query = em.createQuery(
                "SELECT c FROM Client c WHERE c.prenom = :prenom", 
                Client.class
            );
            query.setParameter("prenom", prenom);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Client> findByNomAndPrenom(String nom, String prenom) {
        try {
            TypedQuery<Client> query = em.createQuery(
                "SELECT c FROM Client c WHERE c.nom = :nom AND c.prenom = :prenom", 
                Client.class
            );
            query.setParameter("nom", nom);
            query.setParameter("prenom", prenom);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public long count() {
        try {
            TypedQuery<Long> query = em.createQuery("SELECT COUNT(c) FROM Client c", Long.class);
            return query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}