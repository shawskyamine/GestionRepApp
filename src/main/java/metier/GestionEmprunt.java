package metier;

import dao.Emprunt;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.List;

public class GestionEmprunt implements IGestionEmprunt {
    
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("ORMDemo");
    
    @Override
    public void add(Emprunt emprunt) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(emprunt);
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
    public void update(Emprunt emprunt) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(emprunt);
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
    public void delete(Emprunt emprunt) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Emprunt e = em.find(Emprunt.class, emprunt.getId());
            if (e != null) {
                em.remove(e);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            ex.printStackTrace();
        } finally {
            em.close();
        }
    }
    
    @Override
    public Emprunt findById(int id) {
        EntityManager em = emf.createEntityManager();
        Emprunt emprunt = null;
        try {
            emprunt = em.find(Emprunt.class, id);
        } finally {
            em.close();
        }
        return emprunt;
    }
    
    @Override
    public List<Emprunt> findAll() {
        EntityManager em = emf.createEntityManager();
        List<Emprunt> emprunts = null;
        try {
            TypedQuery<Emprunt> query = em.createQuery("SELECT e FROM Emprunt e", Emprunt.class);
            emprunts = query.getResultList();
        } finally {
            em.close();
        }
        return emprunts;
    }
    
    @Override
    public List<Emprunt> findByEtat(String etat) {
        EntityManager em = emf.createEntityManager();
        List<Emprunt> emprunts = null;
        try {
            TypedQuery<Emprunt> query = em.createQuery(
                "SELECT e FROM Emprunt e WHERE e.etat = :etat", Emprunt.class);
            query.setParameter("etat", etat);
            emprunts = query.getResultList();
        } finally {
            em.close();
        }
        return emprunts;
    }
    
    @Override
    public List<Emprunt> findByCaisse(int caisseId) {
        EntityManager em = emf.createEntityManager();
        List<Emprunt> emprunts = null;
        try {
            TypedQuery<Emprunt> query = em.createQuery(
                "SELECT e FROM Emprunt e WHERE e.caisse.id = :caisseId", Emprunt.class);
            query.setParameter("caisseId", caisseId);
            emprunts = query.getResultList();
        } finally {
            em.close();
        }
        return emprunts;
    }
    
    @Override
    public List<Emprunt> findByDateRange(Date startDate, Date endDate) {
        EntityManager em = emf.createEntityManager();
        List<Emprunt> emprunts = null;
        try {
            TypedQuery<Emprunt> query = em.createQuery(
                "SELECT e FROM Emprunt e WHERE e.dateEmprunt BETWEEN :start AND :end", Emprunt.class);
            query.setParameter("start", startDate);
            query.setParameter("end", endDate);
            emprunts = query.getResultList();
        } finally {
            em.close();
        }
        return emprunts;
    }
}