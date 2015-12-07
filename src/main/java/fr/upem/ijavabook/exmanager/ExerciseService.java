package fr.upem.ijavabook.exmanager;

import java.nio.file.Path;
import java.util.List;
import java.util.Observer;

/**
 *  Interface who allows all markdown files actions.
 * @author Damien Chesneau - contact@damienchesneau.fr
 */
public interface ExerciseService {
    /**
     * Get content of exercise.
     *
     * @param file Path to text file.
     * @return HTML representation.
     */
    String getExercise(Path file, Observer observer);


    /**
     * Start the watcher on the repertory.
     */
    void startWatcher();

    /**
     * Stop the watcher on the repertory.
     */
    void stopWatcher();

    /**
     * Remove an observer from the data map.
     * @param observer an instance of observer.
     */
    void removeObserver(Observer observer);

    /**
     * Get all files and put her in a list.
     * @param path of the directory.
     * @return a list of markdown files.
     */
    List<Path> getAllByDirectory(Path path);
}
