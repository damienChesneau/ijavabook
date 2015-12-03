package fr.upem.ijavabook.exmanager;

/**
 * @author Damien Chesneau - contact@damienchesneau.fr
 */
public class Exercises {

    private static final ExerciseImpl EXERCICE_INSTANCE = new ExerciseImpl("/exercise/:id");
    private static final Thread WATCHER_THREAD = EXERCICE_INSTANCE.start();
    private Exercises() {
    }

    /**
     * Hide implementation of a singleton exercise.
     * @return An implementation to get your exercices.
     */
    public static ExerciseService getExerciseSrv() {
        return EXERCICE_INSTANCE;
    }
}
