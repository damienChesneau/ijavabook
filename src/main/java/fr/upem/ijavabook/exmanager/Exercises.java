package fr.upem.ijavabook.exmanager;

import java.nio.file.Path;

/**
 * Static factory.
 * @author Damien Chesneau - contact@damienchesneau.fr
 */
public class Exercises {

    private Exercises() {
    }
    /**
     * Hide implementation of a singleton exercise.
     * @return An implementation to get your exercises.
     */
    public static ExerciseService getExerciseSrv(Path rootDirectory) {
        return new ExerciseImpl(rootDirectory);
    }
}
