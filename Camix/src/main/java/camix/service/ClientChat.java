package camix.service;

import camix.communication.ConnexionClient;
import camix.communication.ProtocoleChat;

import java.io.IOException;
import java.net.Socket;

/**
 * Classe client du serveur.
 *
 * @author Matthias Brun
 * @version 0.5
 */
public class ClientChat extends Thread {
    /**
     * Identifiant du client.
     */
    private final String id;
    /**
     * Service Chat du client.
     */
    private final ServiceChat chat;
    /**
     * Le surnom du client.
     */
    private String surnom;
    /**
     * Le canal du client.
     */
    private CanalChat canal;
    /**
     * La connexion réseau avec le client.
     */
    private ConnexionClient connexion;


    /**
     * Constructeur d'un client du chat.
     *
     * @param chat   le chat du client.
     * @param id     l'identifiant du client.
     * @param surnom le surnom du client.
     * @param canal  le canal du client.
     */
    public ClientChat(ServiceChat chat, String id, String surnom, CanalChat canal) {
        super();

        this.chat = chat;
        this.id = id;
        this.surnom = surnom;
        this.canal = canal;
    }

    /**
     * Accesseur à l'identifiant (interne) du client.
     *
     * @return l'identifiant du client.
     */
    public String donneId() {
        return this.id;
    }

    /**
     * Lancement du service à un client.
     *
     * @param socket le socket de connexion du client.
     * @throws IOException exception d'entrée/sortie.
     */
    public void lanceService(Socket socket) throws IOException {
        try {
            // Création d'une connexion réseau avec le client.
            this.connexion = new ConnexionClient(socket);
        } catch (IOException ex) {
            System.err.println("Problème de mise en place d'une gestion de client.");
            throw ex;
        }
        this.start();

        // Informe de l'arrivée du client dans le chat.
        informeArrivee();
    }

    /**
     * Point d'entrée du thread de service au client
     * (atteint via start() dans le lancement du service au client).
     *
     * <p>Lecture de messages sur la socket de communication avec le client puis traitement du message.</p>
     * <p>S'arrête quand le message est <tt>null</tt>.</p>
     */
    public void run() {
        try {
            while (true) {
                final String message = this.connexion.lire();
                if (message == null) break; // Fermeture de la connexion par le client.

                traiteMessage(message);
            }
        } catch (IOException e) {
            System.err.println("Problème de gestion d'un client - id : " + this.id);
            System.err.println(e.getMessage());
        } finally {
            fermeConnexion();
        }
    }

    /**
     * Informe de l'arrivée du client dans le chat.
     *
     * <p>Le message d'arrivée d'un client dans le chat est envoyé à tous les clients du canal du client.</p>
     * <p>Le message d'accueil d'un client dans le chat est envoyé au client."</p>
     *
     * @see ProtocoleChat
     */
    private void informeArrivee() {
        String message;

        message = String.format(ProtocoleChat.MESSAGE_ARRIVEE_CHAT, this.canal.donneNom());
        envoieContacts(message);

        message = String.format(ProtocoleChat.MESSAGE_ACCUEIL_CHAT);
        envoieClient(message);
    }

    /**
     * Traitement d'un message envoyé par le client.
     *
     * @param message le message à traiter.
     */
    private void traiteMessage(String message) {
        if (ProtocoleChat.estUneCommande(message)) {

            switch (ProtocoleChat.commandeDuMessage(message)) {

                case ProtocoleChat.COMMANDE_CHANGE_SURNOM_CLIENT:
                    changeSurnom(ProtocoleChat.parametreCommande(message));
                    break;
                case ProtocoleChat.COMMANDE_CHANGE_CANAL_CLIENT:
                    demandeChangementCanal(ProtocoleChat.parametreCommande(message));
                    break;
                case ProtocoleChat.COMMANDE_AJOUTE_CANAL:
                    demandeAjoutCanal(ProtocoleChat.parametreCommande(message));
                    break;
                case ProtocoleChat.COMMANDE_SUPPRIME_CANAL:
                    demandeSuppressionCanal(ProtocoleChat.parametreCommande(message));
                    break;
                case ProtocoleChat.COMMANDE_AFFICHE_CANAUX:
                    afficheInformationsCanaux();
                    break;
                case ProtocoleChat.COMMANDE_AFFICHE_CLIENT:
                    afficheInformations();
                    break;

                default:
                    afficheAide();
                    break;
            }
        } else {
            // Si le message n'est pas une commande,
            // le message est à transmettre aux clients du canal de l'émetteur.
            this.envoieCanal(String.format(ProtocoleChat.MESSAGE_PREFIXE_MESSAGE, this.surnom, message));
        }
    }

    /**
     * Change le surnom d'un client.
     *
     * <p>Le nouveau surnom n'est pas contraint.</p>
     * <p>Le message de changement de surnom du client est émis dans le canal du client.</p>
     *
     * @param surnom le nouveau surnom.
     * @see ProtocoleChat
     */
    private void changeSurnom(String surnom) {
        final String ancienSurnom = this.surnom;
        this.surnom = surnom;

        String message = String.format(ProtocoleChat.MESSAGE_CHANGEMENT_SURNOM, ancienSurnom, this.surnom);
        envoieCanal(message);
    }

    /**
     * Demande un changement de canal (au service du chat).
     *
     * <p>Si la demande est acceptée, @changeCanal est appelée en <i>callback</i>.</p>
     *
     * @param nom le nom du canal concerné.
     */
    private void demandeChangementCanal(String nom) {
        try {
            this.chat.changeCanalClient(this, nom);
        } catch (CanalChat.Exception e) {
            envoieClient(e.getMessage());
        }
    }

    /**
     * Change le canal du client.
     *
     * <p>Un message de départ du client est émis dans le canal que quitte le client.</p>
     * <p>Un message d'arrivée du client est émis dans le canal que rejoint le client.</p>
     *
     * @param canal le nouveau canal du client.
     */
    public void changeCanal(CanalChat canal) {
        String message;

        synchronized (this) {
            if (canal != null) {
                message = String.format(ProtocoleChat.MESSAGE_DEPART_CANAL,
                        this.surnom, this.canal.donneNom());
                envoieCanal(message);

                this.canal.enleveClient(this);
                this.canal = canal;
                this.canal.ajouteClient(this);

                message = String.format(ProtocoleChat.MESSAGE_ARRIVEE_CANAL,
                        this.surnom, this.canal.donneNom());
                envoieCanal(message);
            }
        }
    }

    /**
     * Demande un ajout de canal (au service du chat).
     *
     * @param nom le nom du canal concerné.
     */
    private void demandeAjoutCanal(String nom) {
        try {
            this.chat.ajouteCanal(nom);
            envoieClient(String.format(ProtocoleChat.MESSAGE_CREATION_CANAL, nom));
        } catch (CanalChat.Exception e) {
            envoieClient(e.getMessage());
        }
    }

    /**
     * Demande une suppression de canal (au service du chat).
     *
     * @param nom le nom du canal concerné.
     */
    private void demandeSuppressionCanal(String nom) {
        try {
            this.chat.supprimeCanal(nom);
            envoieClient(String.format(ProtocoleChat.MESSAGE_SUPPRESSION_CANAL, nom));
        } catch (CanalChat.Exception e) {
            envoieClient(e.getMessage());
        }
    }

    /**
     * Envoyer un message à un client.
     *
     * @param message le message à envoyer.
     */
    void envoieClient(String message) {
        try {
            this.connexion.ecrire(message);
        } catch (IOException ex) {
            System.err.println("Problème d'envoi d'un message à un client - id : " + this.id);
            System.err.println(ex.getMessage());
        }
    }

    /**
     * Transmet un message à tous les contacts d'un client du chat (les clients du même canal).
     *
     * <p>Le message n'est pas envoyé au client mais seulement à ses contacts sur le canal.</p>
     *
     * @param message le message à envoyer.
     */
    private void envoieContacts(String message) {
        // Synchronisation :
        // Pour éviter qu'un client ne soit supprimé du canal lors de l'envoi.
        synchronized (this) {
            this.canal.envoieContacts(this, message);
        }
    }

    /**
     * Envoie d'un message sur le canal d'un client.
     *
     * @param message le message à envoyer.
     */
    private void envoieCanal(String message) {
        // Synchronisation :
        // Pour éviter qu'un client ne soit supprimé du canal lors de l'envoi.
        synchronized (this) {
            this.canal.envoieClients(message);
        }
    }

    /**
     * Fermeture de la connexion du client.
     */
    private void fermeConnexion() {
        System.out.println("Fermeture connexion client (id : " + this.id + ").");
        System.out.flush();

        // Information de déconnexion du client.
        informeDepart();

        // Fermeture de la connexion du client.
        // Synchronisation :
        // Pour éviter qu'une connexion soit fermée lors de l'envoi d'un message sur le canal du client.
        synchronized (this) {
            // Suppression du client dans le canal.
            this.canal.enleveClient(this);

            // Fermeture de la connexion.
            this.connexion.ferme();
        }
    }

    /**
     * Informe du départ du client du chat.
     *
     * <p>Le message de départ d'un client du chat est envoyé aux clients du canal du client partant.</p>
     *
     * @see ProtocoleChat
     */
    private void informeDepart() {
        final String message = String.format(ProtocoleChat.MESSAGE_DEPART_CHAT, this.surnom);
        envoieContacts(message);
    }

    /**
     * Afficher les informations sur les canaux du chat.
     *
     * <p>Les informations sur les canaux du chat sont émises au client à l'origine de la requête.</p>
     */
    private void afficheInformationsCanaux() {
        envoieClient(this.chat.donneInformationsCanaux());
    }

    /**
     * Afficher les informations personnelles sur le client.
     *
     * <p>Les informations personnelles du client sont émises au client à l'origine de la requête.</p>
     *
     * @see ProtocoleChat
     */
    private void afficheInformations() {
        final String message = String.format(ProtocoleChat.MESSAGE_INFORMATIONS_PERSONNELLES,
                this.surnom, this.canal.donneNom());
        envoieClient(message);
    }

    /**
     * Afficher l'aide sur les commandes (et services) disponibles dans le chat.
     */
    private void afficheAide() {
        final String message = String.format(ProtocoleChat.MESSAGE_AIDE);
        envoieClient(message);
    }
}
