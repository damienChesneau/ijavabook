package fr.upem.ijavabook.exmanager;

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

    /**
     * Remove an observer from the data map.
     * @param observer
     */
    void removeObserver(Observer observer);

    List<Path> getAllByDirectory(Path path);
}
