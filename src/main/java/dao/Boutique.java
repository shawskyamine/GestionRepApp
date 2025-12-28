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
public class Boutique {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column
    private String nomboutique;
    
    @Column
    private String adresse;
    
    @Column
    @Temporal(TemporalType.DATE)
    private Date dateDeCreation;
    
    // Many Boutiques can be created by one Proprietaire
    @ManyToOne
    @JoinColumn(name = "proprietaire_id")
    private Proprietaire proprietaire;
}