package dao;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.JoinColumn;
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
    private String causeReparation;
    
    @Column
    private int codeReparation;
    
    @Temporal(TemporalType.DATE)
    private Date dateCreation;
    
    @Column
    private int nombreAppareils;
    
    @Column
    private int nombrePiecesReparer;
    
    @Column
    private String statut;
    
    @ManyToOne
    @JoinColumn(name = "reparateur_id")
    private Reparateur reparateur;
    
    @OneToMany(mappedBy = "reparation")
    private List<Appareil> appareils;
    
    @OneToOne
    private Caisse caisse;
}