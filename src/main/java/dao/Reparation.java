package dao;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Reparation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
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

    @Column
    private double prixTotal;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "reparation_id")
    private List<Appareil> appareils;

    @ManyToOne
    @JoinColumn(name = "reparateur_id")
    private Reparateur reparateur;

    @ManyToOne
    @JoinColumn(name = "caisse_id")
    private Caisse caisse;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne
    @JoinColumn(name = "boutique_id")
    private Boutique boutique;

    @Override
    public String toString() {
        return "Reparation{" +
                "id=" + id +
                ", codeReparation='" + codeReparation + '\'' +
                ", statut='" + statut + '\'' +
                ", causeDeReparation='" + causeDeReparation + '\'' +
                ", dateDeCreation=" + dateDeCreation +
                ", nombreDappareils=" + nombreDappareils +
                ", nombreDePiecesARaparer=" + nombreDePiecesARaparer +
                ", prixTotal=" + prixTotal +
                ", client=" + (client != null ? client.getNom() : "null") +
                '}';
    }
}