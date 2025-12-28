package metier;

import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import dao.Emprunt;

public class GestionEmprunt implements IGestionEmprunt {

    private EntityManager em;
    
    public GestionEmprunt() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("ClientUP");
        em = emf.createEntityManager();
    }

    @Override
    public void add(Emprunt emprunt) {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            em.persist(emprunt);
            tr.commit();
        } catch (Exception e) {
            tr.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public void update(Emprunt emprunt) {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            em.merge(emprunt);
            tr.commit();
        } catch (Exception e) {
            tr.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Emprunt emprunt) {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            em.remove(em.contains(emprunt) ? emprunt : em.merge(emprunt));
            tr.commit();
        } catch (Exception e) {
            tr.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public Emprunt findById(int id) {
        try {
            return em.find(Emprunt.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Emprunt> findAll() {
        try {
            TypedQuery<Emprunt> query = em.createQuery("SELECT e FROM Emprunt e", Emprunt.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Emprunt> findByEtat(String etat) {
        try {
            TypedQuery<Emprunt> query = em.createQuery(
                "SELECT e FROM Emprunt e WHERE e.etat = :etat", 
                Emprunt.class
            );
            query.setParameter("etat", etat);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Emprunt> findByCaisse(int caisseId) {
        try {
            TypedQuery<Emprunt> query = em.createQuery(
                "SELECT e FROM Emprunt e WHERE e.caisse.id = :caisseId", 
                Emprunt.class
            );
            query.setParameter("caisseId", caisseId);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Emprunt> findByDateRange(Date startDate, Date endDate) {
        try {
            TypedQuery<Emprunt> query = em.createQuery(
                "SELECT e FROM Emprunt e WHERE e.dateEmprunt BETWEEN :startDate AND :endDate", 
                Emprunt.class
            );
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Additional useful methods
    public List<Emprunt> findByPreteur(int preteurId) {
        try {
            TypedQuery<Emprunt> query = em.createQuery(
                "SELECT e FROM Emprunt e WHERE e.preteurId = :preteurId", 
                Emprunt.class
            );
            query.setParameter("preteurId", preteurId);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public List<Emprunt> findEmpruntsNonRetournes() {
        try {
            TypedQuery<Emprunt> query = em.createQuery(
                "SELECT e FROM Emprunt e WHERE e.etat != 'Retourné' OR e.etat IS NULL", 
                Emprunt.class
            );
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public List<Emprunt> findEmpruntsEnRetard() {
        try {
            TypedQuery<Emprunt> query = em.createQuery(
                "SELECT e FROM Emprunt e WHERE e.dateDeRetour < CURRENT_DATE AND (e.etat != 'Retourné' OR e.etat IS NULL)", 
                Emprunt.class
            );
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public double getTotalMontantByPreteur(int preteurId) {
        try {
            TypedQuery<Double> query = em.createQuery(
                "SELECT COALESCE(SUM(e.montant), 0.0) FROM Emprunt e WHERE e.preteurId = :preteurId", 
                Double.class
            );
            query.setParameter("preteurId", preteurId);
            return query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }
    
    public double getTotalMontantByCaisse(int caisseId) {
        try {
            TypedQuery<Double> query = em.createQuery(
                "SELECT COALESCE(SUM(e.montant), 0.0) FROM Emprunt e WHERE e.caisse.id = :caisseId", 
                Double.class
            );
            query.setParameter("caisseId", caisseId);
            return query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }
}