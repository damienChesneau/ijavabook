package fr.upem.ijavabook.exmanager;

/**
 * @author Damien Chesneau - contact@damienchesneau.fr
 */
public class Exercises {
    private Exercises() {
    }

    /**
     * Hide implementation of exercise.
     * @return An implementation to get your exercices.
     */
    public static ExerciseService getExerciseSrv() {
        return new ExerciseImpl();
    }
}
