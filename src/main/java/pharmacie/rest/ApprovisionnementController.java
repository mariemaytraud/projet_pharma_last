package pharmacie.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pharmacie.service.ApprovisionnementService;

@Slf4j
@RestController
@RequestMapping("/api/services/approvisionnement")
@RequiredArgsConstructor
public class ApprovisionnementController {

    private final ApprovisionnementService approvisionnementService;

    /**
     * Déclenche le processus de vérification des stocks et d'envoi des mails.
     * URL : POST
     * http://localhost:8080/api/services/approvisionnement/declencher
     */
    @PostMapping("/declencher")
    public ResponseEntity<String> declencherReapprovisionnement() {
        log.info("Appel REST reçu : /api/services/approvisionnement/declencher");

        // On demande au service de faire son travail
        approvisionnementService.verifierStocksEtEnvoyerMails();

        return ResponseEntity.ok("Processus de réapprovisionnement terminé ! Vérifiez la console et vos e-mails.");
    }
}
