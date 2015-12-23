package fr.upem.ijavabook.exmanager;

import fr.upem.ijavabook.server.EventBusSender;

import java.nio.file.Path;

/**
 * Static factory.
 *
 * @author Damien Chesneau
 */
public class Exercises {
    /**
     * No you can't construct...
     * But a wonderful static method is present :)
     */
    private Exercises() {
    }

    /**
     * Hide implementation of exercise service and create a watcher on directory.
     *
     * @param rootDirectory java.nio.file.Path
     * @param eventBusSender fr.upem.ijavabook.server.EventBusSender representing a way to update clients exercise in live !
     * @return An implementation to get your exercises.
     */
    public static ExerciseService getExerciseSrv(Path rootDirectory, EventBusSender eventBusSender) {
        ExerciseImpl exercise = new ExerciseImpl(rootDirectory, eventBusSender);
        exercise.startWatcher();
        return exercise;
    }
}
