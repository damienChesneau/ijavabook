package fr.upem.ijavabook.server;

import java.nio.file.Path;

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
    private static Server serverInstance;

    private Servers() {
    }

    /**
     * Get server controls implementation.
     *
     * @return a singleton of server controls.
     */
    public static Server getServer(Path rootDirectory) {
        if (serverInstance == null) {
            serverInstance = new ServerImpl(rootDirectory);
        }
        return serverInstance;
    }

}
