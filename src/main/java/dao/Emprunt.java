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
    private Date dateEmprunt; // Note: typo in diagram, should be "dateEmprunt"
    
    @Column
    @Temporal(TemporalType.DATE)
    private Date dateDeRetour;
    
    @Column
    private String etat;
    
    @Column
    private double montant;
    
    
    // Many Emprunts are provided by one Caisse
    @ManyToOne
    @JoinColumn(name = "caisse_id")
    private Caisse caisse;
}