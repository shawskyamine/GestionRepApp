package metier;

import dao.Piece;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.util.List;

public class GestionPiece implements IGestionPiece {
    
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("ORMDemo");
    
    @Override
    public void add(Piece piece) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(piece);
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
    public void update(Piece piece) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(piece);
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
    public void delete(Piece piece) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Piece p = em.find(Piece.class, piece.getId());
            if (p != null) {
                em.remove(p);
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
    public Piece findById(int id) {
        EntityManager em = emf.createEntityManager();
        Piece piece = null;
        try {
            piece = em.find(Piece.class, id);
        } finally {
            em.close();
        }
        return piece;
    }
    
    @Override
    public List<Piece> findAll() {
        EntityManager em = emf.createEntityManager();
        List<Piece> pieces = null;
        try {
            TypedQuery<Piece> query = em.createQuery("SELECT p FROM Piece p", Piece.class);
            pieces = query.getResultList();
        } finally {
            em.close();
        }
        return pieces;
    }
    
    @Override
    public List<Piece> findByNom(String nom) {
        EntityManager em = emf.createEntityManager();
        List<Piece> pieces = null;
        try {
            TypedQuery<Piece> query = em.createQuery(
                "SELECT p FROM Piece p WHERE p.nomPiece = :nom", Piece.class);
            query.setParameter("nom", nom);
            pieces = query.getResultList();
        } finally {
            em.close();
        }
        return pieces;
    }
}