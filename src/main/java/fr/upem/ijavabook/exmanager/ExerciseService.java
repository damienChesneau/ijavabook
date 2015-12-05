package fr.upem.ijavabook.exmanager;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * @author Damien Chesneau - contact@damienchesneau.fr
 */
public interface ExerciseService {
    /**
     * Get content of exercise.
     *
     * @param java.nio.file.Path Path to text file.
     * @return HTML representation.
     */
    String getExercise(String file);


    /**
     * Start the watcher on the repertory.
     */
    void start();
/*
    /**
     * Update the content of an exercise.
     *
     * @param java.nio.file.Path Path to text file.
     * @return

    void updateExercise(Path file);*/

    //List<Path> getAllByDirectory(Path path);
}
