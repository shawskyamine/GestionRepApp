package dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appareil {

    @Id
    @Column(length = 20)
    private String imei;

    @Column(length = 50)
    private String couleur;

    @Column(length = 50)
    private String marque;

    @Column(length = 50)
    private String modele;

    @Column(length = 50, name = "type_appareil")
    private String typeAppareil;
}