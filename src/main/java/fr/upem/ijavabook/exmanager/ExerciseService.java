package fr.upem.ijavabook.exmanager;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Observer;

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
    String getExercise(String file, Observer observer);


    /**
     * Start the watcher on the repertory.
     */
    Thread start();
/*
    /**
     * Update the content of an exercise.
     *
     * @param java.nio.file.Path Path to text file.
     * @return

    void updateExercise(Path file);*/

    //List<Path> getAllByDirectory(Path path);
}
