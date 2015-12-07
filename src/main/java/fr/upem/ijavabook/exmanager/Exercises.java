package fr.upem.ijavabook.exmanager;

import java.nio.file.Path;

/**
 * Static factory.
 * @author Damien Chesneau - contact@damienchesneau.fr
 */
public class Exercises {

    //private static ExerciseImpl EXERCISE_INSTANCE;
    private Exercises() {
    }
    /*public static void start(Path rootDirectory){
        if(EXERCISE_INSTANCE == null){
            EXERCISE_INSTANCE = new ExerciseImpl(rootDirectory);
            EXERCISE_INSTANCE.start();
        }
    }*/
    /**
     * Hide implementation of a singleton exercise.
     * @return An implementation to get your exercises.
     */
    public static ExerciseService getExerciseSrv(Path rootDirectory) {
        //return EXERCISE_INSTANCE;
        return new ExerciseImpl(rootDirectory);
    }
}
