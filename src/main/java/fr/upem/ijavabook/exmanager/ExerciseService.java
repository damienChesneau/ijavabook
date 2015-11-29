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
    String getExercise(Path file);

    /**
     * Update and get the content of an exercise.
     *
     * @param java.nio.file.Path Path to text file.
     * @return HTML representation.
     */
    String updateAndGetExercise(Path file);

    List<Path> getAllByDirectory(Path path);
}
