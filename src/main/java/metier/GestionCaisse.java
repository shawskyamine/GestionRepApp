package metier;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import dao.Caisse;
import dao.Reparation;
import dao.Emprunt;

public class GestionCaisse implements IGestionCaisse {

    private EntityManager em;
    
    public GestionCaisse() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("ClientUP");
        em = emf.createEntityManager();
    }

    @Override
    public void add(Caisse caisse) {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            em.persist(caisse);
            tr.commit();
        } catch (Exception e) {
            tr.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public void update(Caisse caisse) {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            em.merge(caisse);
            tr.commit();
        } catch (Exception e) {
            tr.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Caisse caisse) {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            em.remove(em.contains(caisse) ? caisse : em.merge(caisse));
            tr.commit();
        } catch (Exception e) {
            tr.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public Caisse findById(int id) {
        try {
            return em.find(Caisse.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Caisse> findAll() {
        try {
            TypedQuery<Caisse> query = em.createQuery("SELECT c FROM Caisse c", Caisse.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void crediter(int caisseId, Reparation reparation) {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            
            // Find the caisse
            Caisse caisse = em.find(Caisse.class, caisseId);
            if (caisse == null) {
                throw new Exception("Caisse not found");
            }
            
            // Set the relationship
            reparation.setCaisse(caisse);
            
            // Persist or merge the reparation
            if (reparation.getId() == 0) {
                em.persist(reparation);
            } else {
                em.merge(reparation);
            }
            
            tr.commit();
        } catch (Exception e) {
            if (tr != null && tr.isActive()) {
                tr.rollback();
            }
            e.printStackTrace();
        }
    }

    @Override
    public void debiter(int caisseId, Emprunt emprunt) {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            
            // Find the caisse
            Caisse caisse = em.find(Caisse.class, caisseId);
            if (caisse == null) {
                throw new Exception("Caisse not found");
            }
            
            // Check if caisse has enough funds
            double solde = getSolde(caisseId);
            if (solde < emprunt.getMontant()) {
                throw new Exception("Solde insuffisant dans la caisse");
            }
            
            // Set the relationship
            emprunt.setCaisse(caisse);
            
            // Persist or merge the emprunt
            if (emprunt.getId() == 0) {
                em.persist(emprunt);
            } else {
                em.merge(emprunt);
            }
            
            tr.commit();
        } catch (Exception e) {
            if (tr != null && tr.isActive()) {
                tr.rollback();
            }
            e.printStackTrace();
        }
    }

    @Override
    public double getSolde(int caisseId) {
        try {
            double totalCredits = getTotalCredits(caisseId);
            double totalDebits = getTotalDebits(caisseId);
            return totalCredits - totalDebits;
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    @Override
    public double getTotalCredits(int caisseId) {
        try {
            TypedQuery<Double> query = em.createQuery(
                "SELECT COALESCE(SUM(r.montant), 0.0) FROM Reparation r WHERE r.caisse.id = :caisseId",
                Double.class
            );
            query.setParameter("caisseId", caisseId);
            return query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    @Override
    public double getTotalDebits(int caisseId) {
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

    @Override
    public List<Reparation> getReparationsByCaisse(int caisseId) {
        try {
            TypedQuery<Reparation> query = em.createQuery(
                "SELECT r FROM Reparation r WHERE r.caisse.id = :caisseId",
                Reparation.class
            );
            query.setParameter("caisseId", caisseId);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Emprunt> getEmpruntsByCaisse(int caisseId) {
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
}