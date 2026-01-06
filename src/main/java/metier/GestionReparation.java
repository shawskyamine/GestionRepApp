package metier;

import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import dao.Reparation;
import dao.Reparateur;
import dao.Appareil;
import dao.Caisse;
import exception.DatabaseException;
import exception.EntityNotFoundException;

public class GestionReparation implements IGestionReparation {

    private EntityManager em;

    public GestionReparation() {
        System.out.println("GestionReparation constructor called");
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("ClientUP");
            em = emf.createEntityManager();
            System.out.println("EntityManager created successfully: " + (em != null));
        } catch (Exception e) {
            System.out.println("Error creating EntityManager: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void add(Reparation reparation) throws DatabaseException {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            em.persist(reparation);
            tr.commit();
        } catch (Exception e) {
            tr.rollback();
            throw new DatabaseException("Failed to add reparation", e);
        }
    }

    @Override
    public void update(Reparation reparation) throws DatabaseException {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            em.merge(reparation);
            tr.commit();
        } catch (Exception e) {
            tr.rollback();
            throw new DatabaseException("Failed to update reparation", e);
        }
    }

    @Override
    public void delete(Reparation reparation) throws DatabaseException {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            em.remove(em.merge(reparation));
            tr.commit();
        } catch (Exception e) {
            tr.rollback();
            throw new DatabaseException("Failed to delete reparation", e);
        }
    }

    @Override
    public Reparation findById(int id) throws DatabaseException, EntityNotFoundException {
        try {
            Reparation reparation = em.find(Reparation.class, id);
            if (reparation == null) {
                throw new EntityNotFoundException("Reparation not found with id: " + id);
            }
            return reparation;
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("Failed to find reparation by id", e);
        }
    }

    @Override
    public List<Reparation> findAll() throws DatabaseException {
        try {
            TypedQuery<Reparation> query = em.createQuery("SELECT r FROM Reparation r", Reparation.class);
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Failed to retrieve all reparations", e);
        }
    }

    public List<Reparation> findAllByBoutique(dao.Boutique boutique) throws DatabaseException {
        try {
            TypedQuery<Reparation> query = em.createQuery("SELECT r FROM Reparation r WHERE r.boutique = :boutique",
                    Reparation.class);
            query.setParameter("boutique", boutique);
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Failed to retrieve reparations by boutique", e);
        }
    }

    @Override
    public Reparation findByCodeReparation(String code) throws DatabaseException, EntityNotFoundException {
        System.out.println("findByCodeReparation called with code: '" + code + "'");
        try {
            TypedQuery<Reparation> query = em.createQuery(
                    "SELECT r FROM Reparation r WHERE r.codeReparation = :code",
                    Reparation.class);
            query.setParameter("code", code);
            List<Reparation> results = query.getResultList();
            System.out.println("Query executed, results size: " + results.size());
            if (results.isEmpty()) {
                throw new EntityNotFoundException("Reparation not found with code: " + code);
            }
            return results.get(0);
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            System.out.println("Exception in findByCodeReparation: " + e.getMessage());
            throw new DatabaseException("Failed to find reparation by code", e);
        }
    }

    @Override
    public List<Reparation> findByStatut(String statut) throws DatabaseException {
        try {
            TypedQuery<Reparation> query = em.createQuery(
                    "SELECT r FROM Reparation r WHERE r.statut = :statut",
                    Reparation.class);
            query.setParameter("statut", statut);
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Failed to find reparations by statut", e);
        }
    }

    @Override
    public List<Reparation> findByReparateur(int reparateurId) throws DatabaseException {
        try {
            TypedQuery<Reparation> query = em.createQuery(
                    "SELECT r FROM Reparation r WHERE r.reparateur.id = :reparateurId",
                    Reparation.class);
            query.setParameter("reparateurId", reparateurId);
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Failed to find reparations by reparateur", e);
        }
    }

    @Override
    public List<Reparation> findByDateRange(Date startDate, Date endDate) throws DatabaseException {
        try {
            TypedQuery<Reparation> query = em.createQuery(
                    "SELECT r FROM Reparation r WHERE r.dateDeCreation BETWEEN :startDate AND :endDate",
                    Reparation.class);
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Failed to find reparations by date range", e);
        }
    }

    // Additional useful methods

    public List<Appareil> findAppareilsByReparation(int reparationId) {
        try {
            Reparation reparation = em.find(Reparation.class, reparationId);
            if (reparation != null) {
                reparation.getAppareils().size();
                return reparation.getAppareils();
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Reparation> findByCaisse(int caisseId) {
        try {
            TypedQuery<Reparation> query = em.createQuery(
                    "SELECT r FROM Reparation r WHERE r.caisse.id = :caisseId",
                    Reparation.class);
            query.setParameter("caisseId", caisseId);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Reparation> findByCauseDeReparation(String cause) {
        try {
            TypedQuery<Reparation> query = em.createQuery(
                    "SELECT r FROM Reparation r WHERE r.causeDeReparation = :cause",
                    Reparation.class);
            query.setParameter("cause", cause);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public long countReparationsByStatut(String statut) {
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(r) FROM Reparation r WHERE r.statut = :statut",
                    Long.class);
            query.setParameter("statut", statut);
            return query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void addAppareilToReparation(int reparationId, int appareilId) {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();

            Reparation reparation = em.find(Reparation.class, reparationId);
            Appareil appareil = em.find(Appareil.class, appareilId);

            if (reparation != null && appareil != null) {
                if (!reparation.getAppareils().contains(appareil)) {
                    reparation.getAppareils().add(appareil);
                    em.merge(reparation);
                }
            }

            tr.commit();
        } catch (Exception e) {
            tr.rollback();
            e.printStackTrace();
        }
    }

    public void removeAppareilFromReparation(int reparationId, int appareilId) {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();

            Reparation reparation = em.find(Reparation.class, reparationId);
            Appareil appareil = em.find(Appareil.class, appareilId);

            if (reparation != null && appareil != null) {
                reparation.getAppareils().remove(appareil);
                em.merge(reparation);
            }

            tr.commit();
        } catch (Exception e) {
            tr.rollback();
            e.printStackTrace();
        }
    }

    public List<Reparation> findReparationsWithoutCaisse() {
        try {
            TypedQuery<Reparation> query = em.createQuery(
                    "SELECT r FROM Reparation r WHERE r.caisse IS NULL",
                    Reparation.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}