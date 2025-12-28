package dao;

import javax.persistence.*;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class Proprietaire extends Utilisateur {
    @Column
    private String email;
    
    @Column
    private String motDePasse;
    
    // One Proprietaire can create many Boutiques (1..*)
    @OneToMany(mappedBy = "proprietaire", cascade = CascadeType.ALL)
    private List<Boutique> boutiques;
}