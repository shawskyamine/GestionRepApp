package metier;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import dao.Boutique;
import exception.DatabaseException;

public class GestionBoutique implements IGestionBoutique {

    private EntityManager em;

    public GestionBoutique() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("ClientUP");
        em = emf.createEntityManager();
    }

    @Override
    public void add(Boutique boutique) throws DatabaseException {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            em.persist(boutique);
            tr.commit();
        } catch (Exception e) {
            tr.rollback();
            throw new DatabaseException("Failed to add boutique", e);
        }
    }

    @Override
    public void update(Boutique boutique) throws DatabaseException {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            em.merge(boutique);
            tr.commit();
        } catch (Exception e) {
            tr.rollback();
            throw new DatabaseException("Failed to update boutique", e);
        }
    }

    @Override
    public void delete(Boutique boutique) throws DatabaseException {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            em.remove(em.contains(boutique) ? boutique : em.merge(boutique));
            tr.commit();
        } catch (Exception e) {
            tr.rollback();
            throw new DatabaseException("Failed to delete boutique", e);
        }
    }

    @Override
    public Boutique findById(int id) throws DatabaseException {
        try {
            return em.find(Boutique.class, id);
        } catch (Exception e) {
            throw new DatabaseException("Failed to find boutique by id", e);
        }
    }

    @Override
    public List<Boutique> findAll() throws DatabaseException {
        try {
            TypedQuery<Boutique> query = em.createQuery("SELECT b FROM Boutique b", Boutique.class);
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Failed to find all boutiques", e);
        }
    }

    @Override
    public List<Boutique> findByNom(String nom) throws DatabaseException {
        try {
            TypedQuery<Boutique> query = em.createQuery(
                    "SELECT b FROM Boutique b WHERE b.nomboutique = :nom",
                    Boutique.class);
            query.setParameter("nom", nom);
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Failed to find boutiques by nom", e);
        }
    }

    @Override
    public List<Boutique> findByProprietaire(int proprietaireId) throws DatabaseException {
        try {
            TypedQuery<Boutique> query = em.createQuery(
                    "SELECT b FROM Boutique b WHERE b.proprietaire.id = :proprietaireId",
                    Boutique.class);
            query.setParameter("proprietaireId", proprietaireId);
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Failed to find boutiques by proprietaire", e);
        }
    }
}