package camix.communication;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Classe de connexion du serveur Camix.
 *
 * @author Matthias Brun
 * @version 0.5
 */
public class ConnexionServeur {
    /**
     * Le socket de connexion des nouveaux clients.
     */
    private final ServerSocket socket;

    /**
     * Constructeur de la connexion serveur.
     *
     * @param port le port d'écoute du socket serveur.
     * @throws IOException exception d'entrée/sortie.
     */
    public ConnexionServeur(int port) throws IOException {
        try {
            this.socket = new ServerSocket(port);
        } catch (IOException ex) {
            System.err.print("Problème de création de la socket serveur.");
            throw ex;
        }
    }

    /**
     * Accepte une connexion client.
     *
     * @return le socket de communication avec le client.
     * @throws IOException exception d'entrée/sortie.
     */
    public Socket accepteConnexion() throws IOException {
        return this.socket.accept();
    }


    /**
     * Fermeture de la connexion serveur.
     *
     * @throws IOException exception d'entrée/sortie.
     */
    public void ferme() throws IOException {
        // Fermeture du socket serveur.
        try {
            this.socket.close();
        } catch (IOException ex) {
            System.err.println("Problème de fermeture de la socket " + this.socket);
            throw ex;
        }
    }
}
