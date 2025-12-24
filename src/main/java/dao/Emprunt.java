package dao;

import javax.persistence.*;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Emprunt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private double montant;

    @Temporal(TemporalType.DATE)
    private Date dateEmprunt;  // "Date ennunt" in diagram

    @Temporal(TemporalType.DATE)
    private Date dateRetour;   // "Date de retour" in diagram

    @Column(length = 50)
    private String etat;       // "Etat" in diagram

    private int preteurId;     // "Preteur id" in diagram
}