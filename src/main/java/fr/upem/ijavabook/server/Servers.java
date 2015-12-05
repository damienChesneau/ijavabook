package fr.upem.ijavabook.server;

/**
 * Static factory class.
 *
 * @author Damien Chesneau - contact@damienchesneau.fr
 */
public class Servers {
    /**
     * Port of the server listening.
     */
    static final int SERVER_PORT = 8989;
    private static final Server SERVER_INSTANCE = new ServerImpl(); // Must be modify !!!

    private Servers() {
    }

    /**
     * Get server controls implementation.
     *
     * @return a singleton of server controls.
     */
    public static Server getServer() {
        return SERVER_INSTANCE;
    }

}
