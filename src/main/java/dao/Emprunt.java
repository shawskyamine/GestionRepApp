package dao;

import javax.persistence.*;
import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Emprunt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column
    @Temporal(TemporalType.DATE)
    private Date dateEmprunt;
    
    @Column
    @Temporal(TemporalType.DATE)
    private Date dateDeRetour;
    
    @Column
    private String etat; // "EN_COURS", "REMBOURSE", "EN_RETARD", "ANNULE"
    
    @Column
    private double montant;
    
    @Column
    private String typeTransaction; // "EMPRUNT_PRIS" (borrowed), "EMPRUNT_DONNE" (lent)
    
    @Column
    private String description;
    
    // For loans between system users
    @Column
    private Integer idPartenaire; // ID of the other party (reparateur or proprietaire)
    
    @Column
    private String nomPartenaire; // Name of the other party
    
    @Column
    private String rolePartenaire; // "REPARATEUR" or "PROPRIETAIRE"
    
    // For external loans (outside the system)
    @Column
    private boolean estExterne;
    
    @Column
    private String nomExterne; // Name of external party
    
    // Many Emprunts are provided by one Caisse
    @ManyToOne
    @JoinColumn(name = "caisse_id")
    private Caisse caisse;
    
    // Calculate if loan is overdue
    public boolean isEnRetard() {
        if (dateDeRetour == null || "REMBOURSE".equals(etat) || "ANNULE".equals(etat)) {
            return false;
        }
        return new Date().after(dateDeRetour);
    }
}