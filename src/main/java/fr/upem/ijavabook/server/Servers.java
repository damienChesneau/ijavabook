package fr.upem.ijavabook.server;

import fr.upem.ijavabook.exmanager.ExerciseService;

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
    public static Server getServer(Path rootDirectory) throws IllegalAccessException {
        if (serverInstance == null) {
            serverInstance = new ServerImpl(rootDirectory);
            serverInstance.getExerciceManager().startWatcher();
        }
        return serverInstance;
    }

    public static ExerciseService getExerciceManager(){
        return serverInstance.getExerciceManager();
    }

}
