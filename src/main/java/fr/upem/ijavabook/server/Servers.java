package fr.upem.ijavabook.server;

/**
 * Static factory class.
 * @author Damien Chesneau - contact@damienchesneau.fr
 */
public class Servers {

    static final int SERVER_PORT = 8989;
    private static final Server SERVER_INSTANCE = new ServerImpl();

    private Servers() {
    }

    /**
     * Get server controls implementation.
     *
     * @return a singleton of server controls.
     */
    public static Server getServer(){
        return SERVER_INSTANCE;
    }

}
