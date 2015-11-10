package fr.upem.ijavabook.exmanager;

import java.nio.file.Path;

/**
 * @author Damien Chesneau - contact@damienchesneau.fr
 */
public interface ExerciceService {
    /**
     * @param Filename
     * @return HTML representation.
     */
    String getExercise(String name);

    String getExercise(Path file);
}
