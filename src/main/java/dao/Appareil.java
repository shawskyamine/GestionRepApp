package dao;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.ManyToMany;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
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
public class Appareil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column
    private String couleur;
    
    @Column
    private String imei;
    
    @Column
    private String marque;
    
    @Column
    private String modele;
    
    @Column
    private String typeAppareil;
    
    @ManyToOne
    @JoinColumn(name = "reparation_id")
    private Reparation reparation;
    
    @ManyToMany
    @JoinTable(
        name = "appareil_piece",
        joinColumns = @JoinColumn(name = "appareil_id"),
        inverseJoinColumns = @JoinColumn(name = "piece_id")
    )
    private List<Piece> pieces;
}