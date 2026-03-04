package pharmacie.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.MailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pharmacie.dao.MedicamentRepository;
import pharmacie.entity.Fournisseur;
import pharmacie.entity.Medicament;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApprovisionnementService {

    private final MedicamentRepository medicamentRepository;
    private final MailSender mailSender;

    @Transactional(readOnly = true)
    public void verifierStocksEtEnvoyerMails() {
        log.info("Démarrage de la vérification des stocks...");

        // 1. Trouver les médicaments à réapprovisionner
        List<Medicament> medicamentsEnRupture = medicamentRepository.findMedicamentsAReapprovisionner();

        if (medicamentsEnRupture.isEmpty()) {
            log.info("Aucun médicament en rupture de stock.");
            return;
        }

        // 2. Grouper les médicaments par fournisseur
        Map<Fournisseur, List<Medicament>> medicamentsParFournisseur = new HashMap<>();

        for (Medicament medicament : medicamentsEnRupture) {
            List<Fournisseur> fournisseurs = medicament.getCategorie().getFournisseurs();

            for (Fournisseur fournisseur : fournisseurs) {
                medicamentsParFournisseur.putIfAbsent(fournisseur, new ArrayList<>());
                medicamentsParFournisseur.get(fournisseur).add(medicament);
            }
        }

        // 3. Envoyer un mail à chaque fournisseur
        for (Map.Entry<Fournisseur, List<Medicament>> entry : medicamentsParFournisseur.entrySet()) {
            envoyerMailAuFournisseur(entry.getKey(), entry.getValue());
        }

        log.info("Processus terminé avec succès.");
    }

    private void envoyerMailAuFournisseur(Fournisseur fournisseur, List<Medicament> medicaments) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(fournisseur.getEmail());
        message.setSubject("Demande de devis de réapprovisionnement");

        StringBuilder texteMail = new StringBuilder();
        texteMail.append("Bonjour ").append(fournisseur.getNom()).append(",\n\n");
        texteMail.append("Veuillez nous transmettre un devis pour les médicaments suivants :\n\n");

        // Grouper les médicaments "catégorie par catégorie" (comme demandé dans l'énoncé)
        Map<String, List<Medicament>> medsParCategorie = new HashMap<>();
        for (Medicament m : medicaments) {
            String nomCat = m.getCategorie().getLibelle();
            medsParCategorie.putIfAbsent(nomCat, new ArrayList<>());
            medsParCategorie.get(nomCat).add(m);
        }

        // Afficher dans le mail
        for (Map.Entry<String, List<Medicament>> catEntry : medsParCategorie.entrySet()) {
            texteMail.append("--- Catégorie : ").append(catEntry.getKey()).append(" ---\n");
            for (Medicament med : catEntry.getValue()) {
                texteMail.append("  - ").append(med.getNom())
                        .append(" (Stock: ").append(med.getUnitesEnStock())
                        .append(", Seuil: ").append(med.getNiveauDeReappro()).append(")\n");
            }
            texteMail.append("\n");
        }

        texteMail.append("Merci d'avance pour votre retour.\nCordialement,\nLa Pharmacie.");

        message.setText(texteMail.toString());

        try {
            mailSender.send(message);
            log.info("Email envoyé au fournisseur : {}", fournisseur.getNom());
        } catch (Exception e) {
            log.error("Erreur d'envoi d'email à {} : {}", fournisseur.getNom(), e.getMessage());
        }
    }
}
