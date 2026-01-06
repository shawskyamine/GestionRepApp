package metier;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import dao.Appareil;
import exception.DatabaseException;

public class GestionAppareil implements IGestionAppareil {

    private EntityManager em;

    public GestionAppareil() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("ClientUP");
        em = emf.createEntityManager();
    }

    @Override
    public void add(Appareil appareil) throws DatabaseException {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            em.persist(appareil);
            tr.commit();
        } catch (Exception e) {
            if (tr.isActive()) {
                tr.rollback();
            }
            throw new DatabaseException("Erreur lors de l'ajout de l'appareil: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Appareil appareil) throws DatabaseException {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            em.merge(appareil);
            tr.commit();
        } catch (Exception e) {
            if (tr.isActive()) {
                tr.rollback();
            }
            throw new DatabaseException("Erreur lors de la modification de l'appareil: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(Appareil appareil) throws DatabaseException {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            em.remove(em.contains(appareil) ? appareil : em.merge(appareil));
            tr.commit();
        } catch (Exception e) {
            if (tr.isActive()) {
                tr.rollback();
            }
            throw new DatabaseException("Erreur lors de la suppression de l'appareil: " + e.getMessage(), e);
        }
    }

    @Override
    public Appareil findById(int id) throws DatabaseException {
        try {
            Appareil appareil = em.find(Appareil.class, id);
            if (appareil == null) {
                throw new DatabaseException("Appareil avec ID " + id + " non trouvé");
            }
            return appareil;
        } catch (Exception e) {
            if (e instanceof DatabaseException) {
                throw (DatabaseException) e;
            }
            throw new DatabaseException("Erreur lors de la recherche de l'appareil: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Appareil> findAll() throws DatabaseException {
        try {
            TypedQuery<Appareil> query = em.createQuery("SELECT a FROM Appareil a", Appareil.class);
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Erreur lors du chargement des appareils: " + e.getMessage(), e);
        }
    }

    public List<Appareil> findAllByBoutique(dao.Boutique boutique) throws DatabaseException {
        try {
            TypedQuery<Appareil> query = em.createQuery("SELECT a FROM Appareil a WHERE a.boutique = :boutique",
                    Appareil.class);
            query.setParameter("boutique", boutique);
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Erreur lors du chargement des appareils par boutique: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Appareil> findByMarque(String marque) throws DatabaseException {
        try {
            TypedQuery<Appareil> query = em.createQuery(
                    "SELECT a FROM Appareil a WHERE a.marque = :marque",
                    Appareil.class);
            query.setParameter("marque", marque);
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Erreur lors de la recherche par marque: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Appareil> findByModele(String modele) throws DatabaseException {
        try {
            TypedQuery<Appareil> query = em.createQuery(
                    "SELECT a FROM Appareil a WHERE a.modele = :modele",
                    Appareil.class);
            query.setParameter("modele", modele);
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Erreur lors de la recherche par modèle: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Appareil> findByImei(String imei) throws DatabaseException {
        try {
            TypedQuery<Appareil> query = em.createQuery(
                    "SELECT a FROM Appareil a WHERE a.imei = :imei",
                    Appareil.class);
            query.setParameter("imei", imei);
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Erreur lors de la recherche par IMEI: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Appareil> findByReparation(int reparationId) throws DatabaseException {
        try {
            TypedQuery<Appareil> query = em.createQuery(
                    "SELECT a FROM Appareil a JOIN a.reparation r WHERE r.id = :reparationId",
                    Appareil.class);
            query.setParameter("reparationId", reparationId);
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Erreur lors de la recherche par réparation: " + e.getMessage(), e);
        }
    }

    // Close EntityManager when done
    public void close() {
        if (em != null && em.isOpen()) {
            em.close();
        }
    }
}