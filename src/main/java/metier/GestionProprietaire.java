package metier;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import dao.Proprietaire;
import dao.Boutique;

public class GestionProprietaire implements IGestionProprietaire {

    private EntityManager em;
    
    public GestionProprietaire() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("ClientUP");
        em = emf.createEntityManager();
    }

    @Override
    public void add(Proprietaire proprietaire) {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            em.persist(proprietaire);
            tr.commit();
        } catch (Exception e) {
            tr.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public void update(Proprietaire proprietaire) {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            em.merge(proprietaire);
            tr.commit();
        } catch (Exception e) {
            tr.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Proprietaire proprietaire) {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            em.remove(em.contains(proprietaire) ? proprietaire : em.merge(proprietaire));
            tr.commit();
        } catch (Exception e) {
            tr.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public Proprietaire findById(int id) {
        try {
            return em.find(Proprietaire.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Proprietaire> findAll() {
        try {
            TypedQuery<Proprietaire> query = em.createQuery("SELECT p FROM Proprietaire p", Proprietaire.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Proprietaire> findByEmail(String email) {
        try {
            TypedQuery<Proprietaire> query = em.createQuery(
                "SELECT p FROM Proprietaire p WHERE p.email = :email", 
                Proprietaire.class
            );
            query.setParameter("email", email);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Proprietaire findByEmailAndPassword(String email, String motDePasse) {
        try {
            TypedQuery<Proprietaire> query = em.createQuery(
                "SELECT p FROM Proprietaire p WHERE p.email = :email AND p.motDePasse = :motDePasse", 
                Proprietaire.class
            );
            query.setParameter("email", email);
            query.setParameter("motDePasse", motDePasse);
            return query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Additional useful methods for OneToMany relationship management
    
    public List<Boutique> findBoutiquesByProprietaire(int proprietaireId) {
        try {
            Proprietaire proprietaire = em.find(Proprietaire.class, proprietaireId);
            if (proprietaire != null) {
                // Force loading of the boutiques list
                proprietaire.getBoutiques().size();
                return proprietaire.getBoutiques();
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public long countBoutiquesByProprietaire(int proprietaireId) {
        try {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(b) FROM Boutique b WHERE b.proprietaire.id = :proprietaireId", 
                Long.class
            );
            query.setParameter("proprietaireId", proprietaireId);
            return query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    public List<Proprietaire> findProprietairesWithBoutiques() {
        try {
            TypedQuery<Proprietaire> query = em.createQuery(
                "SELECT DISTINCT p FROM Proprietaire p JOIN FETCH p.boutiques", 
                Proprietaire.class
            );
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public List<Proprietaire> findProprietairesWithoutBoutiques() {
        try {
            TypedQuery<Proprietaire> query = em.createQuery(
                "SELECT p FROM Proprietaire p WHERE p.boutiques IS EMPTY", 
                Proprietaire.class
            );
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}