package fr.upem.ijavabook.server;

import java.nio.file.Path;

/**
 * Static factory class for create server.
 *
 * @author Damien Chesneau
 */
public class Servers {
    /**
     * Port of the server listening.
     */
    static final int SERVER_PORT = 8989;

    private Servers() {
    }

    /**
     * Get server controls implementation.
     * @param rootDirectory Path of folder with all exercises.
     * @return a singleton of server controls.
     */
    public static Server getServer(Path rootDirectory) {
        return new ServerImpl(rootDirectory);
    }

}
