package pharmacie.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@ToString
public class Fournisseur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE) // la clé est autogénérée par la BD, On ne veut pas de "setter"
    private Long id;

    @NonNull // Lombok, génère une vérification dans le constructeur par défaut
    private String nom;

    @NonNull // Lombok, génère une vérification dans le constructeur par défaut
    private String email;

    // Relation ManyToMany avec Categorie
    @ToString.Exclude
    @JsonIgnoreProperties("fournisseurs") // pour éviter la boucle infinie si on convertit le fournisseur en JSON
    @ManyToMany(mappedBy = "fournisseurs")
    private List<Categorie> categories = new ArrayList<>();

}
