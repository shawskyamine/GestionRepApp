package metier;

import java.util.Date;
import java.util.List;
import dao.Emprunt;
import exception.EmpruntInvalideException;
import exception.EmpruntDejaRembourseException;
import exception.EmpruntInexistantException;
import exception.OperationImpossibleException;

public interface IGestionEmprunt {
    
    // CRUD Operations
    public void ajouter(Emprunt emprunt) throws EmpruntInvalideException, OperationImpossibleException;
    
    public void supprimer(int id) throws EmpruntInvalideException, EmpruntInexistantException, OperationImpossibleException;
    
    public void modifier(Emprunt emprunt) throws EmpruntInvalideException, EmpruntInexistantException, OperationImpossibleException;
    
    public Emprunt rechercher(int id);
    
    public List<Emprunt> lister() throws OperationImpossibleException;
    
    // Specific Emprunt Operations
    public void rembourser(int id) throws EmpruntInvalideException, EmpruntInexistantException, EmpruntDejaRembourseException, OperationImpossibleException;
    
    public void retourner(int id, Date dateRetour) throws EmpruntInvalideException, EmpruntInexistantException, OperationImpossibleException;
    
    // Search/Filter Operations
    public List<Emprunt> listerParEtat(String etat) throws OperationImpossibleException;
    
    public List<Emprunt> listerParPreteur(int preteurId) throws OperationImpossibleException;
    
    public List<Emprunt> listerParDateEmprunt(Date dateDebut, Date dateFin) throws OperationImpossibleException;
    
    // Status Check Operations
    public double getMontantTotalEmprunte() throws OperationImpossibleException;
    
    public double getMontantTotalNonRembourse() throws OperationImpossibleException;
}