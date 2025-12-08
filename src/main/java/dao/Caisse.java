package dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
public class Caisse {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@Column(length = 50)
	private Double solde;
	@Column(length = 50)
	private String type;
	
}







