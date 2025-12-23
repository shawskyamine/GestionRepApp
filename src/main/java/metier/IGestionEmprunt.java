package metier;

import java.util.List;
import dao.Emprunt;
import exception.EmpruntInvalideException;
import exception.EmpruntDejaRembourseException;
import exception.EmpruntInexistantException;
import exception.OperationImpossibleException;

public interface IGestionEmprunt {
    
    public void ajouter(Emprunt emprunt) throws EmpruntInvalideException, OperationImpossibleException;
    public void supprimer(Long id) throws EmpruntInexistantException, OperationImpossibleException;
    public void modifier(Emprunt emprunt) throws EmpruntInvalideException, EmpruntInexistantException, OperationImpossibleException;
    public Emprunt rechercher(Long id);
    public List<Emprunt> lister() throws OperationImpossibleException;
    
    public void rembourser(Long id) throws EmpruntInexistantException, EmpruntDejaRembourseException, OperationImpossibleException;
    public List<Emprunt> listerParStatut(String statut) throws OperationImpossibleException;
    public List<Emprunt> listerParReparateur(Long reparateurId) throws OperationImpossibleException;
}