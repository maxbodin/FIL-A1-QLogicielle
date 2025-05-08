package camix.service;

import java.util.Hashtable;

/**
 * Classe canal du serveur.
 *
 * @author Matthias Brun
 * @version 0.5
 */
public class CanalChat {
    /**
     * Le nom du canal.
     */
    private final String nom;
    /**
     * L'ensemble des clients du canal.
     * La clé de la table de hachage s'appuie sur l'id du client.
     */
    private final Hashtable<String, ClientChat> clients;

    /**
     * Constructeur d'un canal du chat.
     *
     * @param nom le nom du canal.
     */
    public CanalChat(String nom) {
        this.nom = nom;
        this.clients = new Hashtable<>();
    }

    /**
     * Accesseur du nom du canal.
     *
     * @return le nom du canal.
     */
    public String donneNom() {
        return this.nom;
    }

    /**
     * Donne le nombre de clients dans le canal.
     *
     * @return le nombre de clients dans le canal.
     */
    public Integer donneNombreClients() {
        return this.clients.size();
    }

    /**
     * Informe de la présence d'un client dans le canal.
     *
     * @param client le client concerné.
     * @return 'true' si le client est présent, 'false' sinon.
     */
    public Boolean estPresent(ClientChat client) {
        return this.clients.get(client.donneId()) != null;
    }

    /**
     * Ajout d'un client dans le canal.
     *
     * @param client le client à ajouter dans le canal.
     */
    public void ajouteClient(ClientChat client) {
        synchronized (this.clients){
            // Si le client n'est pas déjà dans le canal.
            if (!this.estPresent(client)) {
                this.clients.put(client.donneId(), client);
            }
        }
    }

    /**
     * Suppression d'un client dans le canal.
     *
     * @param client le client à enlever du canal.
     */
    public void enleveClient(ClientChat client) {
        // Si le client est dans le canal.
        if (this.estPresent(client)) {
            this.clients.remove(client.donneId());
        }
    }

    /**
     * Envoi d'un message sur le canal.
     *
     * @param message le message à envoyer.
     */
    public void envoieClients(String message) {
        // Pour chaque client du canal.
        for (String s : this.clients.keySet()) {
            final ClientChat contact = this.clients.get(s);

            // Envoi du message.
            contact.envoieClient(message);
        }
    }

    /**
     * Envoi d'un message aux contacts d'un client sur le canal.
     *
     * <p>Le message n'est pas envoyé au client mais seulement à ses contacts sur le canal.</p>
     *
     * @param client  le client concerné.
     * @param message le message à envoyer.
     */
    public void envoieContacts(ClientChat client, String message) {
        // Pour chaque client du canal.
        for (String s : this.clients.keySet()) {
            final ClientChat contact = this.clients.get(s);

            if (!contact.equals(client)) {
                // Envoi du message si le contact n'est pas le client.
                contact.envoieClient(message);
            }
        }
    }

    /**
     * Exception de gestion d'un canal du chat.
     */
    static public class Exception extends java.lang.Exception {
        public Exception(String message) {
            super(message);
        }
    }

}
