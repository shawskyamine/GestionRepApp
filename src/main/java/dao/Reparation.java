package dao;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reparation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column
    private String codeReparation;
    
    @Column
    private String statut;
    
    @Column
    private String causeDeReparation;
    
    @Column
    @Temporal(TemporalType.DATE)
    private Date dateDeCreation;
    
    @Column
    private int nombreDappareils;
    
    @Column
    private int nombreDePiecesARaparer;
    
    // CORRECTED: +Repare relationship (1..*)
    // One Reparation can repair multiple Appareils
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "reparation_id")
    private List<Appareil> appareils;
    
    // +concerne relationship: Many Reparations to one Reparateur
    @ManyToOne
    @JoinColumn(name = "reparateur_id")
    private Reparateur reparateur;
    
    // +Alimente relationship: Many Reparations to one Caisse
    @ManyToOne
    @JoinColumn(name = "caisse_id")
    private Caisse caisse;
}