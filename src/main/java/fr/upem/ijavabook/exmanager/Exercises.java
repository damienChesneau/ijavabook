package fr.upem.ijavabook.exmanager;

import java.nio.file.Path;

/**
 * @author Damien Chesneau - contact@damienchesneau.fr
 */
public class Exercises {

    private static ExerciseImpl EXERCICE_INSTANCE;
    private static Thread WATCHER_THREAD;
    private Exercises() {
    }
    public static void start(Path rootDirectory){
        if(EXERCICE_INSTANCE == null){
            EXERCICE_INSTANCE = new ExerciseImpl(rootDirectory);
            WATCHER_THREAD = EXERCICE_INSTANCE.start();
        }
    }
    /**
     * Hide implementation of a singleton exercise.
     * @return An implementation to get your exercices.
     */
    public static ExerciseService getExerciseSrv() {
        return EXERCICE_INSTANCE;
    }
}
