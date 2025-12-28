package metier;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import dao.Appareil;

public class GestionAppareil implements IGestionAppareil {

    private EntityManager em;
    
    public GestionAppareil() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("ClientUP");
        em = emf.createEntityManager();
    }

    @Override
    public void add(Appareil appareil) {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            em.persist(appareil);
            tr.commit();
        } catch (Exception e) {
            tr.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public void update(Appareil appareil) {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            em.merge(appareil);
            tr.commit();
        } catch (Exception e) {
            tr.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Appareil appareil) {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            em.remove(em.contains(appareil) ? appareil : em.merge(appareil));
            tr.commit();
        } catch (Exception e) {
            tr.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public Appareil findById(int id) {
        try {
            return em.find(Appareil.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Appareil> findAll() {
        try {
            TypedQuery<Appareil> query = em.createQuery("SELECT a FROM Appareil a", Appareil.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Appareil> findByMarque(String marque) {
        try {
            TypedQuery<Appareil> query = em.createQuery(
                "SELECT a FROM Appareil a WHERE a.marque = :marque", 
                Appareil.class
            );
            query.setParameter("marque", marque);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Appareil> findByModele(String modele) {
        try {
            TypedQuery<Appareil> query = em.createQuery(
                "SELECT a FROM Appareil a WHERE a.modele = :modele", 
                Appareil.class
            );
            query.setParameter("modele", modele);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Appareil> findByImei(String imei) {
        try {
            TypedQuery<Appareil> query = em.createQuery(
                "SELECT a FROM Appareil a WHERE a.imei = :imei", 
                Appareil.class
            );
            query.setParameter("imei", imei);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Appareil> findByReparation(int reparationId) {
        try {
            TypedQuery<Appareil> query = em.createQuery(
                "SELECT a FROM Appareil a JOIN a.reparation r WHERE r.id = :reparationId", 
                Appareil.class
            );
            query.setParameter("reparationId", reparationId);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}