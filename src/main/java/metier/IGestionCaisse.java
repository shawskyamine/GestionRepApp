package metier;

import dao.Caisse;
import dao.Reparation;
import dao.Emprunt;
import exception.EntityNotFoundException;
import exception.InsufficientFundsException;
import exception.DatabaseException;

import java.util.Date;
import java.util.List;

public interface IGestionCaisse {
    // CRUD operations
    void add(Caisse caisse) throws DatabaseException;

    void update(Caisse caisse) throws DatabaseException;

    void delete(Caisse caisse) throws DatabaseException;

    Caisse findById(int id) throws DatabaseException;

    List<Caisse> findAll() throws DatabaseException;

    // Financial operations
    void crediter(int caisseId, Reparation reparation) throws EntityNotFoundException, DatabaseException;

    void debiter(int caisseId, Emprunt emprunt)
            throws EntityNotFoundException, InsufficientFundsException, DatabaseException;

    double getSolde(int caisseId) throws DatabaseException;

    double getTotalCredits(int caisseId) throws DatabaseException;

    double getTotalDebits(int caisseId) throws DatabaseException;

    boolean addTransaction(int reparateurId, String description, String type, double amount, Date date)
            throws DatabaseException;

    // Loan operations
    void prendreEmprunt(int caisseId, String description, double montant, 
                       Date dateEmprunt, Date dateRetour,
                       String typePartenaire, int idPartenaire, String nomPartenaire) 
            throws EntityNotFoundException, DatabaseException;
    
    void donnerEmprunt(int caisseId, String description, double montant, 
                      Date dateEmprunt, Date dateRetour,
                      String typePartenaire, int idPartenaire, String nomPartenaire) 
            throws EntityNotFoundException, InsufficientFundsException, DatabaseException;
    
    void prendreEmpruntExterne(int caisseId, String description, double montant, 
                              Date dateEmprunt, Date dateRetour, String nomExterne) 
            throws EntityNotFoundException, DatabaseException;
    
    void donnerEmpruntExterne(int caisseId, String description, double montant, 
                             Date dateEmprunt, Date dateRetour, String nomExterne) 
            throws EntityNotFoundException, InsufficientFundsException, DatabaseException;
    
    void rembourserEmprunt(int empruntId, Date dateRemboursement) 
            throws EntityNotFoundException, DatabaseException;
    
    // Loan queries
    List<Emprunt> getEmpruntsPris(int caisseId) throws DatabaseException;
    List<Emprunt> getEmpruntsDonnes(int caisseId) throws DatabaseException;
    List<Emprunt> getEmpruntsEnCours(int caisseId) throws DatabaseException;
    List<Emprunt> getEmpruntsEnRetard(int caisseId) throws DatabaseException;
    
    double getTotalEmpruntsPris(int caisseId) throws DatabaseException;
    double getTotalEmpruntsDonnes(int caisseId) throws DatabaseException;
    
    // Relationship queries
    List<Reparation> getReparationsByCaisse(int caisseId) throws DatabaseException;

    List<Emprunt> getEmpruntsByCaisse(int caisseId) throws DatabaseException;
}