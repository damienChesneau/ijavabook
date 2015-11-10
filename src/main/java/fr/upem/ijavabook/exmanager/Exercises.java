package fr.upem.ijavabook.exmanager;

/**
 * @author Damien Chesneau - contact@damienchesneau.fr
 */
public class Exercises {
    private Exercises() {
    }

    public static ExerciceService getExerciseSrv() {
        return new ExerciseImpl();
    }
}
