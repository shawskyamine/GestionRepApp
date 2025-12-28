package metier;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import dao.Piece;
import dao.Appareil;

public class GestionPiece implements IGestionPiece {

    private EntityManager em;
    
    public GestionPiece() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("ClientUP");
        em = emf.createEntityManager();
    }

    @Override
    public void add(Piece piece) {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            em.persist(piece);
            tr.commit();
        } catch (Exception e) {
            tr.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public void update(Piece piece) {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            em.merge(piece);
            tr.commit();
        } catch (Exception e) {
            tr.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Piece piece) {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            em.remove(em.contains(piece) ? piece : em.merge(piece));
            tr.commit();
        } catch (Exception e) {
            tr.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public Piece findById(int id) {
        try {
            return em.find(Piece.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Piece> findAll() {
        try {
            TypedQuery<Piece> query = em.createQuery("SELECT p FROM Piece p", Piece.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Piece> findByNom(String nom) {
        try {
            TypedQuery<Piece> query = em.createQuery(
                "SELECT p FROM Piece p WHERE p.nomPiece = :nom", 
                Piece.class
            );
            query.setParameter("nom", nom);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Additional useful methods for ManyToMany relationship management
    
    public List<Piece> findByAppareil(int appareilId) {
        try {
            TypedQuery<Piece> query = em.createQuery(
                "SELECT p FROM Piece p JOIN p.appareils a WHERE a.id = :appareilId", 
                Piece.class
            );
            query.setParameter("appareilId", appareilId);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public List<Appareil> findAppareilsByPiece(int pieceId) {
        try {
            Piece piece = em.find(Piece.class, pieceId);
            if (piece != null) {
                // Force loading of the appareils list
                piece.getAppareils().size();
                return piece.getAppareils();
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public void addPieceToAppareil(int pieceId, int appareilId) {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            
            Piece piece = em.find(Piece.class, pieceId);
            Appareil appareil = em.find(Appareil.class, appareilId);
            
            if (piece != null && appareil != null) {
                // Add to the owning side of the relationship
                if (!appareil.getPieces().contains(piece)) {
                    appareil.getPieces().add(piece);
                    em.merge(appareil);
                }
            }
            
            tr.commit();
        } catch (Exception e) {
            tr.rollback();
            e.printStackTrace();
        }
    }
    
    public void removePieceFromAppareil(int pieceId, int appareilId) {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            
            Piece piece = em.find(Piece.class, pieceId);
            Appareil appareil = em.find(Appareil.class, appareilId);
            
            if (piece != null && appareil != null) {
                // Remove from the owning side of the relationship
                appareil.getPieces().remove(piece);
                em.merge(appareil);
            }
            
            tr.commit();
        } catch (Exception e) {
            tr.rollback();
            e.printStackTrace();
        }
    }
    
    public List<Piece> findPiecesNotInAppareil(int appareilId) {
        try {
            TypedQuery<Piece> query = em.createQuery(
                "SELECT p FROM Piece p WHERE p.id NOT IN " +
                "(SELECT p2.id FROM Appareil a JOIN a.pieces p2 WHERE a.id = :appareilId)", 
                Piece.class
            );
            query.setParameter("appareilId", appareilId);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public long countAppareilsByPiece(int pieceId) {
        try {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(a) FROM Appareil a JOIN a.pieces p WHERE p.id = :pieceId", 
                Long.class
            );
            query.setParameter("pieceId", pieceId);
            return query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    public List<Piece> findCommonPieces(int appareil1Id, int appareil2Id) {
        try {
            TypedQuery<Piece> query = em.createQuery(
                "SELECT p FROM Piece p " +
                "WHERE p IN (SELECT p1 FROM Appareil a1 JOIN a1.pieces p1 WHERE a1.id = :appareil1Id) " +
                "AND p IN (SELECT p2 FROM Appareil a2 JOIN a2.pieces p2 WHERE a2.id = :appareil2Id)", 
                Piece.class
            );
            query.setParameter("appareil1Id", appareil1Id);
            query.setParameter("appareil2Id", appareil2Id);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}