package fr.upem.ijavabook.server;

/**
 * Do simple action in server.
 *
 * @author Damien Chesneau
 */
public interface Server {
    /**
     * Start server.
     * @return URL to access to server.
     */
    String start();

    /**
     * Stop server.
     */
    void stop();

}
