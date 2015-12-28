package fr.upem.ijavabook.server;

/**
 * Do simple action in server.
 *
 * @author Damien Chesneau
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
