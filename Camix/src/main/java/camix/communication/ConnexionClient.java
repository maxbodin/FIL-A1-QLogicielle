package camix.communication;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

/**
 * Classe de connexion réseau avec un client.
 *
 * @author Matthias Brun
 * @version 0.5
 */
public class ConnexionClient {
    /**
     * Socket de connexion avec le client.
     */
    private final Socket socket;

    /**
     * Buffer d'écriture sur le socket de connexion.
     */
    private final BufferedWriter bufferEcriture;

    /**
     * Buffer de lecture du socket de connexion.
     */
    private final BufferedReader bufferLecture;

    /**
     * Constructeur de la connexion avec le client.
     *
     * @param socket le socket de connexion avec le client.
     * @throws IOException exception d'entrée/sortie.
     */
    public ConnexionClient(Socket socket) throws IOException {
        try {
            // Initialisation de la socket.
            this.socket = socket;

            // Initialisation des buffers de lecture et d'écriture sur la socket.
            this.bufferLecture = new BufferedReader(
                    new InputStreamReader(this.socket.getInputStream(), ProtocoleChat.ENCODAGE));
            this.bufferEcriture = new BufferedWriter(
                    new OutputStreamWriter(this.socket.getOutputStream(), ProtocoleChat.ENCODAGE));
        } catch (IOException ex) {
            System.err.println("Problème de connexion avec un client.");
            throw ex;
        }
    }


    /**
     * Envoi d'un message au client.
     *
     * @param message le message à envoyer au client.
     * @throws IOException exception d'entrée/sortie.
     */
    public void ecrire(String message) throws IOException {
        try {
            this.bufferEcriture.write(message, 0, message.length());
            // Ne pas ajouter de caractère \n : celui-ci est mis via le formatage des messages du protocole (cf. ProtocoleChat)
            //this.bufferEcriture.newLine();
            this.bufferEcriture.flush();
        } catch (IOException ex) {
            if (!(ex instanceof SocketException || "Broken pipe".equals(ex.getMessage()))) {
                // Si l'exception n'est pas due à une coupure de connexion du client.
                System.err.println("Problème de connexion avec le client (envoi de message).");
                throw ex;
            }
        }
    }

    /**
     * Réception d'un message du client.
     *
     * @return le message provenant du client.
     * @throws IOException exception d'entrée/sortie.
     */
    public String lire() throws IOException {
        String message = null;

        try {
            message = this.bufferLecture.readLine();
        } catch (IOException ex) {
            if (!(ex instanceof SocketException || "Connection reset".equals(ex.getMessage()))) {
                // Si l'exception n'est pas due à une coupure de connexion du client.
                System.err.println("Problème de connexion avec Felix (lecture de message).");
                throw ex;
            }
        }

        return message;
    }

    /**
     * Fermeture de la connexion.
     * Ferme les buffers et le socket.
     */
    public void ferme() {
        // Fermeture du buffer d'écriture.
        try {
            this.bufferEcriture.close();
        } catch (IOException ex) {
            System.err.println("Problème de fermeture de connexion - buffer écriture : " + this.bufferEcriture);
            System.err.println(ex.getMessage());
        }

        // Fermeture du buffer de lecture.
        try {
            this.bufferLecture.close();
        } catch (IOException ex) {
            System.err.println("Problème de fermeture de connexion - buffer lecture : " + this.bufferLecture);
            System.err.println(ex.getMessage());
        }

        // Fermeture du socket.
        try {
            this.socket.close();
        } catch (IOException ex) {
            System.err.println("Problème de fermeture de connexion - fermeture socket : " + this.socket);
            System.err.println(ex.getMessage());
        }
    }
}
