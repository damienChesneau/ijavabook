package fr.upem.ijavabook.exmanager;

import java.io.IOException;
import java.nio.file.Path;

/**
 * @author Damien Chesneau - contact@damienchesneau.fr
 */
public interface ExerciceService {
    /**
     * @param Filename
     * @return HTML representation.
     */
    String getExercise(Path file) throws IOException;
}
