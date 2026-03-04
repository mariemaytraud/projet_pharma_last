package pharmacie.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import pharmacie.entity.Fournisseur;

// Spring va automatiquement générer le code pour interagir avec la table Fournisseur
public interface FournisseurRepository extends JpaRepository<Fournisseur, Long> {
    
    // Pas besoin d'ajouter de requêtes personnalisées pour le moment !
    // JpaRepository nous donne déjà accès à findAll(), findById(), save(), etc.
}