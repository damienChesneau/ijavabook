package fr.upem.ijavabook.exmanager;

import fr.upem.ijavabook.server.EventBusSender;

import java.nio.file.Path;

/**
 * Static factory.
 *
 * @author Damien Chesneau
 */
public class Exercises {

    private Exercises() {
    }

    /**
     * Hide implementation of a singleton exercise.
     *
     * @return An implementation to get your exercises.
     */
    public static ExerciseService getExerciseSrv(Path rootDirectory, EventBusSender eventBusSender) {
        return new ExerciseImpl(rootDirectory, eventBusSender);
    }
}
