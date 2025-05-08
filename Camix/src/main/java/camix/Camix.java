package camix;

import camix.service.ServiceChat;

import java.io.IOException;
import java.util.ResourceBundle;

/**
 * Classe principale du programme Camix.
 *
 * <p>
 * Programme à usage pédagogique.
 * ATTENTION : Ce programme comporte des fautes intentionnelles.
 * </p>
 *
 * @author Matthias Brun
 * @version 0.5
 */
public final class Camix {
    /**
     * Fichier de configuration du serveur.
     */
    public static final ResourceBundle CONFIGURATION = ResourceBundle.getBundle("Camix");

    /**
     * Constructeur privé de Camix.
     *
     * <p>Ce constructeur privé assure la non-instanciation de Camix dans un programme.
     * (Camix est la classe principale du programme Camix)</p>
     */
    private Camix() {
        // Constructeur privé pour assurer la non-instanciation de Camix.
    }

    /**
     * Main du programme.
     *
     * <p>Cette fonction main lance le service du chat.</p>
     *
     * @param args aucun argument attendu.
     */
    public static void main(String[] args) {
        System.out.println("Camix v0.5");
        System.out.println("Port serveur : " + Camix.CONFIGURATION.getString("PORT_SERVICE_CHAT").trim());

        try {
            // Création du service.
            new ServiceChat(
                    Camix.CONFIGURATION.getString("CANAL_PAR_DEFAUT").trim()).lanceService(
                    Integer.parseInt(Camix.CONFIGURATION.getString("PORT_SERVICE_CHAT").trim()));
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

}
