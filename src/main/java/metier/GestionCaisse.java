package metier;

import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import dao.Caisse;
import dao.Reparation;
import dao.Emprunt;
import dao.Reparateur;
import exception.EntityNotFoundException;
import exception.InsufficientFundsException;
import exception.DatabaseException;
import java.util.stream.Collectors;

public class GestionCaisse implements IGestionCaisse {

    private EntityManager em;

    public GestionCaisse() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("ClientUP");
        em = emf.createEntityManager();
    }

    @Override
    public void add(Caisse caisse) throws DatabaseException {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            em.merge(caisse);
            tr.commit();
        } catch (Exception e) {
            tr.rollback();
            throw new DatabaseException("Failed to add caisse", e);
        }
    }

    @Override
    public void update(Caisse caisse) throws DatabaseException {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            em.merge(caisse);
            tr.commit();
        } catch (Exception e) {
            tr.rollback();
            throw new DatabaseException("Failed to update caisse", e);
        }
    }

    @Override
    public void delete(Caisse caisse) throws DatabaseException {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            em.remove(em.contains(caisse) ? caisse : em.merge(caisse));
            tr.commit();
        } catch (Exception e) {
            tr.rollback();
            throw new DatabaseException("Failed to delete caisse", e);
        }
    }

    @Override
    public Caisse findById(int id) throws DatabaseException {
        try {
            return em.find(Caisse.class, id);
        } catch (Exception e) {
            throw new DatabaseException("Failed to find caisse by id", e);
        }
    }

    @Override
    public List<Caisse> findAll() throws DatabaseException {
        try {
            TypedQuery<Caisse> query = em.createQuery("SELECT c FROM Caisse c", Caisse.class);
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Failed to find all caisses", e);
        }
    }

    @Override
    public void crediter(int caisseId, Reparation reparation) throws EntityNotFoundException, DatabaseException {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();

            // Find the caisse
            Caisse caisse = em.find(Caisse.class, caisseId);
            if (caisse == null) {
                throw new EntityNotFoundException("Caisse not found with id: " + caisseId);
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
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            if (tr != null && tr.isActive()) {
                tr.rollback();
            }
            throw new DatabaseException("Failed to credit caisse", e);
        }
    }

    @Override
    public void debiter(int caisseId, Emprunt emprunt)
            throws EntityNotFoundException, InsufficientFundsException, DatabaseException {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();

            // Find the caisse
            Caisse caisse = em.find(Caisse.class, caisseId);
            if (caisse == null) {
                throw new EntityNotFoundException("Caisse not found with id: " + caisseId);
            }

            // Check if caisse has enough funds
            double solde = getSolde(caisseId);
            if (solde < emprunt.getMontant()) {
                throw new InsufficientFundsException(
                        "Solde insuffisant dans la caisse: " + solde + " < " + emprunt.getMontant());
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
        } catch (EntityNotFoundException | InsufficientFundsException e) {
            throw e;
        } catch (Exception e) {
            if (tr != null && tr.isActive()) {
                tr.rollback();
            }
            throw new DatabaseException("Failed to debit caisse", e);
        }
    }

    @Override
    public double getSolde(int caisseId) throws DatabaseException {
        try {
            double totalCredits = getTotalCredits(caisseId);
            double totalDebits = getTotalDebits(caisseId);
            double totalEmpruntsPris = getTotalEmpruntsPris(caisseId);
            double totalEmpruntsDonnes = getTotalEmpruntsDonnes(caisseId);
            
            // Formula: (Credits + Loans Taken) - (Debits + Loans Given)
            return (totalCredits + totalEmpruntsPris) - (totalDebits + totalEmpruntsDonnes);
        } catch (Exception e) {
            throw new DatabaseException("Failed to get solde", e);
        }
    }

    @Override
    public double getTotalCredits(int caisseId) throws DatabaseException {
        try {
            TypedQuery<Double> query = em.createQuery(
                    "SELECT COALESCE(SUM(r.prixTotal * (r.reparateur.pourcentage / 100.0)), 0.0) FROM Reparation r WHERE r.caisse.id = :caisseId AND r.statut = 'Terminée'",
                    Double.class);
            query.setParameter("caisseId", caisseId);
            return query.getSingleResult();
        } catch (Exception e) {
            throw new DatabaseException("Failed to get total credits", e);
        }
    }

    @Override
    public double getTotalDebits(int caisseId) throws DatabaseException {
        try {
            TypedQuery<Double> query = em.createQuery(
                    "SELECT COALESCE(SUM(e.montant), 0.0) FROM Emprunt e WHERE e.caisse.id = :caisseId AND e.typeTransaction = 'EMPRUNT_DONNE'",
                    Double.class);
            query.setParameter("caisseId", caisseId);
            return query.getSingleResult();
        } catch (Exception e) {
            throw new DatabaseException("Failed to get total debits", e);
        }
    }

    @Override
    public List<Reparation> getReparationsByCaisse(int caisseId) throws DatabaseException {
        try {
            TypedQuery<Reparation> query = em.createQuery(
                    "SELECT r FROM Reparation r WHERE r.caisse.id = :caisseId",
                    Reparation.class);
            query.setParameter("caisseId", caisseId);
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Failed to get reparations by caisse", e);
        }
    }

    @Override
    public List<Emprunt> getEmpruntsByCaisse(int caisseId) throws DatabaseException {
        try {
            TypedQuery<Emprunt> query = em.createQuery(
                    "SELECT e FROM Emprunt e WHERE e.caisse.id = :caisseId",
                    Emprunt.class);
            query.setParameter("caisseId", caisseId);
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Failed to get emprunts by caisse", e);
        }
    }

    public List<Reparation> getAllReparations() throws DatabaseException {
        try {
            TypedQuery<Reparation> query = em.createQuery("SELECT r FROM Reparation r", Reparation.class);
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Failed to get all reparations", e);
        }
    }

    public List<Emprunt> getAllEmprunts() throws DatabaseException {
        try {
            TypedQuery<Emprunt> query = em.createQuery("SELECT e FROM Emprunt e", Emprunt.class);
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Failed to get all emprunts", e);
        }
    }

    @Override
    public boolean addTransaction(int reparateurId, String description, String type, double amount, Date date)
            throws DatabaseException {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();

            // Find the reparateur
            Reparateur reparateur = em.find(Reparateur.class, reparateurId);
            if (reparateur == null || reparateur.getCaisse() == null) {
                if (tr.isActive())
                    tr.rollback();
                return false;
            }

            Caisse caisse = reparateur.getCaisse();

            // Create a new emprunt for debit transactions
            if ("Débit".equalsIgnoreCase(type)) {
                Emprunt emprunt = Emprunt.builder()
                        .dateEmprunt(date)
                        .montant(amount)
                        .etat("En cours")
                        .caisse(caisse)
                        .build();
                em.persist(emprunt);
            }
            // Note: For "Crédit" transactions, they come from Reparations, not manual entry

            tr.commit();
            return true;

        } catch (Exception e) {
            if (tr.isActive()) {
                tr.rollback();
            }
            throw new DatabaseException("Failed to add transaction", e);
        }
    }
    
    // ========== LOAN METHODS ==========
    
    @Override
    public void prendreEmprunt(int caisseId, String description, double montant, 
                              Date dateEmprunt, Date dateRetour, 
                              String typePartenaire, int idPartenaire, String nomPartenaire) 
            throws EntityNotFoundException, DatabaseException {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            
            // Find the caisse
            Caisse caisse = em.find(Caisse.class, caisseId);
            if (caisse == null) {
                throw new EntityNotFoundException("Caisse not found with id: " + caisseId);
            }
            
            // Create emprunt record
            Emprunt emprunt = Emprunt.builder()
                    .dateEmprunt(dateEmprunt)
                    .dateDeRetour(dateRetour)
                    .montant(montant)
                    .etat("EN_COURS")
                    .typeTransaction("EMPRUNT_PRIS")
                    .description(description)
                    .idPartenaire(idPartenaire)
                    .nomPartenaire(nomPartenaire)
                    .rolePartenaire(typePartenaire)
                    .estExterne(false)
                    .caisse(caisse)
                    .build();
            
            em.persist(emprunt);
            
            tr.commit();
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            if (tr != null && tr.isActive()) {
                tr.rollback();
            }
            throw new DatabaseException("Failed to take loan", e);
        }
    }
    
    @Override
    public void donnerEmprunt(int caisseId, String description, double montant, 
                             Date dateEmprunt, Date dateRetour,
                             String typePartenaire, int idPartenaire, String nomPartenaire) 
            throws EntityNotFoundException, InsufficientFundsException, DatabaseException {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            
            // Find the caisse
            Caisse caisse = em.find(Caisse.class, caisseId);
            if (caisse == null) {
                throw new EntityNotFoundException("Caisse not found with id: " + caisseId);
            }
            
            // Check if caisse has enough funds
            double solde = getSolde(caisseId);
            if (solde < montant) {
                throw new InsufficientFundsException(
                        "Solde insuffisant pour prêter: " + solde + " < " + montant);
            }
            
            // Create emprunt record
            Emprunt emprunt = Emprunt.builder()
                    .dateEmprunt(dateEmprunt)
                    .dateDeRetour(dateRetour)
                    .montant(montant)
                    .etat("EN_COURS")
                    .typeTransaction("EMPRUNT_DONNE")
                    .description(description)
                    .idPartenaire(idPartenaire)
                    .nomPartenaire(nomPartenaire)
                    .rolePartenaire(typePartenaire)
                    .estExterne(false)
                    .caisse(caisse)
                    .build();
            
            em.persist(emprunt);
            
            tr.commit();
        } catch (EntityNotFoundException | InsufficientFundsException e) {
            throw e;
        } catch (Exception e) {
            if (tr != null && tr.isActive()) {
                tr.rollback();
            }
            throw new DatabaseException("Failed to give loan", e);
        }
    }
    
    @Override
    public void prendreEmpruntExterne(int caisseId, String description, double montant, 
                                     Date dateEmprunt, Date dateRetour, String nomExterne) 
            throws EntityNotFoundException, DatabaseException {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            
            Caisse caisse = em.find(Caisse.class, caisseId);
            if (caisse == null) {
                throw new EntityNotFoundException("Caisse not found with id: " + caisseId);
            }
            
            Emprunt emprunt = Emprunt.builder()
                    .dateEmprunt(dateEmprunt)
                    .dateDeRetour(dateRetour)
                    .montant(montant)
                    .etat("EN_COURS")
                    .typeTransaction("EMPRUNT_PRIS")
                    .description(description)
                    .estExterne(true)
                    .nomExterne(nomExterne)
                    .caisse(caisse)
                    .build();
            
            em.persist(emprunt);
            
            tr.commit();
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            if (tr != null && tr.isActive()) {
                tr.rollback();
            }
            throw new DatabaseException("Failed to take external loan", e);
        }
    }
    
    @Override
    public void donnerEmpruntExterne(int caisseId, String description, double montant, 
                                    Date dateEmprunt, Date dateRetour, String nomExterne) 
            throws EntityNotFoundException, InsufficientFundsException, DatabaseException {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            
            Caisse caisse = em.find(Caisse.class, caisseId);
            if (caisse == null) {
                throw new EntityNotFoundException("Caisse not found with id: " + caisseId);
            }
            
            // Check if caisse has enough funds
            double solde = getSolde(caisseId);
            if (solde < montant) {
                throw new InsufficientFundsException(
                        "Solde insuffisant pour prêter: " + solde + " < " + montant);
            }
            
            Emprunt emprunt = Emprunt.builder()
                    .dateEmprunt(dateEmprunt)
                    .dateDeRetour(dateRetour)
                    .montant(montant)
                    .etat("EN_COURS")
                    .typeTransaction("EMPRUNT_DONNE")
                    .description(description)
                    .estExterne(true)
                    .nomExterne(nomExterne)
                    .caisse(caisse)
                    .build();
            
            em.persist(emprunt);
            
            tr.commit();
        } catch (EntityNotFoundException | InsufficientFundsException e) {
            throw e;
        } catch (Exception e) {
            if (tr != null && tr.isActive()) {
                tr.rollback();
            }
            throw new DatabaseException("Failed to give external loan", e);
        }
    }
    
    @Override
    public void rembourserEmprunt(int empruntId, Date dateRemboursement) 
            throws EntityNotFoundException, DatabaseException {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            
            Emprunt emprunt = em.find(Emprunt.class, empruntId);
            if (emprunt == null) {
                throw new EntityNotFoundException("Emprunt not found with id: " + empruntId);
            }
            
            if ("REMBOURSE".equals(emprunt.getEtat())) {
                throw new DatabaseException("Loan already repaid");
            }
            
            emprunt.setEtat("REMBOURSE");
            em.merge(emprunt);
            
            tr.commit();
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            if (tr != null && tr.isActive()) {
                tr.rollback();
            }
            throw new DatabaseException("Failed to repay loan", e);
        }
    }
    
    @Override
    public List<Emprunt> getEmpruntsPris(int caisseId) throws DatabaseException {
        try {
            TypedQuery<Emprunt> query = em.createQuery(
                    "SELECT e FROM Emprunt e WHERE e.caisse.id = :caisseId AND e.typeTransaction = 'EMPRUNT_PRIS' ORDER BY e.dateEmprunt DESC",
                    Emprunt.class);
            query.setParameter("caisseId", caisseId);
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Failed to get loans taken", e);
        }
    }
    
    @Override
    public List<Emprunt> getEmpruntsDonnes(int caisseId) throws DatabaseException {
        try {
            TypedQuery<Emprunt> query = em.createQuery(
                    "SELECT e FROM Emprunt e WHERE e.caisse.id = :caisseId AND e.typeTransaction = 'EMPRUNT_DONNE' ORDER BY e.dateEmprunt DESC",
                    Emprunt.class);
            query.setParameter("caisseId", caisseId);
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Failed to get loans given", e);
        }
    }
    
    @Override
    public List<Emprunt> getEmpruntsEnCours(int caisseId) throws DatabaseException {
        try {
            TypedQuery<Emprunt> query = em.createQuery(
                    "SELECT e FROM Emprunt e WHERE e.caisse.id = :caisseId AND e.etat = 'EN_COURS' ORDER BY e.dateDeRetour ASC",
                    Emprunt.class);
            query.setParameter("caisseId", caisseId);
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Failed to get active loans", e);
        }
    }
    
    @Override
    public List<Emprunt> getEmpruntsEnRetard(int caisseId) throws DatabaseException {
        try {
            // Get all active loans
            List<Emprunt> emprunts = getEmpruntsEnCours(caisseId);
            // Filter for overdue loans
            return emprunts.stream()
                    .filter(Emprunt::isEnRetard)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new DatabaseException("Failed to get overdue loans", e);
        }
    }
    
    @Override
    public double getTotalEmpruntsPris(int caisseId) throws DatabaseException {
        try {
            TypedQuery<Double> query = em.createQuery(
                    "SELECT COALESCE(SUM(e.montant), 0.0) FROM Emprunt e " +
                    "WHERE e.caisse.id = :caisseId AND e.typeTransaction = 'EMPRUNT_PRIS' AND e.etat = 'EN_COURS'",
                    Double.class);
            query.setParameter("caisseId", caisseId);
            return query.getSingleResult();
        } catch (Exception e) {
            throw new DatabaseException("Failed to get total loans taken", e);
        }
    }
    
    @Override
    public double getTotalEmpruntsDonnes(int caisseId) throws DatabaseException {
        try {
            TypedQuery<Double> query = em.createQuery(
                    "SELECT COALESCE(SUM(e.montant), 0.0) FROM Emprunt e " +
                    "WHERE e.caisse.id = :caisseId AND e.typeTransaction = 'EMPRUNT_DONNE' AND e.etat = 'EN_COURS'",
                    Double.class);
            query.setParameter("caisseId", caisseId);
            return query.getSingleResult();
        } catch (Exception e) {
            throw new DatabaseException("Failed to get total loans given", e);
        }
    }
}