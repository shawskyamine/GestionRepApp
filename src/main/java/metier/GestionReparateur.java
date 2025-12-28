package metier;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import dao.Reparateur;
import dao.Reparation;

public class GestionReparateur implements IGestionReparateur {

    private EntityManager em;
    
    public GestionReparateur() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("ClientUP");
        em = emf.createEntityManager();
    }

    @Override
    public void add(Reparateur reparateur) {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            em.persist(reparateur);
            tr.commit();
        } catch (Exception e) {
            tr.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public void update(Reparateur reparateur) {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            em.merge(reparateur);
            tr.commit();
        } catch (Exception e) {
            tr.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Reparateur reparateur) {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            em.remove(em.contains(reparateur) ? reparateur : em.merge(reparateur));
            tr.commit();
        } catch (Exception e) {
            tr.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public Reparateur findById(int id) {
        try {
            return em.find(Reparateur.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Reparateur> findAll() {
        try {
            TypedQuery<Reparateur> query = em.createQuery("SELECT r FROM Reparateur r", Reparateur.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Reparateur findByIdentifiant(String identifiant) {
        try {
            TypedQuery<Reparateur> query = em.createQuery(
                "SELECT r FROM Reparateur r WHERE r.identifiant = :id", 
                Reparateur.class
            );
            query.setParameter("id", identifiant);
            List<Reparateur> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Additional useful methods
    
    public Reparateur findByEmail(String email) {
        try {
            TypedQuery<Reparateur> query = em.createQuery(
                "SELECT r FROM Reparateur r WHERE r.email = :email", 
                Reparateur.class
            );
            query.setParameter("email", email);
            List<Reparateur> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public Reparateur findByEmailAndPassword(String email, String motDePasse) {
        try {
            TypedQuery<Reparateur> query = em.createQuery(
                "SELECT r FROM Reparateur r WHERE r.email = :email AND r.motDePasse = :motDePasse", 
                Reparateur.class
            );
            query.setParameter("email", email);
            query.setParameter("motDePasse", motDePasse);
            List<Reparateur> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public List<Reparation> findReparationsByReparateur(int reparateurId) {
        try {
            Reparateur reparateur = em.find(Reparateur.class, reparateurId);
            if (reparateur != null) {
                reparateur.getReparations().size();
                return reparateur.getReparations();
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public long countReparationsByReparateur(int reparateurId) {
        try {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(r) FROM Reparation r WHERE r.reparateur.id = :reparateurId", 
                Long.class
            );
            query.setParameter("reparateurId", reparateurId);
            return query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    public List<Reparateur> findByPourcentage(int pourcentage) {
        try {
            TypedQuery<Reparateur> query = em.createQuery(
                "SELECT r FROM Reparateur r WHERE r.pourcentage = :pourcentage", 
                Reparateur.class
            );
            query.setParameter("pourcentage", pourcentage);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public List<Reparateur> findByTelephone(String telephone) {
        try {
            TypedQuery<Reparateur> query = em.createQuery(
                "SELECT r FROM Reparateur r WHERE r.telephone = :telephone", 
                Reparateur.class
            );
            query.setParameter("telephone", telephone);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}