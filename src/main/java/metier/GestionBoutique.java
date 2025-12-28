package metier;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import dao.Boutique;

public class GestionBoutique implements IGestionBoutique {

    private EntityManager em;
    
    public GestionBoutique() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("ClientUP");
        em = emf.createEntityManager();
    }

    @Override
    public void add(Boutique boutique) {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            em.persist(boutique);
            tr.commit();
        } catch (Exception e) {
            tr.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public void update(Boutique boutique) {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            em.merge(boutique);
            tr.commit();
        } catch (Exception e) {
            tr.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Boutique boutique) {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            em.remove(em.contains(boutique) ? boutique : em.merge(boutique));
            tr.commit();
        } catch (Exception e) {
            tr.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public Boutique findById(int id) {
        try {
            return em.find(Boutique.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Boutique> findAll() {
        try {
            TypedQuery<Boutique> query = em.createQuery("SELECT b FROM Boutique b", Boutique.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Boutique> findByNom(String nom) {
        try {
            TypedQuery<Boutique> query = em.createQuery(
                "SELECT b FROM Boutique b WHERE b.nomboutique = :nom", 
                Boutique.class
            );
            query.setParameter("nom", nom);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Boutique> findByProprietaire(int proprietaireId) {
        try {
            TypedQuery<Boutique> query = em.createQuery(
                "SELECT b FROM Boutique b WHERE b.proprietaire.id = :proprietaireId", 
                Boutique.class
            );
            query.setParameter("proprietaireId", proprietaireId);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}