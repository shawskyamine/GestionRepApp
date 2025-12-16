package metier;

import dao.Reparateur;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.util.List;

public class GestionReparateur implements IGestionReparateur {
    
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("ORMDemo");
    
    @Override
    public void add(Reparateur reparateur) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(reparateur);
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
    public void update(Reparateur reparateur) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(reparateur);
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
    public void delete(Reparateur reparateur) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Reparateur r = em.find(Reparateur.class, reparateur.getId());
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
    public Reparateur findById(int id) {
        EntityManager em = emf.createEntityManager();
        Reparateur reparateur = null;
        try {
            reparateur = em.find(Reparateur.class, id);
        } finally {
            em.close();
        }
        return reparateur;
    }
    
    @Override
    public List<Reparateur> findAll() {
        EntityManager em = emf.createEntityManager();
        List<Reparateur> reparateurs = null;
        try {
            TypedQuery<Reparateur> query = em.createQuery("SELECT r FROM Reparateur r", Reparateur.class);
            reparateurs = query.getResultList();
        } finally {
            em.close();
        }
        return reparateurs;
    }
    
    @Override
    public Reparateur findByIdentifiant(String identifiant) {
        EntityManager em = emf.createEntityManager();
        Reparateur reparateur = null;
        try {
            TypedQuery<Reparateur> query = em.createQuery(
                "SELECT r FROM Reparateur r WHERE r.identifiant = :id", Reparateur.class);
            query.setParameter("id", identifiant);
            List<Reparateur> results = query.getResultList();
            if (!results.isEmpty()) {
                reparateur = results.get(0);
            }
        } finally {
            em.close();
        }
        return reparateur;
    }
    
   
}