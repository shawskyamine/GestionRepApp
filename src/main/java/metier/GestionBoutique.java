package metier;

import dao.Boutique;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.util.List;

public class GestionBoutique implements IGestionBoutique {
    
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("ORMDemo");
    
    @Override
    public void add(Boutique boutique) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(boutique);
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
    public void update(Boutique boutique) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(boutique);
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
    public void delete(Boutique boutique) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Boutique b = em.find(Boutique.class, boutique.getId());
            if (b != null) {
                em.remove(b);
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
    public Boutique findById(int id) {
        EntityManager em = emf.createEntityManager();
        Boutique boutique = null;
        try {
            boutique = em.find(Boutique.class, id);
        } finally {
            em.close();
        }
        return boutique;
    }
    
    @Override
    public List<Boutique> findAll() {
        EntityManager em = emf.createEntityManager();
        List<Boutique> boutiques = null;
        try {
            TypedQuery<Boutique> query = em.createQuery("SELECT b FROM Boutique b", Boutique.class);
            boutiques = query.getResultList();
        } finally {
            em.close();
        }
        return boutiques;
    }
    
    @Override
    public List<Boutique> findByNom(String nom) {
        EntityManager em = emf.createEntityManager();
        List<Boutique> boutiques = null;
        try {
            TypedQuery<Boutique> query = em.createQuery(
                "SELECT b FROM Boutique b WHERE b.nomBoutique = :nom", Boutique.class);
            query.setParameter("nom", nom);
            boutiques = query.getResultList();
        } finally {
            em.close();
        }
        return boutiques;
    }
    
    @Override
    public List<Boutique> findByProprietaire(int proprietaireId) {
        EntityManager em = emf.createEntityManager();
        List<Boutique> boutiques = null;
        try {
            TypedQuery<Boutique> query = em.createQuery(
                "SELECT b FROM Boutique b WHERE b.proprietaire.id = :propId", Boutique.class);
            query.setParameter("propId", proprietaireId);
            boutiques = query.getResultList();
        } finally {
            em.close();
        }
        return boutiques;
    }
}