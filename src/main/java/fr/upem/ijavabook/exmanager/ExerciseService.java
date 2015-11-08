package fr.upem.ijavabook.exmanager;

import java.io.IOException;
import java.nio.file.Path;

/**
 * @author Damien Chesneau - contact@damienchesneau.fr
 */
public interface ExerciseService {
    /**
     * Get content of exercise.
     * @param java.nio.file.Path Path to text file.
     * @return HTML representation.
     */
    String getExercise(Path file) throws IOException;
}
