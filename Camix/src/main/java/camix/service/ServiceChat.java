package camix.service;

import camix.Camix;
import camix.communication.ConnexionServeur;
import camix.communication.ProtocoleChat;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.Socket;
import java.util.Hashtable;

/**
 * Classe service chat de Camix.
 *
 * @author Matthias Brun
 * @version 0.5
 */
public final class ServiceChat {
    /**
     * L'ensemble des canaux du chat.
     */
    private final Hashtable<String, CanalChat> canaux;
    /**
     * Le canal par défaut du chat.
     */
    private final CanalChat canalDefaut;
    /**
     * La connexion du serveur.
     */
    private ConnexionServeur connexion;


    /**
     * Constructeur d'un service chat.
     *
     * @param canalDefaut le nom du canal par défaut.
     * @throws IOException exception d'entrée/sortie.
     */
    public ServiceChat(String canalDefaut) throws IOException {
        // Création de l'ensemble des canaux.
        this.canaux = new Hashtable<>();

        // Création du canal par défaut.
        this.canalDefaut = new CanalChat(canalDefaut);
        this.canaux.put(this.canalDefaut.donneNom(), this.canalDefaut);

        // this.lanceService(port);
    }

    /**
     * Lancement du service du chat.
     *
     * @param port le port d'écoute du service.
     * @throws IOException exception d'entrée/sortie.
     */
    public void lanceService(Integer port) throws IOException {
        // Ouverture de la connexion serveur.
        try {
            this.connexion = new ConnexionServeur(port);
        } catch (IOException ex) {
            System.err.print("Problème de création de la connexion serveur.");
            throw ex;
        }

        // Lancement du service.
        try {
            this.service();
        } catch (IOException ex) {
            System.err.print("Problème lors du service du chat.");
            this.ferme();
            throw ex;
        }
    }

    /**
     * Service du chat.
     *
     * @throws IOException exception d'entrée/sortie.
     */
    private void service() throws IOException {
        while (true) {
/*            try {
                // Attente et acceptation d'un client pour le chat.
                final Socket socket = this.connexion.accepteConnexion();

                // Création d'un client.
                // - utilisation du socket de connexion du client comme identifiant.
                final ClientChat client = new ClientChat(this, socket.toString(),
                        Camix.CONFIGURATION.getString("SURNOM_CLIENT_DEFAUT").trim(), this.canalDefaut);

                // Enregistrement du client dans le canal par défaut.
                this.canalDefaut.ajouteClient(client);

                // Lancement d'un thread de service pour le client.
                client.lanceService(socket);

                System.out.println("Ouverture connexion client (id : " + client.donneId() + ")");
                System.out.flush();
            } catch (InterruptedIOException e) {
                break;
            }*/
        }
    }

    /**
     * Fermeture du chat.
     *
     * @throws IOException exception d'entrée/sortie.
     */
    private void ferme() throws IOException {
        // Fermeture de la connexion serveur.
        try {
            this.connexion.ferme();
        } catch (IOException ex) {
            System.err.println("Problème de fermeture de la connexion.");
            throw ex;
        }
    }

    /**
     * Change le canal d'un client.
     *
     * <p>Le canal voulu doit exister.
     * S'il n'existe pas, le client ne change pas de canal et une exception est levée.</p>
     *
     * @param client le client concerné.
     * @param nom    le nom du nouveau canal.
     * @throws CanalChat.Exception si le canal n'existe pas.
     * @see ProtocoleChat
     */
    public void changeCanalClient(ClientChat client, String nom) throws CanalChat.Exception {
        // Synchronization :
        // Pour éviter de bouger un client dans un canal en cours de suppression.
        synchronized (this.canaux) {
            final CanalChat canal = this.canaux.get(nom);

            if (canal != null) {
                client.changeCanal(canal);
            } else {
                throw new CanalChat.Exception(String.format(ProtocoleChat.MESSAGE_NON_EXISTENCE_CANAL_DEMANDE));
            }
        }
    }

    /**
     * Ajoute un canal au chat.
     *
     * <p>Le nom du canal n'est pas contraint.</p>
     * <p>Si un canal du même nom existe déjà, le canal n'est pas créé et une exception est levée.</p>
     *
     * @param nom le nom du nouveau canal.
     * @throws CanalChat.Exception si le canal existe déjà.
     * @see ProtocoleChat
     */
    public void ajouteCanal(String nom) throws CanalChat.Exception {
        // Synchronization :
        // Pour éviter de créer deux canaux de même nom en même temps.
        synchronized (this.canaux) {
            if (this.canaux.get(nom) == null) {
                // Si le canal n'existe pas déjà.
                final CanalChat canal = new CanalChat(nom);
                this.canaux.put(canal.donneNom(), canal);
            } else {
                // Si le canal existe déjà.
                throw new CanalChat.Exception(
                        String.format(ProtocoleChat.MESSAGE_CREATION_IMPOSSIBLE_CANAL, nom));
            }
        }
    }

    /**
     * Supprime un canal du chat.
     *
     * <p>Le canal est supprimé uniquement s'il existe et qu'il est vide.
     * Dans le cas contraire, une exception est levée.</p>
     *
     * <p>Le canal par défaut du chat ne peut pas être supprimé.
     * Une tentative de suppression de ce canal lève une exception.</p>
     *
     * @param nom le nom du canal à supprimer.
     * @throws CanalChat.Exception si le canal ne peut pas être supprimé.
     * @see ProtocoleChat
     */
    public void supprimeCanal(String nom) throws CanalChat.Exception {
        // Synchronization :
        // Pour éviter de supprimer un canal utilisé.
        synchronized (this.canaux) {
            final CanalChat canal = this.canaux.get(nom);

            if (canal != null) {
                // Le canal existe.
                if (canal != this.canalDefaut) {
                    // Le canal n'est pas le canal par défaut du chat.
                    if (canal.donneNombreClients() == 0) {
                        // Le canal est vide (sans client).
                        this.canaux.remove(nom);
                    } else {
                        // Le canal n'est pas vide.
                        throw new CanalChat.Exception(
                                String.format(ProtocoleChat.MESSAGE_SUPPRESSION_CANAL_NON_VIDE, nom));
                    }
                } else {
                    // Le canal est le canal par défaut du chat.
                    throw new CanalChat.Exception(
                            String.format(ProtocoleChat.MESSAGE_SUPPRESSION_CANAL_PAR_DEFAUT, nom));
                }
            } else {
                // Le canal n'existe pas.
                throw new CanalChat.Exception(
                        String.format(ProtocoleChat.MESSAGE_SUPPRESSION_CANAL_INEXISTANT, nom));
            }
        }
    }

    /**
     * Donne les informations sur les canaux disponibles dans le chat.
     *
     * <p>Les canaux disponibles ainsi que le nombre de clients par canaux
     * sont envoyés sous la forme d'une chaîne de caractères."</p>
     *
     * @see ProtocoleChat
     */
    public String donneInformationsCanaux() {
        String informations = String.format(ProtocoleChat.MESSAGE_CANAUX_DISPONIBLES_EN_TETE);

        // Pour chaque canal.
        for (String s : this.canaux.keySet()) {
            final CanalChat canal = this.canaux.get(s);
            informations = informations.concat(
                    String.format(ProtocoleChat.MESSAGE_CANAUX_DISPONIBLES_CANAL,
                            canal.donneNom(), canal.donneNombreClients())
            );
        }
        return informations;
    }
}
