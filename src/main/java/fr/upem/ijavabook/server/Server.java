package fr.upem.ijavabook.server;

/**
 * Do action in server.
 * @author Damien Chesneau - contact@damienchesneau.fr
 */
public interface Server {
    /**
     * Start server.
     */
    String start();

    /**
     * Stop server.
     */
    void stop();
}
