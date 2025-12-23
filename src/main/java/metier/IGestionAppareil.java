package metier;

import java.util.List;
import dao.Appareil;
import exception.AppareilDejaExistantException;
import exception.AppareilInvalideException;
import exception.OperationImpossibleException;

public interface IGestionAppareil {
    public void ajouter(Appareil appareil) throws AppareilInvalideException, AppareilDejaExistantException, OperationImpossibleException;
    public void supprimer(String imei) throws AppareilInvalideException, OperationImpossibleException;
    public void modifier(Appareil appareil) throws AppareilInvalideException, OperationImpossibleException;
    public Appareil rechercher(String imei);
    public List<Appareil> lister() throws OperationImpossibleException;
}