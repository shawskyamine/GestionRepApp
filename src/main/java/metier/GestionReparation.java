package metier;

import dao.Reparation;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.List;

public class GestionReparation implements IGestionReparation {
    
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("ORMDemo");
    
    @Override
    public void add(Reparation reparation) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(reparation);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
    
    @Override
    public void update(Reparation reparation) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(reparation);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
    
    @Override
    public void delete(Reparation reparation) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Reparation r = em.find(Reparation.class, reparation.getId());
            if (r != null) {
                em.remove(r);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
    
    @Override
    public Reparation findById(int id) {
        EntityManager em = emf.createEntityManager();
        Reparation reparation = null;
        try {
            reparation = em.find(Reparation.class, id);
        } finally {
            em.close();
        }
        return reparation;
    }
    
    @Override
    public List<Reparation> findAll() {
        EntityManager em = emf.createEntityManager();
        List<Reparation> reparations = null;
        try {
            TypedQuery<Reparation> query = em.createQuery("SELECT r FROM Reparation r", Reparation.class);
            reparations = query.getResultList();
        } finally {
            em.close();
        }
        return reparations;
    }
    
    @Override
    public Reparation findByCodeReparation(int code) {
        EntityManager em = emf.createEntityManager();
        Reparation reparation = null;
        try {
            TypedQuery<Reparation> query = em.createQuery(
                "SELECT r FROM Reparation r WHERE r.codeReparation = :code", Reparation.class);
            query.setParameter("code", code);
            List<Reparation> results = query.getResultList();
            if (!results.isEmpty()) {
                reparation = results.get(0);
            }
        } finally {
            em.close();
        }
        return reparation;
    }
    
    @Override
    public List<Reparation> findByStatut(String statut) {
        EntityManager em = emf.createEntityManager();
        List<Reparation> reparations = null;
        try {
            TypedQuery<Reparation> query = em.createQuery(
                "SELECT r FROM Reparation r WHERE r.statut = :statut", Reparation.class);
            query.setParameter("statut", statut);
            reparations = query.getResultList();
        } finally {
            em.close();
        }
        return reparations;
    }
    
    @Override
    public List<Reparation> findByReparateur(int reparateurId) {
        EntityManager em = emf.createEntityManager();
        List<Reparation> reparations = null;
        try {
            TypedQuery<Reparation> query = em.createQuery(
                "SELECT r FROM Reparation r WHERE r.reparateur.id = :repId", Reparation.class);
            query.setParameter("repId", reparateurId);
            reparations = query.getResultList();
        } finally {
            em.close();
        }
        return reparations;
    }
    
    @Override
    public List<Reparation> findByDateRange(Date startDate, Date endDate) {
        EntityManager em = emf.createEntityManager();
        List<Reparation> reparations = null;
        try {
            TypedQuery<Reparation> query = em.createQuery(
                "SELECT r FROM Reparation r WHERE r.dateCreation BETWEEN :start AND :end", Reparation.class);
            query.setParameter("start", startDate);
            query.setParameter("end", endDate);
            reparations = query.getResultList();
        } finally {
            em.close();
        }
        return reparations;
    }
}