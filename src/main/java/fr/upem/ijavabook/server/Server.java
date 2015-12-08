package fr.upem.ijavabook.server;

import fr.upem.ijavabook.exmanager.ExerciseService;

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
    void stop() throws IllegalAccessException;

    ExerciseService getExerciceManager();
}
