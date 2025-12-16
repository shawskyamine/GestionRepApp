package metier;

import dao.Appareil;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.util.List;

public class GestionAppareil implements IGestionAppareil {
    
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("ORMDemo");
    
    @Override
    public void add(Appareil appareil) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(appareil);
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
    public void update(Appareil appareil) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(appareil);
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
    public void delete(Appareil appareil) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Appareil app = em.find(Appareil.class, appareil.getId());
            if (app != null) {
                em.remove(app);
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
    public Appareil findById(int id) {
        EntityManager em = emf.createEntityManager();
        Appareil appareil = null;
        try {
            appareil = em.find(Appareil.class, id);
        } finally {
            em.close();
        }
        return appareil;
    }
    
    @Override
    public List<Appareil> findAll() {
        EntityManager em = emf.createEntityManager();
        List<Appareil> appareils = null;
        try {
            TypedQuery<Appareil> query = em.createQuery("SELECT a FROM Appareil a", Appareil.class);
            appareils = query.getResultList();
        } finally {
            em.close();
        }
        return appareils;
    }
    
    @Override
    public List<Appareil> findByMarque(String marque) {
        EntityManager em = emf.createEntityManager();
        List<Appareil> appareils = null;
        try {
            TypedQuery<Appareil> query = em.createQuery(
                "SELECT a FROM Appareil a WHERE a.marque = :marque", Appareil.class);
            query.setParameter("marque", marque);
            appareils = query.getResultList();
        } finally {
            em.close();
        }
        return appareils;
    }
    
    @Override
    public List<Appareil> findByModele(String modele) {
        EntityManager em = emf.createEntityManager();
        List<Appareil> appareils = null;
        try {
            TypedQuery<Appareil> query = em.createQuery(
                "SELECT a FROM Appareil a WHERE a.modele = :modele", Appareil.class);
            query.setParameter("modele", modele);
            appareils = query.getResultList();
        } finally {
            em.close();
        }
        return appareils;
    }
    
    @Override
    public List<Appareil> findByImei(String imei) {
        EntityManager em = emf.createEntityManager();
        List<Appareil> appareils = null;
        try {
            TypedQuery<Appareil> query = em.createQuery(
                "SELECT a FROM Appareil a WHERE a.imei = :imei", Appareil.class);
            query.setParameter("imei", imei);
            appareils = query.getResultList();
        } finally {
            em.close();
        }
        return appareils;
    }
    
    @Override
    public List<Appareil> findByReparation(int reparationId) {
        EntityManager em = emf.createEntityManager();
        List<Appareil> appareils = null;
        try {
            TypedQuery<Appareil> query = em.createQuery(
                "SELECT a FROM Appareil a WHERE a.reparation.id = :repId", Appareil.class);
            query.setParameter("repId", reparationId);
            appareils = query.getResultList();
        } finally {
            em.close();
        }
        return appareils;
    }
}